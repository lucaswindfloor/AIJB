# JeecgBoot项目架构指南

**本文档是项目的“宪法”和“蓝图”，定义了所有开发者都必须理解和遵守的全局规则、设计原则和协作契约。**

## 1. 项目架构层面

### 1.1. 整体架构模式

**架构定位：AI低代码平台**
- **前后端分离架构**：前端Vue3 + 后端Spring Boot完全分离
- **单体与微服务双架构支持**：
  - 单体模式：`jeecg-module-system`（统一启动）
  - 微服务模式：`jeecg-server-cloud`（Spring Cloud Alibaba）
- **微前端架构支持**：基于qiankun的微前端框架，支持子应用集成
- **低代码平台架构**：OnlineCoding + 代码生成器 + 手工MERGE
- **AI赋能架构**：集成AIGC能力，支持AI流程编排、知识库问答

### 1.2. 技术架构选型

**核心定位：Java AI Low Code Platform**

**后端技术栈（更精确的版本）：**
- **核心框架**：Spring Boot 2.7.18 + Spring Cloud Alibaba 2021.0.6.2
- **持久化**：MyBatis-Plus 3.5.3.2 + Druid 1.2.24
- **安全框架**：Apache Shiro 1.13.0 + JWT 4.5.0
- **微服务组件**：Nacos + Gateway + Sentinel + Skywalking
- **报表引擎**：JimuReport 1.9.5（积木报表）
- **AI集成**：支持ChatGPT、DeepSeek、Ollama、智普、千问等大模型
- **信创支持**：达梦、人大金仓、TiDB等国产数据库

**前端技术栈（最新技术栈）：**
- **核心框架**：Vue 3.5.13 + TypeScript 4.9.5
- **构建工具**：Vite 6.0.7（要求Node.js 20+）
- **UI框架**：Ant Design Vue 4.2.6
- **状态管理**：Pinia 2.1.7 + Vue Router 4.5.0
- **微前端**：qiankun微前端框架支持
- **图表库**：ECharts 5.6.0 + VXE Table
- **工程化**：UnoCSS + ESLint + Prettier + TypeScript

### 1.3. 模块划分和依赖关系

**Maven多模块架构：**
```
jeecg-boot-parent (3.8.0)
├── jeecg-boot-base-core           # 框架核心基础
│   ├── common/                    # 公共工具类
│   ├── config/                    # 自动配置类
│   └── modules/                   # 基础模块
├── jeecg-module-system            # 系统管理模块（单体架构）
│   ├── jeecg-system-api          # 系统API接口定义
│   ├── jeecg-system-biz          # 系统业务逻辑实现
│   └── jeecg-system-start        # 系统启动入口
├── jeecg-server-cloud             # 微服务架构模块
│   ├── jeecg-cloud-gateway       # 网关服务
│   ├── jeecg-cloud-nacos         # 注册中心
│   ├── jeecg-system-cloud-start  # 系统微服务
│   ├── jeecg-demo-cloud-start    # 示例微服务
│   └── jeecg-visual              # 监控可视化
└── jeecg-boot-module             # 业务功能模块
    ├── jeecg-module-demo         # 示例模块
    ├── jeecg-boot-module-airag   # AI RAG模块
    └── jeecg-boot-module-parking # 停车管理模块（自定义）
```

**前端模块组织：**
```
jeecgboot-vue3/
├── src/
│   ├── components/               # 公共组件库
│   ├── layouts/                  # 布局组件
│   ├── views/                    # 页面组件
│   ├── router/                   # 路由管理
│   ├── store/                    # Pinia状态管理
│   ├── hooks/                    # 组合式函数
│   ├── utils/                    # 工具函数
│   ├── api/                      # API接口（全局，已弃用）
│   └── qiankun/                  # 微前端支持
├── build/                        # 构建配置
├── types/                        # TypeScript类型定义
└── mock/                         # Mock数据
```

### 1.4. 数据流和交互模式

