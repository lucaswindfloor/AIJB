# TDengine时序数据库 - 执行指南

## 1. 连接到TDengine

您已经成功连接到TDengine数据库，使用以下命令：

```bash
taos -h 172.22.2.120 -P 6030 -u root
# 密码：taosdata (如果有密码的话)
```

## 2. 执行步骤

### 第一步：创建数据库和表结构
在TDengine命令行中执行建表脚本：

```sql
-- 复制并粘贴 TDengine时序数据库建表.sql 中的所有内容
-- 或者通过文件导入：
\. TDengine时序数据库建表.sql
```

### 第二步：插入测试数据
执行测试数据脚本：

```sql
-- 复制并粘贴 TDengine遥测测试数据.sql 中的所有内容
-- 或者通过文件导入：
\. TDengine遥测测试数据.sql
```

## 3. 验证数据

### 检查表结构
```sql
USE animal_husbandry;
SHOW STABLES;
SHOW TABLES;
```

### 查看数据统计
```sql
-- 查看总记录数
SELECT COUNT(*) as total_records FROM telemetry_data;

-- 按设备查看记录数
SELECT device_id, COUNT(*) as record_count 
FROM telemetry_data 
GROUP BY device_id;
```

### 查看具体数据样例
```sql
-- 查看瘤胃胶囊数据
SELECT ts, temperature, activity_level, ph_value, battery_level 
FROM telemetry_DEV_CAP_001 
ORDER BY ts DESC LIMIT 10;

-- 查看追踪器数据
SELECT ts, steps, longitude, latitude, battery_level 
FROM telemetry_DEV_TRK_001 
ORDER BY ts DESC LIMIT 10;

-- 查看告警状态设备的高体温数据
SELECT ts, temperature, activity_level 
FROM telemetry_DEV_CAP_002 
WHERE temperature > 40.0 
ORDER BY ts DESC;
```

## 4. 常用查询示例

### 时间范围查询
```sql
-- 查询最近24小时的数据
SELECT * FROM telemetry_DEV_CAP_001 
WHERE ts >= NOW - 1d;

-- 查询指定时间段的数据
SELECT * FROM telemetry_DEV_CAP_001 
WHERE ts >= '2025-01-15 00:00:00' 
AND ts <= '2025-01-16 23:59:59';
```

### 聚合查询
```sql
-- 按小时统计平均体温
SELECT _wstart, AVG(temperature) as avg_temp 
FROM telemetry_DEV_CAP_001 
WHERE ts >= '2025-01-15 00:00:00' 
INTERVAL(1h);

-- 查看电量变化趋势
SELECT _wstart, FIRST(battery_level) as battery 
FROM telemetry_DEV_TRK_002 
WHERE ts >= '2025-01-15 00:00:00' 
INTERVAL(2h);
```

### 跨设备查询
```sql
-- 查询所有瘤胃胶囊的最新体温
SELECT device_id, LAST(temperature) as latest_temp, LAST(ts) as last_time
FROM telemetry_data 
WHERE device_type = 'CAPSULE' 
GROUP BY device_id;

-- 查询所有追踪器的最新位置
SELECT device_id, LAST(longitude) as lon, LAST(latitude) as lat, LAST(ts) as last_time
FROM telemetry_data 
WHERE device_type = 'TRACKER' 
GROUP BY device_id;
```

## 5. 数据库说明

### 数据库配置
- **数据库名称**: `animal_husbandry`
- **数据保留时间**: 10年 (3650天)
- **数据分片**: 30天一个文件
- **缓存块数**: 6个

### 超级表结构

#### telemetry_data (遥测数据)
- **时间列**: ts (时间戳)
- **数据列**: 
  - temperature (体温) - 瘤胃胶囊
  - activity_level (活动量) - 瘤胃胶囊  
  - ph_value (pH值) - 瘤胃胶囊
  - steps (步数) - 追踪器
  - longitude/latitude (经纬度) - 追踪器
  - battery_level/rssi/snr (通用指标)
- **标签**: device_id, device_type, dev_eui, animal_id

#### device_status (设备状态)
- **时间列**: ts (时间戳)
- **数据列**: status, battery_level, rssi, firmware_version
- **标签**: device_id, device_type, dev_eui

### 子表命名规则
- 遥测数据子表: `telemetry_DEV_CAP_001`, `telemetry_DEV_TRK_001`
- 设备状态子表: `status_DEV_CAP_001`, `status_DEV_TRK_001`

## 6. 与MySQL的数据同步

TDengine存储完整的历史数据，MySQL中的`ah_telemetry_latest`表存储最新快照：

```sql
-- MySQL中的最新数据快照示例
SELECT device_id, 
       JSON_EXTRACT(telemetry_data, '$.temperature') as temperature,
       JSON_EXTRACT(telemetry_data, '$.battery_level') as battery,
       last_update_time
FROM ah_telemetry_latest;
```

## 7. 性能优化建议

1. **查询优化**: 
   - 总是在WHERE子句中包含时间范围
   - 使用设备标签进行过滤

2. **索引策略**:
   - TDengine自动为时间列和标签列创建索引
   - 无需手动创建额外索引

3. **数据写入**:
   - 批量写入性能更佳
   - 按时间顺序写入效率最高

## 8. 故障排查

### 常见问题
1. **连接失败**: 检查网络和端口6030
2. **权限问题**: 确认用户权限
3. **内存不足**: 检查TDengine配置

### 日志查看
```bash
# TDengine日志位置（Linux）
tail -f /var/log/taos/taosd.log

# Windows日志位置
# C:\TDengine\log\taosd.log
``` 