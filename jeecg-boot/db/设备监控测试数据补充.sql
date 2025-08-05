-- =================================================================
-- 设备监控测试数据补充 (基于现有数据)
-- 目标：在现有5个设备和4个牲畜基础上，补充设备监控功能的测试场景
-- 说明：不删除现有数据，只进行UPDATE和INSERT操作
-- =================================================================

-- 1. 为现有设备补充 update_time 字段，模拟不同的在线/离线状态
-- -----------------------------------------------------------------
UPDATE ah_device SET update_time = NOW() WHERE id = 'DEV-CAP-001';  -- 在线
UPDATE ah_device SET update_time = NOW() WHERE id = 'DEV-TRK-001';  -- 在线
UPDATE ah_device SET update_time = DATE_SUB(NOW(), INTERVAL 2 HOUR) WHERE id = 'DEV-CAP-002';  -- 2小时前，接近离线
UPDATE ah_device SET update_time = DATE_SUB(NOW(), INTERVAL 18 HOUR) WHERE id = 'DEV-TRK-002'; -- 18小时前，离线
UPDATE ah_device SET update_time = NULL WHERE id = 'DEV-CAP-003';  -- 库存中设备，无更新时间

-- 2. 调整部分设备的电量，创建低电量测试场景
-- -----------------------------------------------------------------
UPDATE ah_device SET battery_level = 15 WHERE id = 'DEV-CAP-002';  -- 低电量
UPDATE ah_device SET battery_level = 8 WHERE id = 'DEV-TRK-002';   -- 极低电量

