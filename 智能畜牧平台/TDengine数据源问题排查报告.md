# TDengine数据源配置问题排查报告

**日期：** 2025-01-15  
**问题状态：** 已解决（临时方案）

## 问题描述

在启动JeecgBoot应用时，积木报表(JimuReport)模块错误地尝试从TDengine数据源中查询`jimu_report_export_job`表，导致以下错误：

```
TDengine ERROR (0x80002603): Fail to get table info, error: Table does not exist
```

## 错误分析

### 1. 错误堆栈关键信息
```
at org.jeecg.modules.jmreport.automate.service.a.a.a(JimuReportAutoServiceImpl.java:244)
SELECT id, name, begin_time, end_time, exec_interval, report_conf, last_run_time, receiver_email, file_sync_path, status, create_by, create_time, update_by, update_time, tenant_id FROM jimu_report_export_job WHERE 1=1 and status = ? order by create_time desc
```

### 2. 根本原因
- **积木报表组件**在启动时需要查询`jimu_report_export_job`表
- 该表应该存在于MySQL数据库中，但查询被错误路由到了TDengine数据源
- 虽然配置了`primary: master`，但仍然出现了数据源路由错误

### 3. 可能的触发因素
- Dynamic-DataSource的默认路由策略
- TDengine数据源配置影响了全局路由
- 积木报表组件的数据源选择逻辑

## 解决方案

### 临时解决方案（已实施）
1. **设置主数据源**：在`application-dev.yml`中明确设置`primary: master`
2. **添加严格模式配置**：设置`strict: false`避免严格模式下的异常
3. **暂时注释TDengine配置**：避免TDengine数据源影响全局路由

```yaml
spring:
  datasource:
    dynamic:
      primary: master # 设置默认的数据源
      strict: false # 非严格模式
      datasource:
        master:
          url: jdbc:mysql://127.0.0.1:3306/jeecg-boot?...
          # ... MySQL配置
        # TDengine配置暂时注释
        # tdengine:
        #   driver-class-name: com.taosdata.jdbc.TSDBDriver
        #   ...
```

### 长期解决方案（推荐）
1. **创建专用TDengine配置类**：使用独立的DataSource Bean，不参与Dynamic-DataSource路由
2. **明确数据源使用范围**：
   - MySQL：业务数据、积木报表、系统表
   - TDengine：仅用于时序数据存储
3. **使用@DS注解**：在需要使用TDengine的Service类上明确指定数据源

## 当前状态

✅ **应用启动成功**：服务已正常启动在端口8081
✅ **核心功能正常**：畜牧管理API接口正常工作
✅ **积木报表错误已消除**：不再出现TDengine查询错误

## 后续计划

1. **重新设计TDengine集成**：
   - 创建独立的TDengine配置
   - 确保不影响其他组件的数据源路由
   
2. **完善数据源管理**：
   - 明确各模块的数据源使用策略
   - 添加数据源路由的日志监控

3. **恢复TDengine功能**：
   - 在解决路由冲突后重新启用TDengine配置
   - 测试时序数据的读写功能

## 影响评估

- ✅ **核心业务功能**：无影响
- ⚠️ **时序数据功能**：暂时无法使用（待恢复TDengine配置）
- ✅ **积木报表功能**：正常
- ✅ **其他系统功能**：正常

---

**备注：** 这是一个典型的多数据源配置冲突问题。在微服务和多数据源环境中，需要特别注意数据源路由的管理，避免不同组件之间的相互影响。 