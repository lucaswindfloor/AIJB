-- =================================================================
-- 查看现有模拟数据情况
-- 用于了解当前数据库中已有的测试数据，然后在此基础上进行改进
-- =================================================================

-- 1. 查看现有的畜群数据
SELECT '=== 畜群数据 ===' as info;
SELECT id, name, manager_id, description, create_time FROM ah_herd ORDER BY create_time;

-- 2. 查看现有的牲畜数据
SELECT '=== 牲畜数据 ===' as info;
SELECT 
    id, 
    ear_tag_id, 
    name, 
    type, 
    herd_id, 
    health_status, 
    health_score,
    latest_temperature,
    latest_activity,
    ai_conclusion,
    create_time 
FROM ah_animal 
ORDER BY create_time;

-- 3. 查看现有的设备数据
SELECT '=== 设备数据 ===' as info;
SELECT 
    id,
    name,
    device_type,
    dev_eui,
    tb_device_id,
    status,
    rssi,
    lo_ra_snr,
    battery_level,
    firmware_version,
    update_time,
    create_time
FROM ah_device 
ORDER BY create_time;

-- 4. 查看设备状态分布
SELECT '=== 设备状态分布 ===' as info;
SELECT 
    status,
    COUNT(*) as count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM ah_device), 1), '%') as percentage
FROM ah_device 
GROUP BY status 
ORDER BY count DESC;

-- 5. 查看设备类型分布
SELECT '=== 设备类型分布 ===' as info;
SELECT 
    device_type,
    COUNT(*) as count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM ah_device), 1), '%') as percentage
FROM ah_device 
GROUP BY device_type 
ORDER BY count DESC;

-- 6. 查看设备绑定关系
SELECT '=== 设备绑定关系 ===' as info;
SELECT 
    a.ear_tag_id as animal_ear_tag,
    a.name as animal_name,
    d.name as device_name,
    d.device_type,
    d.status as device_status,
    d.battery_level,
    d.update_time as last_update,
    adl.bind_time,
    adl.is_active
FROM ah_animal_device_link adl
JOIN ah_animal a ON adl.animal_id = a.id
JOIN ah_device d ON adl.device_id = d.id
ORDER BY adl.is_active DESC, adl.bind_time DESC;

-- 7. 查看告警记录
SELECT '=== 告警记录 ===' as info;
SELECT 
    ar.alarm_type,
    ar.alarm_level,
    ar.alarm_content,
    ar.alarm_time,
    ar.status,
    a.ear_tag_id as animal_ear_tag,
    a.name as animal_name
FROM ah_alarm_record ar
JOIN ah_animal a ON ar.animal_id = a.id
ORDER BY ar.alarm_time DESC;

-- 8. 模拟设备监控KPI统计（当前情况）
SELECT '=== 当前设备KPI统计 ===' as info;
SELECT
    COUNT(*) AS total_devices,
    SUM(CASE WHEN d.update_time >= DATE_SUB(NOW(), INTERVAL 12 HOUR) THEN 1 ELSE 0 END) AS online_devices,
    SUM(CASE WHEN d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) THEN 1 ELSE 0 END) AS offline_devices,
    SUM(CASE WHEN d.battery_level < 20 THEN 1 ELSE 0 END) AS low_battery_devices,
    SUM(CASE WHEN d.battery_level IS NULL THEN 1 ELSE 0 END) AS no_battery_info
FROM ah_device d;

-- 9. 模拟问题设备列表（当前情况）
SELECT '=== 当前问题设备列表 ===' as info;
SELECT 
    d.id,
    d.name,
    d.device_type,
    d.dev_eui,
    d.status,
    d.battery_level,
    d.update_time,
    a.ear_tag_id AS bound_animal,
    CASE 
        WHEN d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) THEN '离线'
        ELSE '在线'
    END AS online_status,
    CASE 
        WHEN d.battery_level < 20 THEN '低电量'
        WHEN d.battery_level IS NULL THEN '无电量信息'
        ELSE '电量正常'
    END AS battery_status,
    CASE
        WHEN d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) AND d.battery_level < 20 THEN '离线+低电量'
        WHEN d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) THEN '仅离线'
        WHEN d.battery_level < 20 THEN '仅低电量'
        ELSE '正常'
    END AS problem_type
FROM ah_device d
LEFT JOIN ah_animal_device_link adl ON d.id = adl.device_id AND adl.is_active = 1
LEFT JOIN ah_animal a ON adl.animal_id = a.id
WHERE d.status IN ('ACTIVE', 'IDLE') 
  AND (d.update_time < DATE_SUB(NOW(), INTERVAL 12 HOUR) OR d.battery_level < 20)
ORDER BY 
    CASE WHEN d.battery_level < 20 THEN 1 ELSE 2 END,  -- 低电量优先
    d.battery_level ASC,  -- 电量从低到高
    d.update_time ASC;    -- 离线时间从长到短

-- 10. 查看数据时间分布（了解数据的时效性）
SELECT '=== 数据时间分布 ===' as info;
SELECT 
    'ah_device' as table_name,
    MIN(create_time) as earliest_create,
    MAX(create_time) as latest_create,
    MIN(update_time) as earliest_update,
    MAX(update_time) as latest_update,
    COUNT(*) as total_records
FROM ah_device
UNION ALL
SELECT 
    'ah_animal' as table_name,
    MIN(create_time) as earliest_create,
    MAX(create_time) as latest_create,
    MIN(update_time) as earliest_update,
    MAX(update_time) as latest_update,
    COUNT(*) as total_records
FROM ah_animal
UNION ALL
SELECT 
    'ah_alarm_record' as table_name,
    MIN(create_time) as earliest_create,
    MAX(create_time) as latest_create,
    NULL as earliest_update,
    NULL as latest_update,
    COUNT(*) as total_records
FROM ah_alarm_record; 