**统一API规范：**
- **请求格式**：RESTful API设计
- **返回格式**：`Result<T>`统一包装
  ```java
  public class Result<T> {
      private boolean success;      // 成功标志
      private String message;       // 返回消息
      private Integer code;         // 返回代码
      private T result;            // 数据对象
      private long timestamp;      // 时间戳
  }
  ```

**分页查询标准：**
- **分页参数**：`pageNo`、`pageSize`
- **返回格式**：`IPage<T>`（MyBatis-Plus分页）
- **排序支持**：`column`、`order`参数

**权限控制流程：**
- **登录认证**：用户名密码 → JWT Token → Redis缓存
- **权限验证**：Shiro权限注解 → 数据权限过滤
- **菜单渲染**：后端菜单配置 → 前端动态路由

**微前端通信：**
- **主子应用**：qiankun框架支持
- **状态共享**：全局状态管理
- **路由同步**：主应用路由控制

**AI功能集成：**
- **模型管理**：支持多种AI大模型切换
- **知识库**：RAG检索增强生成
- **流程编排**：可视化AI工作流设计
- **代码生成**：AI辅助代码生成
- **流程引擎**：工作流设计和执行

**低代码能力：**
- **在线表单**：OnlineCoding零代码开发
- **报表设计**：积木报表可视化设计
- **代码生成**：基于数据库表自动生成CRUD
- **流程引擎**：工作流设计和执行

### 1.5. 架构特色和创新点

1. **AIGC + 低代码融合**：将AI能力与低代码平台深度结合
2. **双架构支持**：同时支持单体和微服务两种架构模式
3. **微前端原生支持**：内置qiankun微前端解决方案
4. **信创国产化**：全面支持国产操作系统和数据库
5. **开发模式创新**：AIGC生成 → OnlineCoding → 代码生成 → 手工MERGE

### 1.6. 开发基本原则

1.  **优先遵循框架既有模式**：在开发新功能时，必须首先研究项目中已有的类似功能的实现方式。特别是对于Controller、Service、Entity等核心组件的实现，应最大限度地复用基类和遵循框架约定。
    *   **案例：Controller最佳实践——"继承并复写"模式**：
        *   **错误做法**：仅继承`JeecgController`而不实现任何方法。这种方式虽然看似简洁，但存在风险，可能因编译优化或框架加载机制等原因，导致父类中的方法路由没有被成功注册，从而引发"404 Not Found"错误。
        *   **正确做法**：采用"继承并复写"的混合模式，这是官方`jeecg-module-demo`模块所遵循的最佳实践。
            1.  **继承**：Controller类必须继承 `JeecgController<Entity, Service>`，以复用框架提供的大量CRUD基础逻辑。
            2.  **复写**：在子类中，显式地将需要暴露的接口（如 `queryPageList`, `add` 等）重写一遍。
            3.  **注解**：为重写的方法添加明确的路由注解（如 `@GetMapping("/list")`）和API文档注解（`@ApiOperation`），这能确保路由被稳定注册，并能生成更精确的API文档。
            4.  **调用**：在方法体中，**不应该**调用 `super.xxx()` 方法，因为基类并未提供这些可供直接复写调用的公共方法。正确的做法是直接注入并调用 `service` 实例（例如 `IPage<AhDevice> pageList = ahDeviceService.page(page, queryWrapper);`）来完成业务逻辑。这种模式确保了路由和注解的规范性，同时将业务实现委托给服务层。
        *   **总结**：此模式兼顾了代码复用、功能健壮性和未来可扩展性，应作为所有标准CRUD功能开发的首选模式。
