-- =============================================
-- 霉菌AI微服务数据库表结构 (MVP版本)
-- =============================================

-- 1. 预设场景表
CREATE TABLE `mold_ai_scene` (
    `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
    `scene_code` VARCHAR(50) NOT NULL COMMENT '场景编码',
    `scene_name` VARCHAR(100) NOT NULL COMMENT '场景名称',
    `material_level` DECIMAL(3,1) NOT NULL COMMENT '材料等级(1.0-6.0)',
    `threshold_low` DECIMAL(5,2) DEFAULT 0.50 COMMENT '低风险阈值',
    `threshold_high` DECIMAL(5,2) DEFAULT 0.80 COMMENT '高风险阈值',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预设场景表';

-- 2. 设备场景绑定表
CREATE TABLE `mold_ai_device_binding` (
    `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
    `device_id` VARCHAR(100) NOT NULL COMMENT '设备ID',
    `scene_id` VARCHAR(36) NOT NULL COMMENT '场景ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备场景绑定表';

-- 3. 风险计算结果表
CREATE TABLE `mold_ai_risk_result` (
    `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
    `device_id` VARCHAR(100) NOT NULL COMMENT '设备ID',
    `scene_id` VARCHAR(36) COMMENT '场景ID',
    `mi_value` DECIMAL(8,4) NOT NULL COMMENT '霉菌指数(MI)',
    `risk_level` VARCHAR(20) NOT NULL COMMENT '风险等级',
    `calculated_time` DATETIME NOT NULL COMMENT '计算时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_device_time` (`device_id`, `calculated_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险计算结果表';

-- =============================================
-- 初始化数据：5种预设场景
-- =============================================
INSERT INTO `mold_ai_scene` (`id`, `scene_code`, `scene_name`, `material_level`, `threshold_low`, `threshold_high`) VALUES
('1', 'wall', '标准墙面', 3.0, 0.50, 0.80),
('2', 'wood', '木质家具', 4.0, 0.40, 0.70),
('3', 'humid', '高湿功能区', 3.5, 0.55, 0.85),
('4', 'window', '窗户外墙角', 3.5, 0.50, 0.80),
('5', 'equipment', '设备管道间', 4.0, 0.45, 0.75);


