-- 更新MySQL设备状态脚本
-- 根据TDengine的最新数据更新ah_device表中的设备状态字段

-- 更新DEV-CAP-001 (正常状态)
UPDATE ah_device SET 
    rssi = -87,
    lo_ra_snr = 7.8,
    battery_level = 69,
    update_time = '2025-01-16 22:00:00'
WHERE id = 'DEV-CAP-001';

-- 更新DEV-CAP-002 (告警状态)
UPDATE ah_device SET 
    rssi = -87,
    lo_ra_snr = 8.8,
    battery_level = 76,
    update_time = '2025-01-16 22:00:00'
WHERE id = 'DEV-CAP-002';

-- 更新DEV-CAP-003 (未绑定设备)
UPDATE ah_device SET 
    rssi = -79,
    lo_ra_snr = 9.0,
    battery_level = 100,
    update_time = '2025-01-16 18:00:00'
WHERE id = 'DEV-CAP-003';

-- 更新DEV-TRK-001 (正常GPS设备)
UPDATE ah_device SET 
    rssi = -95,
    lo_ra_snr = 7.2,
    battery_level = 81,
    update_time = '2025-01-16 22:00:00'
WHERE id = 'DEV-TRK-001';

-- 更新DEV-TRK-002 (低电量设备)
UPDATE ah_device SET 
    rssi = -102,
    lo_ra_snr = 5.2,
    battery_level = 6,
    update_time = '2025-01-16 22:00:00'
WHERE id = 'DEV-TRK-002';

-- 更新牲畜档案表中的最新指标 (根据绑定关系)
-- 更新ANIMAL-002 (绑定DEV-CAP-001)
UPDATE ah_animal SET 
    latest_temperature = 38.4,
    latest_activity = 3200,
    latest_steps = NULL,
    last_location_lon = NULL,
    last_location_lat = NULL,
    update_time = '2025-01-16 22:00:00'
WHERE id = 'ANIMAL-002';

-- 更新ANIMAL-003 (绑定DEV-CAP-002, 告警状态)
UPDATE ah_animal SET 
    latest_temperature = 39.7,
    latest_activity = 2000,
    latest_steps = NULL,
    last_location_lon = NULL,
    last_location_lat = NULL,
    health_status = 'ALARM',
    health_score = 55,
    ai_conclusion = '体温持续偏高，疑似感染，请立即检查！',
    update_time = '2025-01-16 22:00:00'
WHERE id = 'ANIMAL-003';

-- 更新ANIMAL-001 (绑定DEV-TRK-001)
UPDATE ah_animal SET 
    latest_temperature = NULL,
    latest_activity = NULL,
    latest_steps = 15400,
    last_location_lon = 119.758234,
    last_location_lat = 49.217345,
    update_time = '2025-01-16 22:00:00'
WHERE id = 'ANIMAL-001';

-- 由于ANIMAL-002同时绑定了DEV-TRK-002，需要更新GPS信息
UPDATE ah_animal SET 
    latest_steps = 8200,
    last_location_lon = 119.765234,
    last_location_lat = 49.223345
WHERE id = 'ANIMAL-002';

-- 验证更新结果
SELECT 
    a.id as animal_id,
    a.ear_tag_id,
    a.health_status,
    a.health_score,
    a.latest_temperature,
    a.latest_activity,
    a.latest_steps,
    a.last_location_lon,
    a.last_location_lat,
    a.ai_conclusion
FROM ah_animal a
ORDER BY a.id;

-- 验证设备状态
SELECT 
    d.id,
    d.name,
    d.device_type,
    d.status,
    d.battery_level,
    d.rssi,
    d.lo_ra_snr,
    d.update_time
FROM ah_device d
ORDER BY d.id; 