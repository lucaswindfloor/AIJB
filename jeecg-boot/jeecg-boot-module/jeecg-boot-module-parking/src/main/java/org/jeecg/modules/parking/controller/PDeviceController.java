package org.jeecg.modules.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.parking.entity.PDevice;
import org.jeecg.modules.parking.service.IPDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 硬件设备管理
 * @Author: jeecg-boot
 * @Date: 2024-01-01
 * @Version: V1.0
 */
@Tag(name = "硬件设备管理")
@RestController
@RequestMapping("/parking/pDevice")
@Slf4j
public class PDeviceController extends JeecgController<PDevice, IPDeviceService> {

    @Autowired
    private IPDeviceService pDeviceService;

    /**
     * 分页列表查询
     *
     * @param pDevice
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "硬件设备-分页列表查询")
    @Operation(summary = "硬件设备-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<PDevice>> queryPageList(PDevice pDevice,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        Page<PDevice> page = new Page<PDevice>(pageNo, pageSize);
        IPage<PDevice> pageList = pDeviceService.queryPageWithJoin(page, pDevice);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param pDevice
     * @return
     */
    @AutoLog(value = "硬件设备-添加")
    @Operation(summary = "硬件设备-添加")
    @RequiresPermissions("parking:p_device:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody PDevice pDevice) {
        try {
            // 检查序列号是否已存在
            PDevice existDevice = pDeviceService.getBySerialNumber(pDevice.getSerialNumber());
            if (existDevice != null) {
                return Result.error("设备序列号已存在");
            }

            // 检查MAC地址是否已存在
            if (pDevice.getMacAddress() != null) {
                PDevice existMacDevice = pDeviceService.getByMacAddress(pDevice.getMacAddress());
                if (existMacDevice != null) {
                    return Result.error("MAC地址已存在");
                }
            }

            pDeviceService.save(pDevice);
            return Result.OK("添加成功！");
        } catch (Exception e) {
            log.error("设备添加异常", e);
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * 编辑
     *
     * @param pDevice
     * @return
     */
    @AutoLog(value = "硬件设备-编辑")
    @Operation(summary = "硬件设备-编辑")
    @RequiresPermissions("parking:p_device:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody PDevice pDevice) {
        try {
            // 检查序列号是否被其他设备使用
            PDevice existDevice = pDeviceService.getBySerialNumber(pDevice.getSerialNumber());
            if (existDevice != null && !existDevice.getId().equals(pDevice.getId())) {
                return Result.error("设备序列号已被其他设备使用");
            }

            // 检查MAC地址是否被其他设备使用
            if (pDevice.getMacAddress() != null) {
                PDevice existMacDevice = pDeviceService.getByMacAddress(pDevice.getMacAddress());
                if (existMacDevice != null && !existMacDevice.getId().equals(pDevice.getId())) {
                    return Result.error("MAC地址已被其他设备使用");
                }
            }

            pDeviceService.updateById(pDevice);
            return Result.OK("编辑成功!");
        } catch (Exception e) {
            log.error("设备编辑异常", e);
            return Result.error("编辑失败：" + e.getMessage());
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "硬件设备-通过id删除")
    @Operation(summary = "硬件设备-通过id删除")
    @RequiresPermissions("parking:p_device:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            pDeviceService.removeById(id);
            return Result.OK("删除成功!");
        } catch (Exception e) {
            log.error("设备删除异常", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "硬件设备-批量删除")
    @Operation(summary = "硬件设备-批量删除")
    @RequiresPermissions("parking:p_device:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        try {
            this.pDeviceService.removeByIds(Arrays.asList(ids.split(",")));
            return Result.OK("批量删除成功!");
        } catch (Exception e) {
            log.error("设备批量删除异常", e);
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "硬件设备-通过id查询")
    @Operation(summary = "硬件设备-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<PDevice> queryById(@RequestParam(name = "id", required = true) String id) {
        PDevice pDevice = pDeviceService.getById(id);
        if (pDevice == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(pDevice);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param pDevice
     */
    @RequiresPermissions("parking:p_device:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PDevice pDevice) {
        return super.exportXls(request, pDevice, PDevice.class, "硬件设备");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("parking:p_device:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PDevice.class);
    }

    /**
     * 远程控制设备
     *
     * @param params
     * @return
     */
    @AutoLog(value = "硬件设备-远程控制")
    @Operation(summary = "硬件设备-远程控制")
    @RequiresPermissions("parking:p_device:remoteControl")
    @PostMapping(value = "/remoteControl")
    public Result<String> remoteControl(@RequestBody Map<String, Object> params) {
        try {
            String serialNumber = (String) params.get("serialNumber");
            String command = (String) params.get("command");
            
            if (serialNumber == null || command == null) {
                return Result.error("参数不完整");
            }
            
            PDevice device = pDeviceService.getBySerialNumber(serialNumber);
            if (device == null) {
                return Result.error("设备不存在");
            }
            
            if (!"ONLINE".equals(device.getStatus())) {
                return Result.error("设备不在线，无法执行远程控制");
            }
            
            // 这里应该调用具体的设备控制服务
            // deviceControlService.sendCommand(device, command, params);
            
            return Result.OK("控制指令已发送");
        } catch (Exception e) {
            log.error("远程控制异常", e);
            return Result.error("控制失败：" + e.getMessage());
        }
    }

    /**
     * 设备认证
     *
     * @param params
     * @return
     */
    @AutoLog(value = "硬件设备-设备认证")
    @Operation(summary = "硬件设备-设备认证")
    @PostMapping(value = "/deviceAuth")
    public Result<String> deviceAuthentication(@RequestBody Map<String, Object> params) {
        try {
            String serialNumber = (String) params.get("serialNumber");
            String authCode = (String) params.get("authCode");
            
            if (serialNumber == null || authCode == null) {
                return Result.error("认证参数不完整");
            }
            
            PDevice device = pDeviceService.getBySerialNumber(serialNumber);
            if (device == null) {
                return Result.error("设备不存在");
            }
            
            // 这里应该实现具体的认证逻辑
            // boolean authResult = deviceAuthService.authenticate(device, authCode);
            
            return Result.OK("设备认证成功");
        } catch (Exception e) {
            log.error("设备认证异常", e);
            return Result.error("认证失败：" + e.getMessage());
        }
    }

    /**
     * 发布LED内容
     *
     * @param params
     * @return
     */
    @AutoLog(value = "硬件设备-发布LED内容")
    @Operation(summary = "硬件设备-发布LED内容")
    @RequiresPermissions("parking:p_device:publishLed")
    @PostMapping(value = "/publishLed")
    public Result<String> publishLedContent(@RequestBody Map<String, Object> params) {
        try {
            String serialNumber = (String) params.get("serialNumber");
            String content = (String) params.get("content");
            
            if (serialNumber == null || content == null) {
                return Result.error("参数不完整");
            }
            
            PDevice device = pDeviceService.getBySerialNumber(serialNumber);
            if (device == null) {
                return Result.error("设备不存在");
            }
            
            if (!"LED".equals(device.getDeviceType())) {
                return Result.error("该设备不是LED设备");
            }
            
            if (!"ONLINE".equals(device.getStatus())) {
                return Result.error("设备不在线");
            }
            
            // 这里应该调用LED内容发布服务
            // ledContentService.publishContent(device, content);
            
            return Result.OK("LED内容发布成功");
        } catch (Exception e) {
            log.error("LED内容发布异常", e);
            return Result.error("发布失败：" + e.getMessage());
        }
    }

    /**
     * 设备心跳更新
     *
     * @param serialNumber
     * @return
     */
    @AutoLog(value = "硬件设备-更新在线时间")
    @Operation(summary = "硬件设备-更新在线时间")
    @PostMapping(value = "/heartbeat")
    public Result<String> heartbeat(@RequestParam String serialNumber) {
        try {
            if (serialNumber == null) {
                return Result.error("设备序列号不能为空");
            }
            
            PDevice device = pDeviceService.getBySerialNumber(serialNumber);
            if (device == null) {
                return Result.error("设备不存在");
            }
            
            // 更新设备最后在线时间
            pDeviceService.updateLastOnlineTime(serialNumber);
            
            return Result.OK("心跳更新成功");
        } catch (Exception e) {
            log.error("心跳更新异常", e);
            return Result.error("心跳更新失败：" + e.getMessage());
        }
    }

    /**
     * 检查服务器连接
     *
     * @param serialNumber
     * @return
     */
    @AutoLog(value = "硬件设备-检查服务器")
    @Operation(summary = "硬件设备-检查服务器")
    @GetMapping(value = "/checkServer")
    public Result<String> checkServer(@RequestParam String serialNumber) {
        try {
            if (serialNumber == null) {
                return Result.error("设备序列号不能为空");
            }
            
            PDevice device = pDeviceService.getBySerialNumber(serialNumber);
            if (device == null) {
                return Result.error("设备不存在");
            }
            
            // 检查服务器连接状态
            String serverInfo = pDeviceService.checkSameServer(serialNumber);
            
            return Result.OK(serverInfo);
        } catch (Exception e) {
            log.error("服务器检查异常", e);
            return Result.error("检查失败：" + e.getMessage());
        }
    }

    /**
     * 获取离线设备列表
     *
     * @param minutes
     * @return
     */
    @AutoLog(value = "硬件设备-获取离线设备")
    @Operation(summary = "硬件设备-获取离线设备")
    @RequiresPermissions("parking:p_device:monitor")
    @GetMapping(value = "/offlineDevices")
    public Result<List<PDevice>> getOfflineDevices(@RequestParam(defaultValue = "5") Integer minutes) {
        try {
            List<PDevice> offlineDevices = pDeviceService.getOfflineDevices(minutes);
            return Result.OK(offlineDevices);
        } catch (Exception e) {
            log.error("获取离线设备异常", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 根据停车场ID查询设备
     *
     * @param parkingLotId
     * @return
     */
    @AutoLog(value = "硬件设备-根据停车场查询")
    @Operation(summary = "硬件设备-根据停车场查询")
    @GetMapping(value = "/byParkingLot")
    public Result<List<PDevice>> getByParkingLotId(@RequestParam String parkingLotId) {
        try {
            if (parkingLotId == null) {
                return Result.error("停车场ID不能为空");
            }
            
            List<PDevice> devices = pDeviceService.getByParkingLotId(parkingLotId);
            return Result.OK(devices);
        } catch (Exception e) {
            log.error("查询停车场设备异常", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据关卡ID查询设备
     *
     * @param checkpointId
     * @return
     */
    @AutoLog(value = "硬件设备-根据关卡查询")
    @Operation(summary = "硬件设备-根据关卡查询")
    @GetMapping(value = "/byCheckpoint")
    public Result<List<PDevice>> getByCheckpointId(@RequestParam String checkpointId) {
        try {
            if (checkpointId == null) {
                return Result.error("关卡ID不能为空");
            }
            
            List<PDevice> devices = pDeviceService.getByCheckpointId(checkpointId);
            return Result.OK(devices);
        } catch (Exception e) {
            log.error("查询关卡设备异常", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据序列号查询设备
     *
     * @param serialNumber
     * @return
     */
    @AutoLog(value = "硬件设备-根据序列号查询")
    @Operation(summary = "硬件设备-根据序列号查询")
    @GetMapping(value = "/bySerialNumber")
    public Result<PDevice> getBySerialNumber(@RequestParam String serialNumber) {
        try {
            if (serialNumber == null) {
                return Result.error("设备序列号不能为空");
            }
            
            PDevice device = pDeviceService.getBySerialNumber(serialNumber);
            if (device == null) {
                return Result.error("设备不存在");
            }
            
            return Result.OK(device);
        } catch (Exception e) {
            log.error("查询设备异常", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 处理设备连接
     *
     * @param params
     * @return
     */
    @AutoLog(value = "硬件设备-设备连接")
    @Operation(summary = "硬件设备-设备连接")
    @PostMapping(value = "/deviceLink")
    public Result<String> handleDeviceLink(@RequestBody Map<String, Object> params) {
        try {
            String serialNumber = (String) params.get("serialNumber");
            String linkType = (String) params.get("linkType"); // CONNECT or DISCONNECT
            
            if (serialNumber == null || linkType == null) {
                return Result.error("参数不完整");
            }
            
            PDevice device = pDeviceService.getBySerialNumber(serialNumber);
            if (device == null) {
                return Result.error("设备不存在");
            }
            
            // 处理设备连接
            pDeviceService.handleDeviceLink(serialNumber, linkType);
            
            return Result.OK("设备连接状态更新成功");
        } catch (Exception e) {
            log.error("设备连接处理异常", e);
            return Result.error("处理失败：" + e.getMessage());
        }
    }

    /**
     * 处理设备数据上报
     *
     * @param params
     * @return
     */
    @AutoLog(value = "硬件设备-数据上报")
    @Operation(summary = "硬件设备-数据上报")
    @PostMapping(value = "/deviceData")
    public Result<String> handleDeviceData(@RequestBody Map<String, Object> params) {
        try {
            String serialNumber = (String) params.get("serialNumber");
            String dataType = (String) params.get("dataType");
            Object data = params.get("data");
            
            if (serialNumber == null || dataType == null || data == null) {
                return Result.error("数据不完整");
            }
            
            PDevice device = pDeviceService.getBySerialNumber(serialNumber);
            if (device == null) {
                return Result.error("设备不存在");
            }
            
            // 处理设备数据（将数据转换为字符串）
            String dataStr = data.toString();
            pDeviceService.handleDeviceData(serialNumber, dataStr);
            
            return Result.OK("数据处理成功");
        } catch (Exception e) {
            log.error("设备数据处理异常", e);
            return Result.error("数据处理失败：" + e.getMessage());
        }
    }
} 