2.  **依赖统一管理**：业务模块（如`jeecg-boot-module-xxx`）的`pom.xml`应保持简洁，仅依赖`jeecg-boot-base-core`。所有公共依赖（如`swagger`、`mybatis-plus`等）由父项目统一管理，避免在子模块中单独引入，防止版本冲突和不一致。
3.  **坚持统一的命名规范，避免Bean冲突**：
    *   **问题背景**: 在多模块项目中，不同业务模块（如 `animalhusbandry` 和 `parking`）可能包含概念上相同的实体（如 `Device`）。如果直接使用 `DeviceController`, `DeviceService` 等通用名称，在Spring容器启动时会因为存在多个同名Bean而导致 `ConflictingBeanDefinitionException` 错误，造成应用启动失败。
    *   **最佳实践**: 为每个业务模块定义一个独特的、简短的 **前缀** (例如 `animalhusbandry` -> `Ah`, `parking` -> `P`)。在模块内部，所有自主开发的类，包括 `Controller`, `Service`, `Entity`, `Mapper` 等，都应以此为前缀命名 (例如 `AhDeviceController`, `AhDeviceService`)。
    *   **收益**: 这种命名约定可以从根本上杜绝Bean命名冲突，提高代码的可读性和模块的独立性。
4.  **保持API注解版本一致性**：
    *   **问题背景**: 项目中可能存在不同版本的API文档库（如 Swagger 2 和 Swagger 3/OpenAPI 3）。混用不同版本的注解（例如同时使用 Swagger 2 的 `@Api` 和 Swagger 3 的 `@Tag`）会导致注解无法被正确解析，API文档生成不完整或出现编译时/运行时错误。
    *   **最佳实践**: 在开始编码前，通过检查项目中已有的Controller（如 `RumenCapsuleController.java`）来确定项目当前使用的API注解版本。JeecgBoot V3.5+ 推荐使用 **Swagger 3 / OpenAPI 3** 注解。
    *   **注解对应关系**:
        *   `@Api` (Swagger 2) -> `@Tag` (Swagger 3)
        *   `@ApiOperation` (Swagger 2) -> `@Operation` (Swagger 3)
        *   `@ApiModel` (Swagger 2) -> `@Schema` (Swagger 3)
        *   `@ApiModelProperty` (Swagger 2) -> `@Schema(description = "...")` (Swagger 3)
    *   **收益**: 确保API文档的正确生成，避免因注解版本不匹配引发的各种奇怪问题。

## 2. 业务层面

### 2.1. 功能模块清单（详细分析）

**核心系统管理模块：**

| 模块分类 | 功能模块 | 控制器 | 主要功能 | 业务边界 |
|---------|---------|--------|----------|---------|
| **用户权限管理** | 用户管理 | SysUserController | 用户CRUD、权限分配、部门关联、代理人设置 | 用户生命周期管理 |
| | 角色管理 | SysRoleController | 角色CRUD、权限分配、数据权限 | 角色权限体系 |
| | 菜单权限管理 | SysPermissionController | 菜单树管理、按钮权限、数据权限规则 | 功能权限控制 |
| | 部门管理 | SysDepartController | 组织架构、部门用户、部门权限 | 组织结构管理 |
| | 职位管理 | SysPositionController | 职位定义、职位权限 | 岗位权限管理 |
| **租户管理** | 多租户SAAS | SysTenantController | 租户创建、权限包、用户邀请、数据隔离 | SAAS多租户架构 |
| **系统配置** | 数据字典 | SysDictController | 字典管理、字典项、缓存同步 | 系统配置数据 |
| | 系统参数 | - | 系统参数配置、动态配置 | 全局配置管理 |
| | 数据源管理 | SysDataSourceController | 多数据源配置、动态切换 | 数据源管理 |
| **消息通知** | 系统公告 | SysAnnouncementController | 公告发布、用户通知、消息推送 | 系统消息管理 |
| | 消息中心 | MessageController | WebSocket实时消息、短信邮件 | 即时通讯 |
| **文件管理** | 文件上传 | SysUploadController | 本地/OSS/MinIO上传、文件预览 | 文件存储管理 |
| | OSS配置 | OSSController | 云存储配置、文件管理 | 云存储服务 |
| **定时任务** | 任务调度 | QuartzJobController | Quartz任务管理、执行监控 | 定时任务管理 |
| **系统监控** | 在线用户 | SysUserOnlineController | 在线用户监控、强制下线 | 用户会话管理 |
| | 系统日志 | SysLogController | 操作日志、登录日志、异常日志 | 系统审计 |
| | 数据日志 | SysDataLogController | 数据变更记录、操作追踪 | 数据审计 |
| **第三方集成** | 第三方登录 | ThirdLoginController | 微信、钉钉、企业微信登录 | 第三方认证 |
| | API开放平台 | ThirdAppController | API密钥管理、接口授权 | 开放API管理 |
| **流程引擎** | 工作流 | - | 流程设计、任务处理、审批流转 | 业务流程管理 |

