-- 智能畜牧管理系统 - V2.2 - 核心数据表结构
-- 基于《智能畜牧管理系统_SDD.md》文档生成
-- Generation Date: 2024-08-23

-- ----------------------------
-- Table structure for ah_herd
-- ----------------------------
DROP TABLE IF EXISTS `ah_herd`;
CREATE TABLE `ah_herd` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '畜群名称/编号',
  `farm_id` varchar(36) DEFAULT NULL COMMENT '所属牧场ID (用于多牧场扩展)',
  `manager_id` varchar(36) DEFAULT NULL COMMENT '负责人用户ID',
  `description` text COMMENT '描述',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime(3) DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='畜群管理表';

-- ----------------------------
-- Table structure for ah_animal
-- ----------------------------
DROP TABLE IF EXISTS `ah_animal`;
CREATE TABLE `ah_animal` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `ear_tag_id` varchar(50) DEFAULT NULL COMMENT '耳标号 (唯一)',
  `name` varchar(100) DEFAULT NULL COMMENT '牲畜昵称',
  `type` varchar(36) DEFAULT NULL COMMENT '牲畜类型 (字典: animal_type, 如黄牛、奶牛)',
  `herd_id` varchar(36) DEFAULT NULL COMMENT '所属畜群ID (关联 ah_herd.id)',
  `enclosure` varchar(50) DEFAULT NULL COMMENT '圈舍号',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别 (字典: sex)',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `weight_kg` decimal(10,2) DEFAULT NULL COMMENT '最新体重(KG)',
  `height` decimal(10,2) DEFAULT NULL COMMENT '体高(CM)',
  `health_status` varchar(20) DEFAULT 'HEALTHY' COMMENT '健康状态 (字典: health_status, 如HEALTHY, SUB_HEALTHY, ALARM)',
  `health_score` int(11) DEFAULT '100' COMMENT '健康评分 (0-100)',
  `latest_temperature` decimal(5,2) DEFAULT NULL COMMENT '最新体温(℃)',
  `latest_activity` int(11) DEFAULT NULL COMMENT '最新活动量',
  `latest_steps` int(11) DEFAULT NULL COMMENT '最新步数',
  `ai_conclusion` varchar(255) DEFAULT '一切正常' COMMENT '最新AI分析结论',
  `last_location_lon` decimal(10,7) DEFAULT NULL COMMENT '最后更新经度',
  `last_location_lat` decimal(10,7) DEFAULT NULL COMMENT '最后更新纬度',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime(3) DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ear_tag_id` (`ear_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牲畜档案表';

-- ----------------------------
-- Table structure for ah_device
-- ----------------------------
DROP TABLE IF EXISTS `ah_device`;
CREATE TABLE `ah_device` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '设备名称',
  `device_type` varchar(20) NOT NULL COMMENT '设备类型 (字典: device_type, CAPSULE-瘤胃胶囊, TRACKER-追踪器)',
  `dev_eui` varchar(50) DEFAULT NULL COMMENT 'LoRaWAN DevEUI (唯一)',
  `tb_device_id` varchar(50) DEFAULT NULL COMMENT 'ThingsBoard平台设备ID (唯一)',
  `model` varchar(100) DEFAULT NULL COMMENT '设备型号',
  `purchase_date` date DEFAULT NULL COMMENT '采购日期',
  `status` varchar(20) NOT NULL DEFAULT 'IN_STOCK' COMMENT '设备生命周期状态 (字典: device_lifecycle_status)',
  `rssi` int(11) DEFAULT NULL COMMENT '最后一次的信号强度RSSI',
  `lo_ra_snr` decimal(10, 2) DEFAULT NULL COMMENT '最后一次的信噪比SNR',
  `battery_level` int(11) DEFAULT NULL COMMENT '电量百分比',
  `firmware_version` varchar(50) DEFAULT NULL COMMENT '固件版本',
  `hardware_version` varchar(50) DEFAULT NULL COMMENT '硬件版本',
  `work_mode` varchar(50) DEFAULT NULL COMMENT '工作模式',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime(3) DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dev_eui` (`dev_eui`),
  UNIQUE KEY `uk_tb_device_id` (`tb_device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';

