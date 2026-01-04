-- TDengine 3.x 建表脚本 - 一条一条执行
-- 请复制每一行单独执行

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS animal_husbandry;

-- 2. 使用数据库  
USE animal_husbandry;

-- 3. 创建遥测数据超级表
CREATE STABLE IF NOT EXISTS telemetry_data (ts TIMESTAMP, temperature FLOAT, activity_level INT, ph_value FLOAT, steps INT, longitude DOUBLE, latitude DOUBLE, battery_level INT, rssi INT, snr FLOAT) TAGS (device_id NCHAR(36), device_type NCHAR(20), dev_eui NCHAR(50), animal_id NCHAR(36));

-- 4. 创建设备状态超级表  
CREATE STABLE IF NOT EXISTS device_status (ts TIMESTAMP, status NCHAR(20), battery_level INT, rssi INT, firmware_version NCHAR(50)) TAGS (device_id NCHAR(36), device_type NCHAR(20), dev_eui NCHAR(50));

-- 5. 创建子表
CREATE TABLE IF NOT EXISTS telemetry_DEV_CAP_001 USING telemetry_data TAGS ('DEV-CAP-001', 'CAPSULE', 'EUI-CAP-001', 'ANIMAL-002');

CREATE TABLE IF NOT EXISTS telemetry_DEV_CAP_002 USING telemetry_data TAGS ('DEV-CAP-002', 'CAPSULE', 'EUI-CAP-002', 'ANIMAL-003');

CREATE TABLE IF NOT EXISTS telemetry_DEV_CAP_003 USING telemetry_data TAGS ('DEV-CAP-003', 'CAPSULE', 'EUI-CAP-003', NULL);

CREATE TABLE IF NOT EXISTS telemetry_DEV_TRK_001 USING telemetry_data TAGS ('DEV-TRK-001', 'TRACKER', 'EUI-TRK-001', 'ANIMAL-001');

CREATE TABLE IF NOT EXISTS telemetry_DEV_TRK_002 USING telemetry_data TAGS ('DEV-TRK-002', 'TRACKER', 'EUI-TRK-002', 'ANIMAL-002');

-- 6. 创建设备状态子表
CREATE TABLE IF NOT EXISTS status_DEV_CAP_001 USING device_status TAGS ('DEV-CAP-001', 'CAPSULE', 'EUI-CAP-001');

CREATE TABLE IF NOT EXISTS status_DEV_CAP_002 USING device_status TAGS ('DEV-CAP-002', 'CAPSULE', 'EUI-CAP-002');

CREATE TABLE IF NOT EXISTS status_DEV_CAP_003 USING device_status TAGS ('DEV-CAP-003', 'CAPSULE', 'EUI-CAP-003');

CREATE TABLE IF NOT EXISTS status_DEV_TRK_001 USING device_status TAGS ('DEV-TRK-001', 'TRACKER', 'EUI-TRK-001');

CREATE TABLE IF NOT EXISTS status_DEV_TRK_002 USING device_status TAGS ('DEV-TRK-002', 'TRACKER', 'EUI-TRK-002');

-- 7. 验证
SHOW STABLES;

SHOW TABLES; 