**AI智能模块：**

| 模块 | 功能 | 说明 |
|------|------|------|
| AI应用平台 | AI流程编排、模型管理 | 类似Dify的AIGC应用开发平台 |
| AI知识库 | 文档导入、RAG问答 | 支持Markdown、PDF格式保持 |
| AI对话助手 | 智能问答、图片对话 | 多模态AI交互 |
| AI代码生成 | 表单设计、字段建议 | AI辅助开发 |

**低代码平台模块：**

| 模块 | 功能 | 说明 |
|------|------|------|
| 在线表单 | OnlineCoding | 零代码表单设计 |
| 报表设计 | 积木报表 | 可视化报表设计器 |
| 代码生成器 | CRUD生成 | 基于数据库表自动生成代码 |
| 表单设计器 | 拖拽式设计 | 可视化表单设计 |

### 2.2. 业务流程和数据流向

**用户认证授权流程：**
```
用户登录 → 验证码校验 → 用户名密码验证 → 生成JWT Token → 
缓存用户信息(Redis) → 加载用户权限 → 加载菜单树 → 前端渲染
```

**权限验证流程：**
```
API请求 → JWT Token解析 → 用户信息获取 → Shiro权限验证 → 
@RequiresPermissions注解检查 → @PermissionData数据权限过滤 → 业务处理
```

**菜单权限加载：**
```
用户登录 → 查询用户角色 → 查询角色权限 → 构建菜单树 → 
前端动态路由 → 按钮权限控制 → 数据权限过滤
```

**多租户数据流：**
```
请求携带租户信息 → 租户上下文设置 → 数据源动态切换 → 
租户数据隔离 → 业务数据查询 → 结果返回
```

**AI功能集成流程：**
```
用户输入 → AI模型调用 → 知识库检索(RAG) → AI推理生成 → 
结果后处理 → 格式保持 → 前端渲染展示
```

### 2.3. 权限体系和安全机制

**权限模型：RBAC + 数据权限**

**权限层级结构：**
```
用户(User) → 角色(Role) → 权限(Permission) → 资源(Resource)
      ↓           ↓           ↓              ↓
   部门权限    角色权限    菜单/按钮权限    数据权限
```

**权限实体设计：**

- **SysUser（用户）**：基础用户信息、状态、部门关联
- **SysRole（角色）**：角色定义、角色编码、租户隔离
- **SysPermission（权限）**：菜单权限、按钮权限、数据权限规则
- **SysUserRole（用户角色）**：用户角色关联关系
- **SysRolePermission（角色权限）**：角色权限关联关系

**权限注解体系：**
```java
// 功能权限
@RequiresPermissions("system:user:list")

// 数据权限
@PermissionData(pageComponent = "system/UserList")

// 角色权限  
@RequiresRoles("admin")
```

**数据权限策略：**
- **全部权限**：查看所有数据
- **本人权限**：只能查看自己的数据
- **本部门权限**：查看本部门数据
- **本部门及子部门权限**：查看本部门及下级部门数据
- **自定义权限**：根据自定义规则过滤数据

**安全防护机制：**
- **登录安全**：验证码、登录失败次数限制、密码加密存储
- **会话管理**：JWT Token、Redis会话缓存、单点登录
- **接口安全**：签名验证、重放攻击防护、接口限流
- **数据安全**：SQL注入防护、XSS防护、CSRF防护
- **审计安全**：操作日志、数据变更日志、登录日志

