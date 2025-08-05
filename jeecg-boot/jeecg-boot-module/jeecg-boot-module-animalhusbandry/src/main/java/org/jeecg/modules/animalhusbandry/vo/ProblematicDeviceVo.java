package org.jeecg.modules.animalhusbandry.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: "有问题"的设备列表的视图对象
 * @Author: Gemini
 * @Date:   2024-08-23
 * @Version: V1.0
 */
@Schema(description = "有问题设备视图对象")
public class ProblematicDeviceVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    private String id;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "设备类型")
    private String deviceType;
    
    @Schema(description = "设备类型字典文本")
    private String deviceTypeDictText;

    @Schema(description = "DevEUI")
    private String devEui;

    @Schema(description = "电量百分比")
    private Integer batteryLevel;

    @Schema(description = "最后在线时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "绑定的牲畜耳标")
    private String animalEarTag;

    @Schema(description = "是否离线标记")
    private boolean isOffline;

    @Schema(description = "是否低电量标记")
    private boolean isLowBattery;

    @Schema(description = "状态文字描述 (根据isOffline和isLowBattery计算)")
    public String getStatusText() {
        if (isOffline) {
            return "离线";
        }
        if (isLowBattery) {
            return "低电量";
        }
        return "在线"; // Should not happen in this query, but as a fallback
    }

    // --- Manual getters and setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceTypeDictText() {
        return deviceTypeDictText;
    }

    public void setDeviceTypeDictText(String deviceTypeDictText) {
        this.deviceTypeDictText = deviceTypeDictText;
    }

    public String getDevEui() {
        return devEui;
    }

    public void setDevEui(String devEui) {
        this.devEui = devEui;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAnimalEarTag() {
        return animalEarTag;
    }

    public void setAnimalEarTag(String animalEarTag) {
        this.animalEarTag = animalEarTag;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public boolean isLowBattery() {
        return isLowBattery;
    }

    public void setLowBattery(boolean lowBattery) {
        isLowBattery = lowBattery;
    }
} 