-- ----------------------------
-- Table structure for ah_animal_device_link
-- ----------------------------
DROP TABLE IF EXISTS `ah_animal_device_link`;
CREATE TABLE `ah_animal_device_link` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `animal_id` varchar(36) NOT NULL COMMENT '牲畜ID',
  `device_id` varchar(36) NOT NULL COMMENT '设备ID',
  `bind_time` datetime(3) NOT NULL COMMENT '绑定时间',
  `unbind_time` datetime(3) DEFAULT NULL COMMENT '解绑时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否当前有效 (1-是, 0-否)',
  `device_type` varchar(50) NULL COMMENT '设备类型 (冗余字段, 用于简化查询)',
  PRIMARY KEY (`id`),
  KEY `idx_animal_id` (`animal_id`),
  KEY `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牲畜设备关联表';

-- ----------------------------
-- Table structure for ah_alarm_record
-- ----------------------------
DROP TABLE IF EXISTS `ah_alarm_record`;
CREATE TABLE `ah_alarm_record` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `animal_id` varchar(36) NOT NULL COMMENT '告警牲畜ID',
  `alarm_type` varchar(50) NOT NULL COMMENT '告警类型 (字典: alarm_type, 如: a_temp_high, a_inactive, a_possible_estrus)',
  `alarm_level` varchar(20) NOT NULL DEFAULT 'WARN' COMMENT '告警级别 (字典: alarm_level, WARN, CRITICAL)',
  `alarm_content` varchar(500) NOT NULL COMMENT '告警内容描述',
  `alarm_time` datetime(3) NOT NULL COMMENT '告警发生时间',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态 (字典: process_status, PENDING, PROCESSING, RESOLVED, IGNORED)',
  `handler_id` varchar(36) DEFAULT NULL COMMENT '处理人ID',
  `handle_time` datetime(3) DEFAULT NULL COMMENT '处理时间',
  `handle_notes` text COMMENT '处理备注',
  `create_time` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_animal_id_time` (`animal_id`, `alarm_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

-- ----------------------------
-- Table structure for ah_animal_lifecycle_event
-- ----------------------------
DROP TABLE IF EXISTS `ah_animal_lifecycle_event`;
CREATE TABLE `ah_animal_lifecycle_event` (
  `id` varchar(36) NOT NULL,
  `animal_id` varchar(36) NOT NULL COMMENT '关联的牲畜ID',
  `event_type` varchar(50) NOT NULL COMMENT '事件类型 (字典: animal_event_type)',
  `event_time` datetime(3) NOT NULL COMMENT '事件发生时间',
  `description` text COMMENT '事件详细描述 (如药品名称、配种公牛编号等)',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_animal_id` (`animal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牲畜生命周期事件表';

-- ----------------------------
-- Table structure for ah_firmware
-- ----------------------------
DROP TABLE IF EXISTS `ah_firmware`;
CREATE TABLE `ah_firmware` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `device_type` varchar(20) NOT NULL COMMENT '适用的设备类型',
  `version` varchar(50) NOT NULL COMMENT '固件版本号',
  `file_url` varchar(255) NOT NULL COMMENT '固件文件存储地址',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(Bytes)',
  `checksum` varchar(100) DEFAULT NULL COMMENT '文件校验和 (MD5/SHA256)',
  `description` text COMMENT '版本更新说明',
  `upload_time` datetime(3) NOT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固件版本管理表';

-- ----------------------------
-- Table structure for ah_fota_task
-- ----------------------------
DROP TABLE IF EXISTS `ah_fota_task`;
CREATE TABLE `ah_fota_task` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `firmware_id` varchar(36) NOT NULL COMMENT '目标固件ID',
  `task_name` varchar(100) DEFAULT NULL COMMENT '任务名称',
  `target_selection_type` varchar(20) NOT NULL COMMENT '升级目标类型 (ALL, BY_DEVICE, BY_HERD)',
  `target_ids_json` json DEFAULT NULL COMMENT '升级目标的ID列表 (JSON数组)',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态 (PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELED)',
  `scheduled_time` datetime(3) DEFAULT NULL COMMENT '预定的开始时间 (NULL为立即开始)',
  `completion_time` datetime(3) DEFAULT NULL COMMENT '任务完成时间',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固件升级任务表';

-- ----------------------------
-- Table structure for ah_telemetry_latest
-- ----------------------------
DROP TABLE IF EXISTS `ah_telemetry_latest`;
CREATE TABLE `ah_telemetry_latest` (
  `id` varchar(36) NOT NULL,
  `device_id` varchar(36) NOT NULL COMMENT '设备ID',
  `telemetry_data` json NOT NULL COMMENT '遥测数据 (JSON格式)',
  `last_update_time` datetime(3) NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='最新遥测数据快照表';

-- ----------------------------
-- Table structure for ah_alarm_rule
-- ----------------------------
DROP TABLE IF EXISTS `ah_alarm_rule`;
CREATE TABLE `ah_alarm_rule` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '规则名称 (如: 高温告警)',
  `alarm_type` varchar(50) NOT NULL COMMENT '关联的告警类型 (同 ah_alarm_record.alarm_type)',
  `device_type` varchar(20) NOT NULL COMMENT '适用的设备类型 (CAPSULE, TRACKER)',
  `telemetry_key` varchar(50) NOT NULL COMMENT '监控的遥测数据字段名 (如: Temperature, step)',
  `operator` varchar(10) NOT NULL COMMENT '比较操作符 (>, <, =)',
  `threshold_value` varchar(50) NOT NULL COMMENT '阈值',
  `duration_seconds` int(11) DEFAULT '0' COMMENT '持续时间(秒, 0表示瞬时触发)',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用 (1-是, 0-否)',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime(3) DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则配置表';

-- ----------------------------
-- Table structure for ah_daily_stats_report
-- ----------------------------
DROP TABLE IF EXISTS `ah_daily_stats_report`;
CREATE TABLE `ah_daily_stats_report` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `report_date` date NOT NULL COMMENT '统计日期',
  `farm_id` varchar(36) DEFAULT NULL COMMENT '牧场ID',
  `herd_id` varchar(36) DEFAULT NULL COMMENT '畜群ID (NULL表示全场统计)',
  `total_animals` int(11) DEFAULT '0' COMMENT '牲畜总数',
  `healthy_count` int(11) DEFAULT '0' COMMENT '健康数量',
  `sub_healthy_count` int(11) DEFAULT '0' COMMENT '亚健康数量',
  `alarm_count` int(11) DEFAULT '0' COMMENT '告警数量',
  `new_alarms_count` int(11) DEFAULT '0' COMMENT '当日新增告警数',
  `avg_temperature` decimal(5,2) DEFAULT NULL COMMENT '平均体温',
  `avg_activity` decimal(10,2) DEFAULT NULL COMMENT '平均活动量',
  `create_time` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_farm_herd_date` (`farm_id`, `herd_id`, `report_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日运营统计报表'; 


-- =================================================================
-- 智能畜牧平台 - 全套模拟数据 (V3 - 基于原有数据优化)
-- 说明：此脚本基于原有模拟数据进行增强，补充了新字段并优化了测试场景。
-- =================================================================

-- 0. 清空数据，确保幂等性
-- -----------------------------------------------------------------
DELETE FROM `ah_alarm_record`;
DELETE FROM `ah_animal_lifecycle_event`;
DELETE FROM `ah_animal_device_link`;
DELETE FROM `ah_device`;
DELETE FROM `ah_animal`;
DELETE FROM `ah_herd`;

-- 1. 模拟畜群数据 (ah_herd) - 保持不变
-- -----------------------------------------------------------------
INSERT INTO `ah_herd` (`id`, `name`, `manager_id`, `description`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('HERD-001', '阳光一号牛群', 'admin', '主要负责繁育的核心牛群', 'admin', NOW(3), 'admin', NOW(3)),
('HERD-002', '高山育肥群', 'admin', '专注于肉牛育肥的牛群', 'admin', NOW(3), 'admin', NOW(3));


-- 2. 模拟设备数据 (ah_device) - 补全字段
-- -----------------------------------------------------------------
INSERT INTO `ah_device` (`id`, `name`, `device_type`, `dev_eui`, `tb_device_id`, `model`, `purchase_date`, `status`, `rssi`, `lo_ra_snr`, `battery_level`, `firmware_version`, `hardware_version`, `work_mode`, `create_by`, `create_time`) VALUES
('DEV-CAP-001', '瘤胃胶囊C001', 'CAPSULE', 'EUI-CAP-001', 'TB-CAP-001', 'RM-2000', '2023-01-10', 'ACTIVE', -80, 9.5, 95, 'v1.2.0', 'v2.1', '常规', 'admin', NOW(3)),
('DEV-CAP-002', '瘤胃胶囊C002', 'CAPSULE', 'EUI-CAP-002', 'TB-CAP-002', 'RM-2000', '2023-01-11', 'ACTIVE', -88, 8.2, 88, 'v1.2.0', 'v2.1', '常规', 'admin', NOW(3)),
('DEV-TRK-001', '追踪器T001', 'TRACKER', 'EUI-TRK-001', 'TB-TRK-001', 'ET-500', '2023-02-20', 'ACTIVE', -95, 7.0, 100, 'v2.1.5', 'v3.0', '省电', 'admin', NOW(3)),
('DEV-TRK-002', '追踪器T002', 'TRACKER', 'EUI-TRK-002', 'TB-TRK-002', 'ET-500', '2023-02-21', 'IDLE', -110, 5.0, 99, 'v2.1.5', 'v3.0', '常规', 'admin', NOW(3)),
('DEV-CAP-003', '瘤胃胶囊C003', 'CAPSULE', 'EUI-CAP-003', 'TB-CAP-003', 'RM-2000', '2023-05-01', 'IN_STOCK', NULL, NULL, 100, 'v1.3.0', 'v2.2', '常规', 'admin', NOW(3));


-- 3. 模拟牲畜档案数据 (ah_animal) - 补全字段
-- -----------------------------------------------------------------
INSERT INTO `ah_animal` (`id`, `ear_tag_id`, `name`, `type`, `breed`, `source`, `herd_id`, `enclosure`, `gender`, `birth_date`, `weight_kg`, `height`, `health_status`, `health_score`, `latest_temperature`, `latest_activity`, `latest_steps`, `ai_conclusion`, `last_location_lon`, `last_location_lat`, `create_by`, `create_time`) VALUES
('ANIMAL-001', 'EAR-001', '大壮', 'YELLOW_CATTLE', 'angus', 'self_bred', 'HERD-002', 'C-01', '1', '2022-01-15', 560.50, 160.0, 'HEALTHY', 95, 38.6, 8200, 15000, '一切正常，活力十足', 119.758, 49.217, 'admin', NOW(3)),
('ANIMAL-002', 'EAR-002', '小花', 'DAIRY_COW', 'holstein', 'purchased', 'HERD-001', 'A-08', '2', '2021-07-20', 610.00, 155.5, 'SUB_HEALTHY', 78, 39.1, 4500, 8000, '近期反刍次数偏低，建议关注采食量。', 119.765, 49.223, 'admin', NOW(3)),
('ANIMAL-003', 'EAR-003', '福星', 'DAIRY_COW', 'holstein', 'self_bred', 'HERD-001', 'A-09', '2', '2023-03-10', 480.25, 140.0, 'ALARM', 55, 40.2, 2100, 3500, '体温持续偏高，疑似感染，请立即检查！', 119.750, 49.210, 'admin', NOW(3)),
('ANIMAL-004', 'EAR-004', '黑旋风', 'YELLOW_CATTLE', 'charolais', 'purchased', 'HERD-002', 'C-02', '1', '2022-11-05', 595.70, 165.0, 'HEALTHY', 98, 38.8, 9500, 18000, '各项指标优秀', 119.771, 49.215, 'admin', NOW(3));


-- 4. 模拟牲畜与设备的绑定关系 (ah_animal_device_link) - 优化测试场景
-- -----------------------------------------------------------------
INSERT INTO `ah_animal_device_link` (`id`, `animal_id`, `device_id`, `bind_time`, `is_active`, `device_type`) VALUES
(UUID(), 'ANIMAL-001', 'DEV-TRK-001', DATE_SUB(NOW(), INTERVAL 100 DAY), 1, 'TRACKER'),
(UUID(), 'ANIMAL-002', 'DEV-CAP-001', DATE_SUB(NOW(), INTERVAL 80 DAY), 1, 'CAPSULE'),
(UUID(), 'ANIMAL-002', 'DEV-TRK-002', DATE_SUB(NOW(), INTERVAL 79 DAY), 1, 'TRACKER'), -- 为"小花"绑定第二个设备
(UUID(), 'ANIMAL-003', 'DEV-CAP-002', DATE_SUB(NOW(), INTERVAL 60 DAY), 1, 'CAPSULE');


-- 5. 模拟告警记录 (ah_alarm_record) - 保持不变
-- -----------------------------------------------------------------
INSERT INTO `ah_alarm_record` (`id`, `animal_id`, `alarm_type`, `alarm_level`, `alarm_content`, `alarm_time`, `status`, `handler_id`, `handle_time`, `handle_notes`, `create_time`) VALUES
(UUID(), 'ANIMAL-003', 'a_temp_high', 'CRITICAL', '体温达到40.2℃，超过红色阈值(39.5℃)', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'PENDING', NULL, NULL, NULL, NOW(3)),
(UUID(), 'ANIMAL-003', 'a_inactive', 'WARN', '连续4小时活动量低于100，活动量过低', DATE_SUB(NOW(), INTERVAL 8 HOUR), 'PENDING', NULL, NULL, NULL, NOW(3)),
(UUID(), 'ANIMAL-002', 'a_inactive', 'WARN', '24小时总步数仅8000，低于亚健康线', DATE_SUB(NOW(), INTERVAL 1 DAY), 'RESOLVED', 'admin', DATE_SUB(NOW(), INTERVAL 20 HOUR), '已检查，采食正常，继续观察。', DATE_SUB(NOW(), INTERVAL 1 DAY));


-- 6. 模拟生命周期事件 (ah_animal_lifecycle_event) - 保持不变
-- -----------------------------------------------------------------
INSERT INTO `ah_animal_lifecycle_event` (`id`, `animal_id`, `event_type`, `event_time`, `description`, `create_by`, `create_time`) VALUES
(UUID(), 'ANIMAL-001', 'event_immune', '2023-06-01 10:00:00', '接种口蹄疫疫苗，5ml', 'admin', NOW(3)),
(UUID(), 'ANIMAL-002', 'event_breed', '2023-09-20 14:30:00', '人工授精，使用冻精编号 XN-0081', 'admin', NOW(3)),
(UUID(), 'ANIMAL-003', 'event_med', '2024-01-10 09:00:00', '治疗轻微蹄部擦伤，使用青霉素。', 'admin', NOW(3));

-- 7. 字典数据部分 (保留不变)
-- -----------------------------------------------------------------
-- 1. 在字典主表 (sys_dict) 中创建“牲畜类型”的条目
INSERT INTO `sys_dict` (`id`, `dict_name`, `dict_code`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES
	('1826500661280387074', '牲畜类型', 'animal_type', '用于定义牲畜的具体种类', 'admin', NOW(), NULL, NULL, 0)
ON DUPLICATE KEY UPDATE dict_name='牲畜类型', description='用于定义牲畜的具体种类';

-- 2. 在字典项表 (sys_dict_item) 中为“牲畜类型”添加具体选项
-- 注意: 请确保上面的字典主表ID ('1826500661280387074') 是正确的，如果您的数据库中已存在此ID，请相应修改。
INSERT INTO `sys_dict_item` (`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES
	('1826500854425620482', '1826500661280387074', '黄牛', 'YELLOW_CATTLE', NULL, 1, 1, 'admin', NOW(), NULL, NULL),
	('1826500918090534914', '1826500661280387074', '奶牛', 'DAIRY_COW', NULL, 2, 1, 'admin', NOW(), NULL, NULL)
ON DUPLICATE KEY UPDATE item_text=VALUES(item_text), item_value=VALUES(item_value);
