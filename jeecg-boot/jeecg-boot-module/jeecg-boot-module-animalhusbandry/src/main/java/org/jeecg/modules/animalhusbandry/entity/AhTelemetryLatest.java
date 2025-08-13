package org.jeecg.modules.animalhusbandry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 最新遥测数据快照表
 * @Author: Gemini
 * @Date:   2024-08-28
 * @Version: V1.0
 */
@Data
@TableName("ah_telemetry_latest")
public class AhTelemetryLatest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**设备ID (关联ah_device.id)*/
    @Excel(name = "设备ID", width = 15)
    private String deviceId;

    /**遥测数据 (JSON格式)*/
    @Excel(name = "遥测数据", width = 15)
    private String telemetryData;

    /**最后更新时间*/
    @Excel(name = "最后更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;
}