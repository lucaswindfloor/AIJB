package org.jeecg.modules.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 硬件设备管理
 * @Author: jeecg-boot
 * @Date: 2024-01-01
 * @Version: V1.0
 */
@Data
@TableName("p_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name="PDevice对象", description="硬件设备管理")
public class PDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
    
    /**关卡ID*/
    @Excel(name = "关卡ID", width = 15)
    @Schema(description = "关卡ID")
    private String checkpointId;
    
    /**设备名称*/
    @Excel(name = "设备名称", width = 15)
    @Schema(description = "设备名称")
    private String name;
    
    /**设备序列号*/
    @Excel(name = "设备序列号", width = 15)
    @Schema(description = "设备序列号")
    private String serialNumber;
    
    /**设备编号*/
    @Excel(name = "设备编号", width = 15)
    @Schema(description = "设备编号")
    private String deviceNo;
    
    /**MAC地址*/
    @Excel(name = "MAC地址", width = 15)
    @Schema(description = "MAC地址")
    private String macAddress;
    
    /**设备类型*/
    @Excel(name = "设备类型", width = 15, dicCode = "device_type")
    @Dict(dicCode = "device_type")
    @Schema(description = "设备类型")
    private String deviceType;
    
    /**品牌*/
    @Excel(name = "品牌", width = 15, dicCode = "device_brand")
    @Dict(dicCode = "device_brand")
    @Schema(description = "品牌")
    private String brand;
    
    /**型号*/
    @Excel(name = "型号", width = 15)
    @Schema(description = "型号")
    private String model;
    
    /**IP地址*/
    @Excel(name = "IP地址", width = 15)
    @Schema(description = "IP地址")
    private String ipAddress;
    
    /**登录用户名*/
    @Excel(name = "登录用户名", width = 15)
    @Schema(description = "登录用户名")
    private String username;
    
    /**登录密码*/
    @Excel(name = "登录密码", width = 15)
    @Schema(description = "登录密码")
    private String password;
    
    /**视频流地址*/
    @Excel(name = "视频流地址", width = 15)
    @Schema(description = "视频流地址")
    private String streamUrl;
    
    /**设备状态*/
    @Excel(name = "设备状态", width = 15, dicCode = "device_status")
    @Dict(dicCode = "device_status")
    @Schema(description = "设备状态")
    private String status;
    
    /**最后在线时间*/
    @Excel(name = "最后在线时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后在线时间")
    private Date lastOnlineTime;
    
    /**创建人*/
    @Schema(description = "创建人")
    private String createBy;
    
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
    
    /**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
    
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
    
    /**所属部门*/
    @Schema(description = "所属部门")
    private String sysOrgCode;
    
    // 关联查询字段
    
    /**关卡名称 - 关联查询*/
    @TableField(exist = false)
    @Schema(description = "关卡名称")
    private String checkpointName;
    
    /**停车场ID - 通过关卡关联*/
    @TableField(exist = false)
    @Schema(description = "停车场ID")
    private String parkingLotId;
    
    /**停车场名称 - 关联查询*/
    @TableField(exist = false)
    @Schema(description = "停车场名称")
    private String parkingLotName;
    
    /**方向 - 通过关卡关联*/
    @TableField(exist = false)
    @Schema(description = "方向")
    private String direction;
    
    /**品牌名称 - 字典翻译*/
    @TableField(exist = false)
    @Schema(description = "品牌名称")
    private String brandName;
    
    /**设备类型名称 - 字典翻译*/
    @TableField(exist = false)
    @Schema(description = "设备类型名称")
    private String deviceTypeName;
    
    /**状态名称 - 字典翻译*/
    @TableField(exist = false)
    @Schema(description = "状态名称")
    private String statusName;
} 