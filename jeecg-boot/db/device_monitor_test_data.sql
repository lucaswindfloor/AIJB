-- =================================================================
-- 设备监控系统 - 测试数据 (V1.0)
-- 说明：此脚本为设备监控功能提供完整的测试数据，包含各种状态的设备
-- 用于验证：KPI统计、问题设备列表、RPC指令、维保任务等功能
-- =================================================================

-- 清空现有数据，确保测试环境的一致性
DELETE FROM `ah_alarm_record`;
DELETE FROM `ah_animal_device_link`;
DELETE FROM `ah_device`;
DELETE FROM `ah_animal`;
DELETE FROM `ah_herd`;

-- 1. 插入畜群数据
INSERT INTO `ah_herd` (`id`, `name`, `manager_id`, `description`, `create_by`, `create_time`) VALUES
('HERD-001', '阳光一号牛群', 'admin', '主要负责繁育的核心牛群', 'admin', NOW(3)),
('HERD-002', '高山育肥群', 'admin', '专注于肉牛育肥的牛群', 'admin', NOW(3));

-- 2. 插入牲畜数据
INSERT INTO `ah_animal` (`id`, `ear_tag_id`, `name`, `type`, `herd_id`, `enclosure`, `gender`, `birth_date`, `weight_kg`, `height`, `health_status`, `health_score`, `latest_temperature`, `latest_activity`, `latest_steps`, `ai_conclusion`, `last_location_lon`, `last_location_lat`, `create_by`, `create_time`) VALUES
('ANIMAL-001', 'EAR-001', '大壮', 'YELLOW_CATTLE', 'HERD-002', 'C-01', '1', '2022-01-15', 560.50, 160.0, 'HEALTHY', 95, 38.6, 8200, 15000, '一切正常，活力十足', 119.758, 49.217, 'admin', NOW(3)),
('ANIMAL-002', 'EAR-002', '小花', 'DAIRY_COW', 'HERD-001', 'A-08', '2', '2021-07-20', 610.00, 155.5, 'SUB_HEALTHY', 78, 39.1, 4500, 8000, '近期反刍次数偏低，建议关注采食量', 119.765, 49.223, 'admin', NOW(3)),
('ANIMAL-003', 'EAR-003', '福星', 'DAIRY_COW', 'HERD-001', 'A-09', '2', '2023-03-10', 480.25, 140.0, 'ALARM', 55, 40.2, 2100, 3500, '体温持续偏高，疑似感染，请立即检查！', 119.750, 49.210, 'admin', NOW(3)),
('ANIMAL-004', 'EAR-004', '黑旋风', 'YELLOW_CATTLE', 'HERD-002', 'C-02', '1', '2022-11-05', 595.70, 165.0, 'HEALTHY', 98, 38.8, 9500, 18000, '各项指标优秀', 119.771, 49.215, 'admin', NOW(3)),
('ANIMAL-005', 'EAR-005', '白雪', 'DAIRY_COW', 'HERD-001', 'A-10', '2', '2023-01-20', 520.00, 148.0, 'HEALTHY', 92, 38.9, 7200, 12000, '健康状态良好', 119.762, 49.220, 'admin', NOW(3));

-- 3. 插入设备数据 - 涵盖各种状态场景
INSERT INTO `ah_device` (`id`, `name`, `device_type`, `dev_eui`, `tb_device_id`, `model`, `purchase_date`, `status`, `rssi`, `lo_ra_snr`, `battery_level`, `firmware_version`, `hardware_version`, `work_mode`, `create_by`, `create_time`, `update_time`) VALUES
-- 正常在线设备
('DEV-001', '瘤胃胶囊-C001', 'CAPSULE', 'EUI-CAP-001', 'TB-CAP-001', 'RM-2000', '2023-01-10', 'ACTIVE', -82, 9.5, 88, 'v1.2.0', 'v2.1', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 100 DAY), DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
('DEV-002', '瘤胃胶囊-C002', 'CAPSULE', 'EUI-CAP-002', 'TB-CAP-002', 'RM-2000', '2023-01-11', 'ACTIVE', -75, 8.2, 95, 'v1.2.0', 'v2.1', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 90 DAY), DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
('DEV-003', '追踪器-T001', 'TRACKER', 'EUI-TRK-001', 'TB-TRK-001', 'ET-500', '2023-02-20', 'ACTIVE', -90, 7.0, 75, 'v2.1.5', 'v3.0', '省电', 'admin', DATE_SUB(NOW(), INTERVAL 80 DAY), DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
('DEV-004', '追踪器-T002', 'TRACKER', 'EUI-TRK-002', 'TB-TRK-002', 'ET-500', '2023-02-21', 'ACTIVE', -85, 6.5, 82, 'v2.1.5', 'v3.0', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 70 DAY), DATE_SUB(NOW(), INTERVAL 8 MINUTE)),