### 2.4. API接口规范和约定

**统一返回格式：**
```java
public class Result<T> {
    private boolean success;      // 成功标志
    private String message;       // 返回消息  
    private Integer code;         // 返回代码(200成功，500失败)
    private T result;            // 业务数据
    private long timestamp;      // 时间戳
}
```

**标准HTTP状态码：**
- **200**：请求成功
- **500**：服务器内部错误
- **401**：未授权（Token失效）
- **403**：权限不足
- **412**：前置条件失败（验证码错误）

**分页查询规范：**
```java
// 请求参数
pageNo: 1           // 页码（从1开始）
pageSize: 10        // 每页条数
column: createTime  // 排序字段
order: desc         // 排序方式

// 返回格式
IPage<T> {
    current: 1,      // 当前页
    size: 10,        // 每页条数  
    total: 100,      // 总记录数
    pages: 10,       // 总页数
    records: []      // 数据列表
}
```

**RESTful API设计：**
```
GET    /sys/user/list           # 查询用户列表
POST   /sys/user/add            # 新增用户
PUT    /sys/user/edit           # 编辑用户  
DELETE /sys/user/delete         # 删除用户
GET    /sys/user/queryById      # 根据ID查询
```

**权限接口命名规范：**
```
system:user:list        # 用户列表查询权限
system:user:add         # 用户新增权限
system:user:edit        # 用户编辑权限
system:user:delete      # 用户删除权限
system:user:export      # 用户导出权限
system:user:import      # 用户导入权限
```

**特殊接口约定：**
- **批量操作**：统一使用`/deleteBatch`、`/updateBatch`等后缀
- **导入导出**：统一使用`/exportXls`、`/importExcel`等
- **状态变更**：如`/frozenBatch`（批量冻结）、`/resume`（启用）
- **回收站**：如`/putRecycleBin`（放入回收站）、`/deleteRecycleBin`（彻底删除）

**请求头约定：**
```
X-Access-Token: JWT令牌
Content-Type: application/json
X-Sign: 接口签名（敏感接口）
Version: vue3（前端版本标识）
```

**异常处理规范：**
- **业务异常**：使用`JeecgBootException`
- **参数异常**：统一参数校验和错误提示
- **权限异常**：统一权限不足提示
- **系统异常**：统一异常日志记录

## 3. 微服务开发最佳实践与避坑指南

在将一个现有业务模块（如`animal-husbandry`）迁移或新建为一个独立的JeecgBoot微服务时，以下四个核心原则是确保成功的关键，它们能帮助我们避开99%的坑：

### 3.1. 依赖隔离原则：保持`pom.xml`的"洁癖"
- **问题根源**：微服务化的初衷就是隔离，但如果在 `pom.xml` 中错误地引入了 `jeecg-system-biz` 等包含过多业务的"大包"，就相当于把单体应用的核心业务又打包了进来，很容易导致意料之外的模块冲突（例如本项目中 `jimureport` 和 `tdengine` 的冲突）。
- **最佳实践**：一个标准的微服务启动模块（如`jeecg-animalhusbandry-cloud-start`），其`pom.xml`应该像`jeecg-demo-cloud-start`一样干净。它**只应包含**：
    1.  **微服务核心启动器**：`jeecg-boot-starter-cloud`
    2.  **自身的业务逻辑模块**：例如 `jeecg-boot-module-animalhusbandry`
    3.  **可选的系统API**：`jeecg-system-cloud-api`（如果需要跨服务调用系统接口）
    4.  **可选的功能启动器**：`jeecg-boot-starter-job`（如果需要定时任务）
    5.  **模块特有的JDBC驱动**：如 `taos-jdbcdriver`。这是因为核心启动器为了通用性，不会包含所有特定数据库的驱动。