-- 3. 新增更多设备，丰富测试场景
-- -----------------------------------------------------------------
INSERT INTO ah_device (id, name, device_type, dev_eui, tb_device_id, model, purchase_date, status, rssi, lo_ra_snr, battery_level, firmware_version, hardware_version, work_mode, create_by, create_time, update_time) VALUES
-- 问题设备：长时间离线 + 低电量
('DEV-CAP-004', '瘤胃胶囊C004', 'CAPSULE', 'EUI-CAP-004', 'TB-CAP-004', 'RM-2000', '2023-03-01', 'ACTIVE', -120, 3.0, 5, 'v1.1.5', 'v2.0', '常规', 'admin', NOW(), DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- 问题设备：仅低电量
('DEV-TRK-003', '追踪器T003', 'TRACKER', 'EUI-TRK-003', 'TB-TRK-003', 'ET-500', '2023-03-15', 'ACTIVE', -85, 8.5, 12, 'v2.1.0', 'v2.8', '省电', 'admin', NOW(), NOW()),

-- 问题设备：仅长时间离线
('DEV-CAP-005', '瘤胃胶囊C005', 'CAPSULE', 'EUI-CAP-005', 'TB-CAP-005', 'RM-2000', '2023-04-01', 'ACTIVE', NULL, NULL, 80, 'v1.2.0', 'v2.1', '常规', 'admin', NOW(), DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- 正常设备：在线+电量正常
('DEV-TRK-004', '追踪器T004', 'TRACKER', 'EUI-TRK-004', 'TB-TRK-004', 'ET-500', '2023-04-15', 'ACTIVE', -78, 9.2, 95, 'v2.1.5', 'v3.0', '常规', 'admin', NOW(), DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

-- 正常设备：在线+电量正常
('DEV-CAP-006', '瘤胃胶囊C006', 'CAPSULE', 'EUI-CAP-006', 'TB-CAP-006', 'RM-2000', '2023-05-01', 'ACTIVE', -82, 8.8, 92, 'v1.2.0', 'v2.1', '常规', 'admin', NOW(), DATE_SUB(NOW(), INTERVAL 45 MINUTE)),

-- 闲置设备：无问题
('DEV-TRK-005', '追踪器T005', 'TRACKER', 'EUI-TRK-005', 'TB-TRK-005', 'ET-500', '2023-05-15', 'IDLE', -90, 7.5, 88, 'v2.1.5', 'v3.0', '省电', 'admin', NOW(), DATE_SUB(NOW(), INTERVAL 1 HOUR)),

-- 维保中设备
('DEV-CAP-007', '瘤胃胶囊C007', 'CAPSULE', 'EUI-CAP-007', 'TB-CAP-007', 'RM-2000', '2023-06-01', 'MAINTENANCE', NULL, NULL, NULL, 'v1.1.0', 'v2.0', '常规', 'admin', NOW(), NULL),

-- 库存中设备
('DEV-TRK-006', '追踪器T006', 'TRACKER', 'EUI-TRK-006', 'TB-TRK-006', 'ET-500', '2023-06-15', 'IN_STOCK', NULL, NULL, 100, 'v2.1.5', 'v3.0', '常规', 'admin', NOW(), NULL);

-- 4. 新增一个牲畜，用于绑定测试
-- -----------------------------------------------------------------
INSERT INTO ah_animal (id, ear_tag_id, name, type, herd_id, enclosure, gender, birth_date, weight_kg, height, health_status, health_score, latest_temperature, latest_activity, latest_steps, ai_conclusion, last_location_lon, last_location_lat, create_by, create_time) VALUES
('ANIMAL-005', 'EAR-005', '白雪', 'DAIRY_COW', 'HERD-001', 'A-10', '2', '2022-08-20', 520.75, 148.0, 'ALARM', 45, 40.8, 1200, 2800, '长时间离线且低电量，设备可能故障，请立即检查！', 119.755, 49.205, 'admin', NOW());

-- 5. 补充设备绑定关系
-- -----------------------------------------------------------------
INSERT INTO ah_animal_device_link (id, animal_id, device_id, bind_time, is_active) VALUES
(UUID(), 'ANIMAL-004', 'DEV-CAP-004', DATE_SUB(NOW(), INTERVAL 50 DAY), 1),  -- 黑旋风绑定问题设备
(UUID(), 'ANIMAL-005', 'DEV-CAP-005', DATE_SUB(NOW(), INTERVAL 30 DAY), 1),  -- 白雪绑定离线设备
(UUID(), 'ANIMAL-001', 'DEV-TRK-004', DATE_SUB(NOW(), INTERVAL 20 DAY), 1);  -- 大壮绑定第二个设备（追踪器）

-- 6. 补充相关告警记录
-- -----------------------------------------------------------------
INSERT INTO ah_alarm_record (id, animal_id, alarm_type, alarm_level, alarm_content, alarm_time, status, handler_id, handle_time, handle_notes, create_time) VALUES
(UUID(), 'ANIMAL-004', 'a_device_offline', 'WARN', '绑定设备[瘤胃胶囊C004]离线超过12小时', DATE_SUB(NOW(), INTERVAL 1 DAY), 'PENDING', NULL, NULL, NULL, NOW()),
(UUID(), 'ANIMAL-005', 'a_device_offline', 'CRITICAL', '绑定设备[瘤胃胶囊C005]离线超过24小时', DATE_SUB(NOW(), INTERVAL 12 HOUR), 'PENDING', NULL, NULL, NULL, NOW()),
(UUID(), 'ANIMAL-005', 'a_temp_high', 'CRITICAL', '体温达到40.8℃，可能与设备离线有关', DATE_SUB(NOW(), INTERVAL 6 HOUR), 'PENDING', NULL, NULL, NULL, NOW());

-- =================================================================
-- 验证查询：执行以下查询来验证测试数据的效果
-- =================================================================

-- 查询1：设备监控KPI统计
SELECT '=== 设备监控KPI验证 ===' as info;
SELECT 
    COUNT(*) AS total_devices,
    SUM(CASE WHEN d.update_time >= DATE_SUB(NOW(), INTERVAL 12 HOUR) THEN 1 ELSE 0 END) AS online_devices,
    SUM(CASE WHEN d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) OR d.update_time IS NULL THEN 1 ELSE 0 END) AS offline_devices,
    SUM(CASE WHEN d.battery_level < 20 THEN 1 ELSE 0 END) AS low_battery_devices
FROM ah_device d 
WHERE d.status IN ('ACTIVE', 'IDLE');

-- 查询2：问题设备列表
SELECT '=== 问题设备列表验证 ===' as info;
SELECT 
    d.name,
    d.device_type,
    d.dev_eui,
    d.battery_level,
    d.update_time,
    a.ear_tag_id AS bound_animal,
    CASE 
        WHEN d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) AND d.battery_level < 20 THEN '离线+低电量'
        WHEN d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) OR d.update_time IS NULL THEN '仅离线'
        WHEN d.battery_level < 20 THEN '仅低电量'
        ELSE '正常'
    END AS problem_type
FROM ah_device d
LEFT JOIN ah_animal_device_link adl ON d.id = adl.device_id AND adl.is_active = 1
LEFT JOIN ah_animal a ON adl.animal_id = a.id
WHERE d.status IN ('ACTIVE', 'IDLE')
  AND (d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) OR d.update_time IS NULL OR d.battery_level < 20)
ORDER BY 
    CASE WHEN d.battery_level < 20 THEN 1 ELSE 2 END,
    d.battery_level ASC,
    d.update_time ASC;

-- 查询3：设备状态分布
SELECT '=== 最新设备状态分布 ===' as info;
SELECT 
    status,
    COUNT(*) as count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM ah_device), 1), '%') as percentage
FROM ah_device 
GROUP BY status 
ORDER BY count DESC;

-- 查询4：最新告警统计
SELECT '=== 最新告警统计 ===' as info;
SELECT 
    alarm_type,
    alarm_level,
    status,
    COUNT(*) as count
FROM ah_alarm_record 
GROUP BY alarm_type, alarm_level, status
ORDER BY alarm_level DESC, count DESC; 