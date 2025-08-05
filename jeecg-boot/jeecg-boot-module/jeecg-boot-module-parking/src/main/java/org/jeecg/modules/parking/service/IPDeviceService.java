package org.jeecg.modules.parking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.parking.entity.PDevice;

import java.util.List;

/**
 * @Description: 硬件设备管理
 * @Author: jeecg-boot
 * @Date: 2024-01-01
 * @Version: V1.0
 */
public interface IPDeviceService extends IService<PDevice> {

    /**
     * 分页查询设备列表（包含关联信息）
     * 对应旧系统的 selectListByCondition 功能
     * 
     * @param page 分页参数
     * @param pDevice 查询条件
     * @return 分页结果
     */
    IPage<PDevice> queryPageWithJoin(Page<PDevice> page, PDevice pDevice);

    /**
     * 查询设备列表（包含关联信息） - 不分页
     * 用于导出等功能
     * 
     * @param pDevice 查询条件
     * @return 设备列表
     */
    List<PDevice> queryListWithJoin(PDevice pDevice);

    /**
     * 根据序列号查询设备信息
     * 用于远程控制功能
     * 
     * @param serialNumber 设备序列号
     * @return 设备信息
     */
    PDevice getBySerialNumber(String serialNumber);

    /**
     * 根据MAC地址查询设备信息
     * 用于设备注册功能
     * 
     * @param macAddress MAC地址
     * @return 设备信息
     */
    PDevice getByMacAddress(String macAddress);

    /**
     * 更新设备最后在线时间
     * 用于设备心跳监控
     * 
     * @param serialNumber 设备序列号
     * @return 是否更新成功
     */
    boolean updateLastOnlineTime(String serialNumber);

    /**
     * 批量更新设备最后在线时间
     * 
     * @param serialNumbers 设备序列号列表
     * @return 更新的设备数量
     */
    int batchUpdateLastOnlineTime(List<String> serialNumbers);

    /**
     * 查询离线设备列表
     * 用于设备监控报警
     * 
     * @param offlineMinutes 离线分钟数阈值
     * @return 离线设备列表
     */
    List<PDevice> getOfflineDevices(int offlineMinutes);

    /**
     * 根据停车场ID查询设备列表
     * 
     * @param parkingLotId 停车场ID
     * @return 设备列表
     */
    List<PDevice> getByParkingLotId(String parkingLotId);

    /**
     * 根据关卡ID查询设备列表
     * 
     * @param checkpointId 关卡ID
     * @return 设备列表
     */
    List<PDevice> getByCheckpointId(String checkpointId);

    /**
     * 远程控制设备
     * 对应旧系统的开闸/关闸功能
     * 
     * @param serialNumber 设备序列号
     * @param action 操作类型 (open-开闸, close-关闸)
     * @param reason 操作原因
     * @return 控制结果
     */
    boolean remoteControl(String serialNumber, String action, String reason);

    /**
     * 设备认证
     * 对应旧系统的设备登录功能
     * 
     * @param serialNumber 设备序列号
     * @param username 用户名
     * @param password 密码
     * @return 认证结果
     */
    boolean deviceAuthentication(String serialNumber, String username, String password);

    /**
     * 发布LED广告内容
     * 对应旧系统的publishLedAd功能
     * 
     * @param serialNumber 设备序列号
     * @param content 广告内容
     * @param duration 播放时长
     * @return 发布结果
     */
    boolean publishLedContent(String serialNumber, String content, Integer duration);

    /**
     * 设备状态监控
     * 定时任务调用，检查设备在线状态
     */
    void monitorDeviceStatus();

    /**
     * 校验设备是否在同一服务器
     * 用于设备控制前的服务器校验
     * 
     * @param serialNumber 设备序列号
     * @return 服务器信息
     */
    String checkSameServer(String serialNumber);

    /**
     * 处理设备链接
     * WebSocket连接处理
     * 
     * @param serialNumber 设备序列号
     * @param clientInfo 客户端信息
     */
    void handleDeviceLink(String serialNumber, String clientInfo);

    /**
     * 设备数据上报处理
     * 处理设备上报的车牌识别等数据
     * 
     * @param serialNumber 设备序列号
     * @param data 上报数据
     */
    void handleDeviceData(String serialNumber, String data);
} 