### 3.2. 组件扫描黄金法则：启动类必须在根包
- **问题根源**：如果将启动类放在了子包（如 `org.jeecg.modules.animalhusbandry.start`）下，会导致Spring Boot的默认组件扫描范围极其有限，无法找到框架核心及业务模块中的任何Bean，从而引发连锁的 `NoSuchBeanDefinitionException`。
- **最佳实践**：微服务的启动类 **必须** 位于根包 `org.jeecg` 下。这是JeecgBoot框架的一个核心"约定"。只有这样，`@SpringBootApplication` 注解才能正确地扫描并加载所有依赖（starter、业务module）中的Bean。

### 3.3. Bean加载顺序原则：依赖注入的"优雅之道"
- **问题根源**：在修复了扫描问题后，可能会遇到自定义配置类（如 `TDengineConfig`）无法注入由框架自动配置的核心Bean（如 `DynamicRoutingDataSource`）的问题。其本质是用户自定义配置类的加载时机早于框架的自动配置类。
- **最佳实践**：当自定义的 `@Configuration` 类需要依赖一个由框架自动配置的Bean时，**不应**使用`@Autowired`字段注入。正确的做法是采用**方法参数注入**：
    ```java
    // 在@Bean方法上将依赖作为参数传入
    @Bean
    public JdbcTemplate myCustomJdbcTemplate(DynamicRoutingDataSource dataSource) {
        // Spring会确保在调用此方法前，dataSource这个Bean已准备就- 绪
        return new JdbcTemplate(dataSource.getDataSource("my_ds"));
    }
    ```
    这个模式从根本上解决了Bean加载顺序的冲突。

### 3.4. 对比分析原则：永远信任可以工作的代码 (Trust the Working Code)
- **问题根源**：在遇到复杂框架（如JeecgBoot）中的棘手问题时，很容易陷入"盲目试错"的循环，即根据错误信息随意修改配置或代码，导致引入更多问题。
- **最佳实践**：解决问题的最快路径，是**系统性地对比**故障模块与一个或多个**已知可成功运行的参照物**。在JeecgBoot项目中，有两个黄金参照物：
    1.  **`jeecg-demo-cloud-start`**：这是微服务架构下的最佳实践范本。
    2.  **单体启动模式**（`jeecg-system-start`）：这是功能最全、配置最完整的参照系。
- **具体实践方法**：
    *   **配置对比**：当遇到 `NullPointerException` 或配置相关的错误时，应立即逐行对比故障微服务的 Nacos 配置与单体应用的 `application-dev.yml`，找出缺失的配置项。
    *   **依赖对比**：当遇到 `NoClassDefFoundError` 或 `NoSuchMethodError` 时，应立即对比故障微服务的 `pom.xml` 与 `jeecg-demo-cloud-start` 的 `pom.xml`，找出多余或缺失的依赖。
    *   **代码结构对比**：当遇到 `NoSuchBeanDefinitionException` 或组件扫描问题时，应立即对比故障微服务的启动类、`@Configuration` 类的代码结构、包路径、注解使用方式与参照物有何不同。

## 4. 微服务前后端通信最佳实践

从单体架构切换到微服务架构时，前端如何正确调用后端微服务的API，是项目中最常见也最容易出错的环节。本章节总结了JeecgBoot在该场景下的标准工作流程与核心要点。

### 4.1. 核心原则

JeecgBoot的前端API调用模式非常统一，开发者**不需要为新模块做任何特殊的前端网络配置**。绝大部分工作都集中在**后端的网关路由配置**上。

### 4.2. 第一步：后端微服务开发（后端同学负责）

1.  **统一Controller路径规范**
    后端的`Controller`必须使用包含模块全名的`@RequestMapping`注解。这是前端和网关进行匹配的"契约"。
    ```java
    // 正确的例子 (DashboardController.java)
    @RestController
    @RequestMapping("/animal_husbandry/dashboard") // 必须包含"/animal_husbandry"
    public class DashboardController {
        @GetMapping("/kpi") // 方法路径
        public Result getKpiData(){...}
    }
    ```

