-- TDengine时序数据库建表脚本
-- 智能畜牧管理系统 - 遥测数据存储
-- 连接到TDengine后执行此脚本

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS animal_husbandry KEEP 3650d DAYS 30 BLOCKS 6 UPDATE 1;

-- 使用数据库
USE animal_husbandry;

-- 2. 创建遥测数据超级表 (Super Table)
-- 存储所有设备的遥测数据
CREATE STABLE IF NOT EXISTS telemetry_data (
    ts TIMESTAMP,                   -- 时间戳 (主键)
    temperature FLOAT,              -- 体温(℃) - 瘤胃胶囊
    activity_level INT,             -- 活动量 - 瘤胃胶囊
    ph_value FLOAT,                 -- pH值 - 瘤胃胶囊
    steps INT,                      -- 步数 - 追踪器
    longitude DOUBLE,               -- 经度 - 追踪器
    latitude DOUBLE,                -- 纬度 - 追踪器
    battery_level INT,              -- 电量百分比 - 通用
    rssi INT,                       -- 信号强度 - 通用
    snr FLOAT                       -- 信噪比 - 通用
) TAGS (
    device_id NCHAR(36),            -- 设备ID
    device_type NCHAR(20),          -- 设备类型 (CAPSULE/TRACKER)
    dev_eui NCHAR(50),              -- DevEUI
    animal_id NCHAR(36)             -- 绑定的牲畜ID (可能为NULL)
);

-- 3. 创建设备状态超级表
-- 用于存储设备在线/离线状态变化
CREATE STABLE IF NOT EXISTS device_status (
    ts TIMESTAMP,                   -- 时间戳
    status NCHAR(20),              -- 状态 (ONLINE/OFFLINE)
    battery_level INT,              -- 电量
    rssi INT,                       -- 信号强度
    firmware_version NCHAR(50)      -- 固件版本
) TAGS (
    device_id NCHAR(36),            -- 设备ID
    device_type NCHAR(20),          -- 设备类型
    dev_eui NCHAR(50)              -- DevEUI
);

-- 4. 为现有设备创建子表
-- 瘤胃胶囊C001
CREATE TABLE IF NOT EXISTS telemetry_DEV_CAP_001 USING telemetry_data TAGS ('DEV-CAP-001', 'CAPSULE', 'EUI-CAP-001', 'ANIMAL-002');

-- 瘤胃胶囊C002  
CREATE TABLE IF NOT EXISTS telemetry_DEV_CAP_002 USING telemetry_data TAGS ('DEV-CAP-002', 'CAPSULE', 'EUI-CAP-002', 'ANIMAL-003');

-- 瘤胃胶囊C003
CREATE TABLE IF NOT EXISTS telemetry_DEV_CAP_003 USING telemetry_data TAGS ('DEV-CAP-003', 'CAPSULE', 'EUI-CAP-003', NULL);

-- 追踪器T001
CREATE TABLE IF NOT EXISTS telemetry_DEV_TRK_001 USING telemetry_data TAGS ('DEV-TRK-001', 'TRACKER', 'EUI-TRK-001', 'ANIMAL-001');

-- 追踪器T002
CREATE TABLE IF NOT EXISTS telemetry_DEV_TRK_002 USING telemetry_data TAGS ('DEV-TRK-002', 'TRACKER', 'EUI-TRK-002', 'ANIMAL-002');

-- 5. 创建设备状态子表
CREATE TABLE IF NOT EXISTS status_DEV_CAP_001 USING device_status TAGS ('DEV-CAP-001', 'CAPSULE', 'EUI-CAP-001');
CREATE TABLE IF NOT EXISTS status_DEV_CAP_002 USING device_status TAGS ('DEV-CAP-002', 'CAPSULE', 'EUI-CAP-002');  
CREATE TABLE IF NOT EXISTS status_DEV_CAP_003 USING device_status TAGS ('DEV-CAP-003', 'CAPSULE', 'EUI-CAP-003');
CREATE TABLE IF NOT EXISTS status_DEV_TRK_001 USING device_status TAGS ('DEV-TRK-001', 'TRACKER', 'EUI-TRK-001');
CREATE TABLE IF NOT EXISTS status_DEV_TRK_002 USING device_status TAGS ('DEV-TRK-002', 'TRACKER', 'EUI-TRK-002');

-- 6. 验证表创建
SHOW STABLES;
SHOW TABLES; 