-- 低电量设备（电量<20%）
('DEV-005', '瘤胃胶囊-C003', 'CAPSULE', 'EUI-CAP-003', 'TB-CAP-003', 'RM-2000', '2023-03-01', 'ACTIVE', -88, 5.8, 15, 'v1.2.0', 'v2.1', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
('DEV-006', '追踪器-T003', 'TRACKER', 'EUI-TRK-003', 'TB-TRK-003', 'ET-500', '2023-03-05', 'ACTIVE', -92, 4.2, 8, 'v2.1.5', 'v3.0', '省电', 'admin', DATE_SUB(NOW(), INTERVAL 50 DAY), DATE_SUB(NOW(), INTERVAL 25 MINUTE)),

-- 长时间离线设备（超过12小时）
('DEV-007', '瘤胃胶囊-C004', 'CAPSULE', 'EUI-CAP-004', 'TB-CAP-004', 'RM-2000', '2023-04-01', 'ACTIVE', -95, 3.5, 60, 'v1.1.8', 'v2.0', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 18 HOUR)),
('DEV-008', '追踪器-T004', 'TRACKER', 'EUI-TRK-004', 'TB-TRK-004', 'ET-500', '2023-04-05', 'ACTIVE', -98, 2.8, 45, 'v2.1.3', 'v2.8', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- 既低电量又离线的设备（最严重问题）
('DEV-009', '瘤胃胶囊-C005', 'CAPSULE', 'EUI-CAP-005', 'TB-CAP-005', 'RM-2000', '2023-05-01', 'ACTIVE', -100, 1.5, 5, 'v1.1.5', 'v1.9', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- 闲置设备（解绑后）
('DEV-010', '瘤胃胶囊-C006', 'CAPSULE', 'EUI-CAP-006', 'TB-CAP-006', 'RM-2000', '2023-05-10', 'IDLE', -78, 8.8, 90, 'v1.2.0', 'v2.1', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
('DEV-011', '追踪器-T005', 'TRACKER', 'EUI-TRK-005', 'TB-TRK-005', 'ET-500', '2023-05-15', 'IDLE', -80, 7.5, 18, 'v2.1.5', 'v3.0', '省电', 'admin', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 14 HOUR)),

-- 维保中设备
('DEV-012', '瘤胃胶囊-C007', 'CAPSULE', 'EUI-CAP-007', 'TB-CAP-007', 'RM-2000', '2023-06-01', 'MAINTENANCE', NULL, NULL, NULL, 'v1.1.8', 'v2.0', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- 库存中设备
('DEV-013', '瘤胃胶囊-C008', 'CAPSULE', 'EUI-CAP-008', 'TB-CAP-008', 'RM-2000', '2023-06-10', 'IN_STOCK', NULL, NULL, 100, 'v1.2.0', 'v2.1', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
('DEV-014', '追踪器-T006', 'TRACKER', 'EUI-TRK-006', 'TB-TRK-006', 'ET-500', '2023-06-15', 'IN_STOCK', NULL, NULL, 100, 'v2.1.5', 'v3.0', '常规', 'admin', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 4. 插入设备与牲畜的绑定关系
INSERT INTO `ah_animal_device_link` (`id`, `animal_id`, `device_id`, `bind_time`, `is_active`) VALUES
-- 正常绑定关系
(UUID(), 'ANIMAL-001', 'DEV-001', DATE_SUB(NOW(), INTERVAL 90 DAY), 1),  -- 大壮 - 瘤胃胶囊C001
(UUID(), 'ANIMAL-001', 'DEV-003', DATE_SUB(NOW(), INTERVAL 75 DAY), 1),  -- 大壮 - 追踪器T001（一个牲畜绑定两个设备）
(UUID(), 'ANIMAL-002', 'DEV-002', DATE_SUB(NOW(), INTERVAL 85 DAY), 1),  -- 小花 - 瘤胃胶囊C002
(UUID(), 'ANIMAL-002', 'DEV-004', DATE_SUB(NOW(), INTERVAL 65 DAY), 1),  -- 小花 - 追踪器T002
(UUID(), 'ANIMAL-003', 'DEV-005', DATE_SUB(NOW(), INTERVAL 55 DAY), 1),  -- 福星 - 瘤胃胶囊C003（低电量设备）
(UUID(), 'ANIMAL-004', 'DEV-007', DATE_SUB(NOW(), INTERVAL 35 DAY), 1),  -- 黑旋风 - 瘤胃胶囊C004（离线设备）
(UUID(), 'ANIMAL-005', 'DEV-009', DATE_SUB(NOW(), INTERVAL 25 DAY), 1),  -- 白雪 - 瘤胃胶囊C005（严重问题设备）

-- 历史绑定关系（已解绑）
(UUID(), 'ANIMAL-003', 'DEV-010', DATE_SUB(NOW(), INTERVAL 120 DAY), 0),  -- 福星曾经绑定过胶囊C006（现在闲置）
(UUID(), 'ANIMAL-004', 'DEV-011', DATE_SUB(NOW(), INTERVAL 100 DAY), 0);  -- 黑旋风曾经绑定过追踪器T005（现在闲置）

-- 更新历史绑定的解绑时间
UPDATE `ah_animal_device_link` SET `unbind_time` = DATE_SUB(NOW(), INTERVAL 20 DAY) WHERE `is_active` = 0;

-- 5. 插入一些告警记录（与问题设备相关）
INSERT INTO `ah_alarm_record` (`id`, `animal_id`, `alarm_type`, `alarm_level`, `alarm_content`, `alarm_time`, `status`, `create_time`) VALUES
-- 低电量告警
(UUID(), 'ANIMAL-003', 'device_low_battery', 'WARN', '设备瘤胃胶囊C003电量降至15%，请及时充电或更换', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'PENDING', NOW(3)),
(UUID(), 'ANIMAL-005', 'device_low_battery', 'CRITICAL', '设备瘤胃胶囊C005电量仅剩5%，请立即处理', DATE_SUB(NOW(), INTERVAL 6 HOUR), 'PENDING', NOW(3)),

-- 设备离线告警
(UUID(), 'ANIMAL-004', 'device_offline', 'WARN', '设备瘤胃胶囊C004已离线超过18小时，请检查设备状态', DATE_SUB(NOW(), INTERVAL 18 HOUR), 'PENDING', NOW(3)),
(UUID(), 'ANIMAL-005', 'device_offline', 'CRITICAL', '设备瘤胃胶囊C005已离线超过3天，设备可能故障', DATE_SUB(NOW(), INTERVAL 3 DAY), 'PENDING', NOW(3)),

-- 已处理的告警
(UUID(), 'ANIMAL-002', 'device_low_battery', 'WARN', '设备追踪器T002电量曾降至18%', DATE_SUB(NOW(), INTERVAL 2 DAY), 'RESOLVED', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- =================================================================
-- 验证数据统计 - 用于检查测试数据的正确性
-- =================================================================

-- 查看设备总体统计
SELECT 
    COUNT(*) AS total_devices,
    SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) AS active_devices,
    SUM(CASE WHEN status = 'IDLE' THEN 1 ELSE 0 END) AS idle_devices,
    SUM(CASE WHEN status = 'IN_STOCK' THEN 1 ELSE 0 END) AS stock_devices,
    SUM(CASE WHEN status = 'MAINTENANCE' THEN 1 ELSE 0 END) AS maintenance_devices
FROM ah_device;

-- 查看KPI统计（模拟设备监控接口结果）
SELECT
    COUNT(*) AS total,
    SUM(CASE WHEN update_time >= DATE_SUB(NOW(), INTERVAL 12 HOUR) THEN 1 ELSE 0 END) AS online,
    SUM(CASE WHEN update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) THEN 1 ELSE 0 END) AS offline,
    SUM(CASE WHEN battery_level < 20 THEN 1 ELSE 0 END) AS low_battery
FROM ah_device;

-- 查看问题设备列表（模拟问题设备查询结果）
SELECT 
    d.name,
    d.device_type,
    d.dev_eui,
    d.battery_level,
    d.update_time,
    a.ear_tag_id AS animal_ear_tag,
    CASE WHEN d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) THEN '离线' ELSE '在线' END AS status,
    CASE WHEN d.battery_level < 20 THEN '低电量' ELSE '电量正常' END AS battery_status
FROM ah_device d
LEFT JOIN ah_animal_device_link adl ON d.id = adl.device_id AND adl.is_active = 1
LEFT JOIN ah_animal a ON adl.animal_id = a.id
WHERE d.status IN ('ACTIVE', 'IDLE') 
  AND (d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) OR d.battery_level < 20)
ORDER BY d.battery_level ASC, d.update_time ASC; 