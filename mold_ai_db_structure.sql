-- MySQL dump 10.13  Distrib 8.3.0, for Win64 (x86_64)
--
-- Host: localhost    Database: mold_ai_db
-- ------------------------------------------------------
-- Server version	5.7.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `mai_actuator`
--

DROP TABLE IF EXISTS `mai_actuator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mai_actuator` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mai_device_binding`
--

DROP TABLE IF EXISTS `mai_device_binding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mai_device_binding` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `device_id` varchar(64) NOT NULL COMMENT '设备ID (来源于IoT平台)',
  `scene_id` varchar(36) NOT NULL COMMENT '关联的场景ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_device_id` (`device_id`) USING BTREE,
  KEY `idx_scene_id` (`scene_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霉菌分析-设备场景绑定表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mai_risk_result`
--

DROP TABLE IF EXISTS `mai_risk_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mai_risk_result` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `device_id` varchar(64) NOT NULL COMMENT '设备ID',
  `scene_id` varchar(36) NOT NULL COMMENT '分析时使用的场景ID',
  `mi_value` decimal(10,4) NOT NULL COMMENT '霉菌指数(MI)',
  `risk_level` varchar(20) NOT NULL COMMENT '风险等级 (LOW/MEDIUM/HIGH)',
  `calculated_time` datetime NOT NULL COMMENT '计算基准时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_device_time` (`device_id`,`calculated_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霉菌分析-风险计算历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mai_scene`
--

DROP TABLE IF EXISTS `mai_scene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mai_scene` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `scene_code` varchar(64) NOT NULL COMMENT '场景编码 (e.g. wood_furniture)',
  `scene_name` varchar(100) NOT NULL COMMENT '场景名称',
  `material_level` decimal(4,2) NOT NULL COMMENT '材料敏感度等级 (1.0-6.0)',
  `threshold_low` decimal(6,4) NOT NULL DEFAULT '0.5000' COMMENT '低风险阈值 (MI)',
  `threshold_high` decimal(6,4) NOT NULL DEFAULT '2.0000' COMMENT '高风险阈值 (MI)',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用: 1-是, 0-否',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_scene_code` (`scene_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霉菌分析-场景配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mai_sensor_actuator_link`
--

DROP TABLE IF EXISTS `mai_sensor_actuator_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mai_sensor_actuator_link` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `sensor_device_id` varchar(64) NOT NULL COMMENT '传感器设备ID',
  `actuator_id` varchar(36) NOT NULL COMMENT '联动设备ID',
  `trigger_level` varchar(20) DEFAULT 'HIGH' COMMENT '触发风险等级: HIGH/MEDIUM/LOW',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_sensor_actuator` (`sensor_device_id`,`actuator_id`) USING BTREE,
  KEY `idx_sensor_id` (`sensor_device_id`) USING BTREE,
  KEY `idx_actuator_id` (`actuator_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='霉菌分析-传感器联动设备关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-19 11:47:40
