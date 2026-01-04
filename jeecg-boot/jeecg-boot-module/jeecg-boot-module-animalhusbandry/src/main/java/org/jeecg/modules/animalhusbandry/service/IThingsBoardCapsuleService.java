package org.jeecg.modules.animalhusbandry.service;

import org.jeecg.common.api.vo.Result;

public interface IThingsBoardCapsuleService {
    Result<?> getLatestTelemetry(String tbDeviceId);
} 