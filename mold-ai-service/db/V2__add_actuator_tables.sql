-- =============================================
-- 霉菌风险分析模块 - 联动设备管理表
-- 版本: V2
-- 描述: 新增联动设备表和传感器-联动设备关联表
-- =============================================

-- 1. 联动设备表（执行器）
CREATE TABLE IF NOT EXISTS `mai_actuator` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `device_id` varchar(64) NOT NULL COMMENT '设备ID (ThingsBoard设备标识)',
  `device_name` varchar(100) NOT NULL COMMENT '设备名称',
  `device_type` varchar(32) NOT NULL COMMENT '设备类型: parking_lock/lorawan_switch/exhaust_fan/dehumidifier/heater',
  `rpc_method` varchar(64) DEFAULT 'LockControl' COMMENT 'RPC调用方法名',
  `rpc_params` varchar(255) DEFAULT NULL COMMENT 'RPC默认参数(JSON)',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用: 1-是, 0-否',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_device_id` (`device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霉菌分析-联动设备表(执行器)';

-- 2. 传感器-联动设备关联表
CREATE TABLE IF NOT EXISTS `mai_sensor_actuator_link` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `sensor_device_id` varchar(64) NOT NULL COMMENT '传感器设备ID',
  `actuator_id` varchar(36) NOT NULL COMMENT '联动设备ID',
  `trigger_level` varchar(20) DEFAULT 'HIGH' COMMENT '触发风险等级: HIGH/MEDIUM/LOW',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_sensor_actuator` (`sensor_device_id`, `actuator_id`) USING BTREE,
  KEY `idx_sensor_id` (`sensor_device_id`) USING BTREE,
  KEY `idx_actuator_id` (`actuator_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霉菌分析-传感器联动设备关联表';

-- 插入一条默认的车位锁设备（演示用）
INSERT INTO `mai_actuator` (`id`, `device_id`, `device_name`, `device_type`, `rpc_method`, `enabled`) VALUES
('1', '7dc08ac0-4c15-11f0-bda4-570db53547bd', '演示车位锁', 'parking_lock', 'LockControl', 1);

