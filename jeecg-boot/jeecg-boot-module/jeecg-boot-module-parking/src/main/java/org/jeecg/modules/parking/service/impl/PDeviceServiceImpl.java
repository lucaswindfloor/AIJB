package org.jeecg.modules.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.parking.entity.PDevice;
import org.jeecg.modules.parking.mapper.PDeviceMapper;
import org.jeecg.modules.parking.service.IPDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: 硬件设备管理
 * @Author: jeecg-boot
 * @Date: 2024-01-01
 * @Version: V1.0
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class PDeviceServiceImpl extends ServiceImpl<PDeviceMapper, PDevice> implements IPDeviceService {

    @Autowired
    private PDeviceMapper pDeviceMapper;

    @Autowired
    private RedisUtil redisUtil;

    // Redis key前缀
    private static final String DEVICE_ONLINE_KEY_PREFIX = "device:online:";
    private static final String DEVICE_CONTROL_KEY_PREFIX = "device:control:";

    @Override
    public IPage<PDevice> queryPageWithJoin(Page<PDevice> page, PDevice pDevice) {
        QueryWrapper<PDevice> queryWrapper = buildQueryWrapper(pDevice);
        return pDeviceMapper.selectDevicePageWithJoin(page, queryWrapper);
    }

    @Override
    public List<PDevice> queryListWithJoin(PDevice pDevice) {
        QueryWrapper<PDevice> queryWrapper = buildQueryWrapper(pDevice);
        return pDeviceMapper.selectDeviceListWithJoin(queryWrapper);
    }

    @Override
    public PDevice getBySerialNumber(String serialNumber) {
        if (StringUtils.isBlank(serialNumber)) {
            return null;
        }
        return pDeviceMapper.selectBySerialNumber(serialNumber);
    }

    @Override
    public PDevice getByMacAddress(String macAddress) {
        if (StringUtils.isBlank(macAddress)) {
            return null;
        }
        return pDeviceMapper.selectByMacAddress(macAddress);
    }

    @Override
    public boolean updateLastOnlineTime(String serialNumber) {
        if (StringUtils.isBlank(serialNumber)) {
            return false;
        }
        
        // 更新数据库
        QueryWrapper<PDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("serial_number", serialNumber);
        
        PDevice updateDevice = new PDevice();
        updateDevice.setLastOnlineTime(new Date());
        updateDevice.setStatus("ONLINE");
        
        int result = pDeviceMapper.update(updateDevice, queryWrapper);
        
        // 更新Redis缓存
        if (result > 0) {
            redisUtil.set(DEVICE_ONLINE_KEY_PREFIX + serialNumber, System.currentTimeMillis(), 300); // 5分钟过期
        }
        
        return result > 0;
    }

    @Override
    public int batchUpdateLastOnlineTime(List<String> serialNumbers) {
        if (serialNumbers == null || serialNumbers.isEmpty()) {
            return 0;
        }
        
        // 批量更新数据库
        int result = pDeviceMapper.batchUpdateLastOnlineTime(serialNumbers);
        
        // 批量更新Redis缓存
        if (result > 0) {
            long currentTime = System.currentTimeMillis();
            for (String serialNumber : serialNumbers) {
                redisUtil.set(DEVICE_ONLINE_KEY_PREFIX + serialNumber, currentTime, 300);
            }
        }
        
        return result;
    }

    @Override
    public List<PDevice> getOfflineDevices(int offlineMinutes) {
        return pDeviceMapper.selectOfflineDevices(offlineMinutes);
    }

    @Override
    public List<PDevice> getByParkingLotId(String parkingLotId) {
        if (StringUtils.isBlank(parkingLotId)) {
            return null;
        }
        return pDeviceMapper.selectByParkingLotId(parkingLotId);
    }

    @Override
    public List<PDevice> getByCheckpointId(String checkpointId) {
        if (StringUtils.isBlank(checkpointId)) {
            return null;
        }
        return pDeviceMapper.selectByCheckpointId(checkpointId);
    }

    @Override
    public boolean remoteControl(String serialNumber, String action, String reason) {
        if (StringUtils.isBlank(serialNumber) || StringUtils.isBlank(action)) {
            log.error("设备远程控制参数错误: serialNumber={}, action={}", serialNumber, action);
            return false;
        }

        try {
            // 查询设备信息
            PDevice device = getBySerialNumber(serialNumber);
            if (device == null) {
                log.error("设备不存在: serialNumber={}", serialNumber);
                return false;
            }

            // 检查设备状态
            if (!"ONLINE".equals(device.getStatus())) {
                log.error("设备不在线，无法控制: serialNumber={}, status={}", serialNumber, device.getStatus());
                return false;
            }

            // 构建控制指令
            String controlCommand = buildControlCommand(device, action, reason);
            
            // 发送控制指令（这里模拟发送，实际项目中需要集成硬件SDK）
            boolean sendResult = sendControlCommand(device, controlCommand);
            
            if (sendResult) {
                // 记录控制日志到Redis
                String logKey = DEVICE_CONTROL_KEY_PREFIX + serialNumber + ":" + System.currentTimeMillis();
                String logValue = String.format("{\"action\":\"%s\",\"reason\":\"%s\",\"time\":\"%s\"}", 
                    action, reason, new Date());
                redisUtil.set(logKey, logValue, 86400); // 保存24小时
                
                log.info("设备远程控制成功: serialNumber={}, action={}, reason={}", serialNumber, action, reason);
                return true;
            }
            
        } catch (Exception e) {
            log.error("设备远程控制异常: serialNumber={}, action={}", serialNumber, action, e);
        }
        
        return false;
    }

    @Override
    public boolean deviceAuthentication(String serialNumber, String username, String password) {
        if (StringUtils.isAnyBlank(serialNumber, username, password)) {
            return false;
        }

        QueryWrapper<PDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("serial_number", serialNumber)
                   .eq("username", username)
                   .eq("password", password); // 实际项目中密码应该是加密的

        PDevice device = pDeviceMapper.selectOne(queryWrapper);
        return device != null;
    }

    @Override
    public boolean publishLedContent(String serialNumber, String content, Integer duration) {
        if (StringUtils.isAnyBlank(serialNumber, content) || duration == null || duration <= 0) {
            log.error("LED内容发布参数错误: serialNumber={}, content={}, duration={}", serialNumber, content, duration);
            return false;
        }

        try {
            PDevice device = getBySerialNumber(serialNumber);
            if (device == null || !"ONLINE".equals(device.getStatus())) {
                log.error("设备不存在或不在线: serialNumber={}", serialNumber);
                return false;
            }

            // 构建LED内容指令
            String ledCommand = buildLedCommand(content, duration);
            
            // 发送LED指令
            boolean result = sendLedCommand(device, ledCommand);
            
            if (result) {
                log.info("LED内容发布成功: serialNumber={}, content={}, duration={}", serialNumber, content, duration);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("LED内容发布异常: serialNumber={}, content={}", serialNumber, content, e);
            return false;
        }
    }

    @Override
    public void monitorDeviceStatus() {
        log.info("开始设备状态监控...");
        
        try {
            // 查询5分钟内未上线的在线设备
            List<PDevice> offlineDevices = getOfflineDevices(5);
            
            if (!offlineDevices.isEmpty()) {
                log.warn("发现离线设备数量: {}", offlineDevices.size());
                
                for (PDevice device : offlineDevices) {
                    // 更新设备状态为离线
                    PDevice updateDevice = new PDevice();
                    updateDevice.setId(device.getId());
                    updateDevice.setStatus("OFFLINE");
                    updateDevice.setUpdateTime(new Date());
                    
                    pDeviceMapper.updateById(updateDevice);
                    
                    // 发送离线通知（可以集成短信、邮件等通知）
                    sendOfflineNotification(device);
                    
                    log.warn("设备离线: serialNumber={}, name={}, checkpointName={}", 
                        device.getSerialNumber(), device.getName(), device.getCheckpointName());
                }
            }
            
        } catch (Exception e) {
            log.error("设备状态监控异常", e);
        }
    }

    @Override
    public String checkSameServer(String serialNumber) {
        try {
            // 这里返回当前服务器信息，用于分布式环境下的设备控制
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.error("获取服务器信息失败", e);
            return "unknown";
        }
    }

    @Override
    public void handleDeviceLink(String serialNumber, String clientInfo) {
        if (StringUtils.isBlank(serialNumber)) {
            return;
        }
        
        log.info("设备连接: serialNumber={}, clientInfo={}", serialNumber, clientInfo);
        
        // 更新设备在线状态
        updateLastOnlineTime(serialNumber);
        
        // 保存连接信息到Redis
        String linkKey = "device:link:" + serialNumber;
        redisUtil.set(linkKey, clientInfo, 3600); // 1小时过期
    }

    @Override
    public void handleDeviceData(String serialNumber, String data) {
        if (StringUtils.isAnyBlank(serialNumber, data)) {
            return;
        }
        
        log.info("设备数据上报: serialNumber={}, dataLength={}", serialNumber, data.length());
        
        try {
            // 更新心跳时间
            updateLastOnlineTime(serialNumber);
            
            // 解析设备数据（这里需要根据实际协议解析）
            // parseDeviceData(serialNumber, data);
            
            // 可以在这里处理车牌识别、人脸识别等数据
            // 然后触发相应的业务流程（如开闸、计费等）
            
        } catch (Exception e) {
            log.error("处理设备数据异常: serialNumber={}", serialNumber, e);
        }
    }

    /**
     * 构建查询条件
     */
    private QueryWrapper<PDevice> buildQueryWrapper(PDevice pDevice) {
        QueryWrapper<PDevice> queryWrapper = new QueryWrapper<>();
        
        if (pDevice != null) {
            if (StringUtils.isNotBlank(pDevice.getName())) {
                queryWrapper.like("d.name", pDevice.getName());
            }
            if (StringUtils.isNotBlank(pDevice.getSerialNumber())) {
                queryWrapper.eq("d.serial_number", pDevice.getSerialNumber());
            }
            if (StringUtils.isNotBlank(pDevice.getDeviceNo())) {
                queryWrapper.like("d.device_no", pDevice.getDeviceNo());
            }
            if (StringUtils.isNotBlank(pDevice.getMacAddress())) {
                queryWrapper.eq("d.mac_address", pDevice.getMacAddress());
            }
            if (StringUtils.isNotBlank(pDevice.getDeviceType())) {
                queryWrapper.eq("d.device_type", pDevice.getDeviceType());
            }
            if (StringUtils.isNotBlank(pDevice.getBrand())) {
                queryWrapper.eq("d.brand", pDevice.getBrand());
            }
            if (StringUtils.isNotBlank(pDevice.getModel())) {
                queryWrapper.like("d.model", pDevice.getModel());
            }
            if (StringUtils.isNotBlank(pDevice.getStatus())) {
                queryWrapper.eq("d.status", pDevice.getStatus());
            }
            if (StringUtils.isNotBlank(pDevice.getCheckpointId())) {
                queryWrapper.eq("d.checkpoint_id", pDevice.getCheckpointId());
            }
            if (StringUtils.isNotBlank(pDevice.getParkingLotId())) {
                queryWrapper.eq("c.parking_lot_id", pDevice.getParkingLotId());
            }
            if (StringUtils.isNotBlank(pDevice.getDirection())) {
                queryWrapper.eq("c.direction", pDevice.getDirection());
            }
        }
        
        queryWrapper.orderByDesc("d.create_time");
        return queryWrapper;
    }

    /**
     * 构建控制指令
     */
    private String buildControlCommand(PDevice device, String action, String reason) {
        // 这里需要根据不同设备品牌构建不同的控制指令
        // 实际项目中应该有设备适配器模式
        return String.format("{\"action\":\"%s\",\"reason\":\"%s\",\"timestamp\":%d}", 
            action, reason, System.currentTimeMillis());
    }

    /**
     * 发送控制指令
     */
    private boolean sendControlCommand(PDevice device, String command) {
        // 这里应该集成具体的硬件SDK
        // 根据设备品牌调用相应的控制接口
        log.info("发送控制指令: device={}, command={}", device.getSerialNumber(), command);
        
        // 模拟发送成功
        return true;
    }

    /**
     * 构建LED指令
     */
    private String buildLedCommand(String content, Integer duration) {
        return String.format("{\"type\":\"led\",\"content\":\"%s\",\"duration\":%d}", content, duration);
    }

    /**
     * 发送LED指令
     */
    private boolean sendLedCommand(PDevice device, String command) {
        log.info("发送LED指令: device={}, command={}", device.getSerialNumber(), command);
        
        // 模拟发送成功
        return true;
    }

    /**
     * 发送离线通知
     */
    private void sendOfflineNotification(PDevice device) {
        // 这里可以集成短信、邮件、企业微信等通知方式
        log.warn("设备离线通知: device={}, checkpoint={}", 
            device.getSerialNumber(), device.getCheckpointName());
    }
} 