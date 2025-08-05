-- MySQL更新最新遥测数据脚本
-- 将TDengine中的最新数据同步到MySQL的ah_telemetry_latest表

-- 首先清空现有数据
DELETE FROM ah_telemetry_latest;

-- 插入DEV-CAP-001的最新数据 (正常状态)
INSERT INTO ah_telemetry_latest (id, device_id, telemetry_data, last_update_time) VALUES 
(UUID(), 'DEV-CAP-001', JSON_OBJECT(
    'temperature', 38.4,
    'activity_level', 3200,
    'ph_value', 6.8,
    'battery_level', 69,
    'rssi', -87,
    'snr', 7.8
), '2025-01-16 22:00:00');

-- 插入DEV-CAP-002的最新数据 (告警状态 - 高体温)
INSERT INTO ah_telemetry_latest (id, device_id, telemetry_data, last_update_time) VALUES 
(UUID(), 'DEV-CAP-002', JSON_OBJECT(
    'temperature', 39.7,
    'activity_level', 2000,
    'ph_value', 6.4,
    'battery_level', 76,
    'rssi', -87,
    'snr', 8.8
), '2025-01-16 22:00:00');

-- 插入DEV-CAP-003的最新数据 (未绑定设备)
INSERT INTO ah_telemetry_latest (id, device_id, telemetry_data, last_update_time) VALUES 
(UUID(), 'DEV-CAP-003', JSON_OBJECT(
    'temperature', 38.4,
    'activity_level', 0,
    'ph_value', 6.9,
    'battery_level', 100,
    'rssi', -79,
    'snr', 9.0
), '2025-01-16 18:00:00');

-- 插入DEV-TRK-001的最新数据 (正常GPS设备)
INSERT INTO ah_telemetry_latest (id, device_id, telemetry_data, last_update_time) VALUES 
(UUID(), 'DEV-TRK-001', JSON_OBJECT(
    'steps', 15400,
    'longitude', 119.758234,
    'latitude', 49.217345,
    'battery_level', 81,
    'rssi', -95,
    'snr', 7.2
), '2025-01-16 22:00:00');

-- 插入DEV-TRK-002的最新数据 (低电量设备)
INSERT INTO ah_telemetry_latest (id, device_id, telemetry_data, last_update_time) VALUES 
(UUID(), 'DEV-TRK-002', JSON_OBJECT(
    'steps', 8200,
    'longitude', 119.765234,
    'latitude', 49.223345,
    'battery_level', 6,
    'rssi', -102,
    'snr', 5.2
), '2025-01-16 22:00:00');

-- 验证插入结果
SELECT 
    device_id,
    JSON_EXTRACT(telemetry_data, '$.temperature') as temperature,
    JSON_EXTRACT(telemetry_data, '$.battery_level') as battery_level,
    JSON_EXTRACT(telemetry_data, '$.steps') as steps,
    last_update_time
FROM ah_telemetry_latest
ORDER BY device_id; 