2.  **确保服务成功注册**
    启动微服务后，必须在Nacos控制台的"服务列表"中看到它，并记下其**服务名**（例如 `jeecg-animalhusbandry`）。这个服务名是后续网关配置的寻址目标。

### 4.3. 第二步：前端模块开发（前端同学负责）

前端的工作非常简单，核心就是"模仿"：模仿 `system` 或 `dashboard` 等现有模块的写法。

1.  **遵循API文件规范**
    在你的模块目录下（例如 `views/animal_husbandry/`），创建 `api.ts` 文件。在文件中，**直接硬编码完整的后端Controller路径**，并使用**全局导出**的`defHttp`实例。
    ```typescript
    // 正确的例子 (dashboard.api.ts)
    import { defHttp } from '/@/utils/http/axios'; // 使用全局defHttp

    enum Api {
      // 直接硬编码, 与后端Controller的@RequestMapping完全对应
      GetKpiData = '/animal_husbandry/dashboard/kpi',
      GetRecentAlarms = '/animal_husbandry/dashboard/recent-alarms',
    }

    export const getKpiData = () => defHttp.get({ url: Api.GetKpiData });
    ```

2.  **无需任何特殊网络配置**
    您**不需要**也**不应该**为新模块创建专用的 `axios` 实例，或者修改任何路由辅助函数。前端的 `.env` 文件中配置的 `VITE_GLOB_API_URL=/jeecgboot` 会自动为所有请求加上全局前缀，这是框架的内建机制。

### 4.4. 第三步：网关路由配置（核心步骤）

这是连接前端和后端的"交通枢纽"，也是最关键的配置环节。

1.  **理解核心机制：全局前缀剥离**
    JeecgBoot网关处理请求分为两步，这个机制解释了所有现象：
    *   **第一步（全局处理）**：网关服务有一个隐藏的全局规则，它会首先自动**剥离**掉所有请求路径开头的全局前缀（例如 `/jeecgboot`）。
    *   **第二步（按库匹配）**：然后，网关用**剥离后**的干净路径（例如 `/animal_husbandry/dashboard/kpi`）去匹配数据库里存储的路由规则。

2.  **通过后台UI界面添加路由**
    项目网关的路由规则默认是从**数据库**加载的 (`data-type: database`)。因此，任何路由修改都应该通过后台管理界面完成。
    *   **菜单路径**：系统管理 -> Gateway路由管理
    *   **操作**：为您的新微服务（如智能畜牧）新增一条路由规则。

    | 配置项 | 填写内容 | 解释 |
    | :--- | :--- |:--- |
    | **路由ID** | `jeecg-animal-husbandry` | 一个唯一的、可读的英文ID |
    | **服务名(URI)**| `lb://jeecg-animalhusbandry` | `lb://` + 在Nacos中注册的服务名 |
    | **路径配置** | `/animal_husbandry/**` | 干净的、不带全局前缀的路径匹配规则 |
    | **过滤器** | **留空** | 全局机制已剥离前缀，此处无需配置`StripPrefix` |

### 4.5. 完整流程示意图

下表清晰地展示了一个请求从前端到后端的完整旅程：

| 模块 | 前端API定义<br/>(`api.ts`) | 浏览器发出的<br/>完整请求路径 | 网关全局处理 | 网关用于匹配<br/>的干净路径 | 数据库中<br/>应配的路由规则 |
| :--- | :--- | :--- | :--- |:--- |:--- |
| **系统** | `/sys/user/list` | `/jeecgboot/sys/user/list`| 剥离 `/jeecgboot`| `/sys/user/list`| `/sys/**` |
| **畜牧** | `/animal_husbandry/kpi`| `/jeecgboot/animal_husbandry/kpi`| 剥离 `/jeecgboot`| `/animal_husbandry/kpi`| **`/animal_husbandry/**`** |
| **停车** | `/parking/device/list` | `/jeecgboot/parking/device/list`| 剥离 `/jeecgboot`| `/parking/device/list`| **`/parking/**`** |
