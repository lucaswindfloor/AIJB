-- -----------------------------------------------------
-- 霉菌风险分析微服务独立数据库脚本
-- 建议库名: mold_ai_db (与 Jeecg 主库物理隔离)
-- -----------------------------------------------------

-- 创建并使用 mold_ai_db
CREATE DATABASE IF NOT EXISTS `mold_ai_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `mold_ai_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------
-- Table structure for mai_scene (预设场景)
-- 前缀从 mold_ai_ 改为 mai_ 以示区分，且更加简短
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mai_scene`;
CREATE TABLE `mai_scene`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `scene_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '场景编码 (e.g. wood_furniture)',
  `scene_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '场景名称',
  `material_level` decimal(4, 2) NOT NULL COMMENT '材料敏感度等级 (1.0-6.0)',
  `threshold_low` decimal(6, 4) NOT NULL DEFAULT 0.5000 COMMENT '低风险阈值 (MI)',
  `threshold_high` decimal(6, 4) NOT NULL DEFAULT 2.0000 COMMENT '高风险阈值 (MI)',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用: 1-是, 0-否',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_scene_code`(`scene_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '霉菌分析-场景配置表';

-- -----------------------------------------------------
-- 初始化 5 种标准场景数据 (根据架构方案文档)
-- -----------------------------------------------------
INSERT INTO `mai_scene` (`id`, `scene_code`, `scene_name`, `material_level`, `threshold_low`, `threshold_high`, `enabled`) VALUES 
('1', 'standard_wall', '标准墙面/天花板', 3.00, 0.5000, 2.0000, 1),
('2', 'wood_furniture', '木质家具/储物区', 4.00, 0.4000, 1.5000, 1),
('3', 'high_humidity_zone', '高湿功能区(浴室等)', 3.50, 0.8000, 2.5000, 1),
('4', 'window_corner', '窗户/外墙角', 3.50, 0.6000, 2.0000, 1),
('5', 'equipment_room', '设备区/管道间', 4.00, 0.5000, 2.0000, 1);

-- -----------------------------------------------------
-- Table structure for mai_device_binding (设备绑定关系)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mai_device_binding`;
CREATE TABLE `mai_device_binding`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `device_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备ID (来源于IoT平台)',
  `scene_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联的场景ID',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_device_id`(`device_id`) USING BTREE,
  INDEX `idx_scene_id`(`scene_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '霉菌分析-设备场景绑定表';

-- -----------------------------------------------------
-- Table structure for mai_risk_result (分析历史结果)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mai_risk_result`;
CREATE TABLE `mai_risk_result`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `device_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备ID',
  `scene_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分析时使用的场景ID',
  `mi_value` decimal(10, 4) NOT NULL COMMENT '霉菌指数(MI)',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '风险等级 (LOW/MEDIUM/HIGH)',
  `calculated_time` datetime(0) NOT NULL COMMENT '计算基准时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_device_time`(`device_id`, `calculated_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '霉菌分析-风险计算历史表';

SET FOREIGN_KEY_CHECKS = 1;

