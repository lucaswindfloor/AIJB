package org.jeecg.modules.animalhusbandry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * @Description: 创建维保任务DTO
 * @Author: Gemini
 * @Date:   2024-08-23
 * @Version: V1.0
 */
@Schema(description = "创建维保任务DTO")
public class MaintenanceTaskDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "目标设备ID", required = true)
    private String deviceId;

    @Schema(description = "任务类型 (例如: a_battery_replacement, a_firmware_upgrade)", required = true)
    private String taskType;

    @Schema(description = "指派给的用户ID")
    private String assigneeId;

    @Schema(description = "备注说明")
    private String notes;
    
    // --- Manual getters and setters ---

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
} 