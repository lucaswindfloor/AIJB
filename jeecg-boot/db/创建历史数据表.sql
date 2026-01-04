-- =================================================================
-- 创建历史数据表 (MySQL模拟时序数据库)
-- 目标：为历史曲线图功能提供数据支持，无需等待时序数据库部署
-- =================================================================

-- 1. 创建遥测数据历史表
-- -----------------------------------------------------------------
CREATE TABLE `ah_telemetry_history` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `device_id` varchar(36) NOT NULL COMMENT '设备ID',
  `telemetry_key` varchar(50) NOT NULL COMMENT '遥测字段名 (temperature, activity, ph, steps, battery, rssi, snr)',
  `telemetry_value` decimal(10,4) NOT NULL COMMENT '遥测值',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位 (℃, 次/小时, pH, 步/小时, %, dBm, dB)',
  `timestamp` datetime(3) NOT NULL COMMENT '时间戳',
  `create_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  KEY `idx_device_key_time` (`device_id`, `telemetry_key`, `timestamp`),
  KEY `idx_timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='遥测数据历史记录表(MySQL模拟时序数据库)';

-- 2. 创建GPS位置历史表
-- -----------------------------------------------------------------
CREATE TABLE `ah_location_history` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `device_id` varchar(36) NOT NULL COMMENT '设备ID (追踪器)',
  `longitude` decimal(10,7) NOT NULL COMMENT '经度',
  `latitude` decimal(10,7) NOT NULL COMMENT '纬度',
  `altitude` decimal(8,2) DEFAULT NULL COMMENT '海拔(米)',
  `accuracy` decimal(6,2) DEFAULT NULL COMMENT 'GPS精度(米)',
  `timestamp` datetime(3) NOT NULL COMMENT '时间戳',
  `create_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  KEY `idx_device_time` (`device_id`, `timestamp`),
  KEY `idx_timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GPS位置历史记录表';

-- 验证表创建
SELECT 'ah_telemetry_history 表已创建' as info;
SELECT 'ah_location_history 表已创建' as info; 