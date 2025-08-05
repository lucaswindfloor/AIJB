package org.jeecg.modules.animalhusbandry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Description: RPC远程指令DTO
 * @Author: Gemini
 * @Date:   2024-08-23
 * @Version: V1.0
 */
@Data
@Schema(description = "RPC远程指令DTO")
public class RpcCommandDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "目标设备ID", required = true)
    private String deviceId;

    @Schema(description = "要执行的方法名 (例如: reboot, setConfig)", required = true)
    private String method;

    @Schema(description = "指令的超时时间 (毫秒)")
    private Long timeout;

    @Schema(description = "指令参数 (键值对)")
    private Map<String, Object> params;
} 