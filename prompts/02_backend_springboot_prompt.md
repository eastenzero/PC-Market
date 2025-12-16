# Spring Boot 后端提示词（电脑配件销售管理系统）

将下面 `提示词正文` 整段复制给大模型使用；把占位符按你的实际项目名/包名/端口/数据库账号替换。

```text
你是一名资深 Java 架构师与 Spring Boot 全栈后端工程师。

# 1. 目标
为【电脑配件销售管理系统】生成一个【可直接运行】的后端项目：Spring Boot + MySQL。

我希望你输出：
- 完整项目结构（文件树）
- 每个关键文件的完整代码（不要省略）
- 并确保项目可以 `mvn test` / `mvn spring-boot:run` 正常启动

# 2. 技术栈约束（默认）
- Java 17
- Spring Boot 3.x
- Maven
- 数据库：MySQL 8.0
- ORM：MyBatis-Plus（如你更推荐 JPA 也可，但必须解释选择，并全项目一致）
- 安全：Spring Security + JWT（无状态）
- 参数校验：spring-boot-starter-validation
- API 文档：springdoc-openapi（OpenAPI 3 / Swagger UI）
- 数据库迁移：Flyway（优先）
- 日志：SLF4J + Logback

# 3. 业务模块范围
必须覆盖以下模块（先实现核心链路，报表可简化）：
- 认证与权限：登录/退出/获取当前用户；RBAC（sys_user/sys_role/sys_user_role）
- 商品：品牌、分类、SPU、SKU CRUD；支持分页与关键字搜索
- 合作方：客户、供应商 CRUD
- 仓库与库存：仓库 CRUD；库存查询；库存调整（入库/出库/盘点调整至少一种）
- 采购：采购单 + 采购明细；采购入库（可合并实现：创建采购单后确认入库）
- 销售：销售单 + 明细；收款记录 payment_record；销售出库

# 4. 重要业务规则（请落实到代码/事务中）
- 订单与库存：
  - 创建销售出库/确认出库时扣减库存；禁止库存为负
  - 创建采购入库/确认入库时增加库存
  - 库存变动必须记录 inventory_movement
- 并发控制：
  - 你必须选择一种方案避免并发扣减导致超卖：
    - 方案A：SELECT ... FOR UPDATE + 事务
    - 方案B：库存表 version 字段 + 乐观锁
  - 并在代码中体现
- 软删除：删除接口默认软删；查询默认过滤 deleted=0

# 5. API 设计规范（必须遵循）
- REST 风格
- 统一响应结构：
  {
    "code": 0,
    "message": "ok",
    "data": ...
  }
- 分页：page/size；返回 total、items
- 错误处理：全局异常处理（@RestControllerAdvice）输出统一结构
- 入参校验失败需返回清晰 message

# 6. 推荐接口清单（至少实现这些）
- Auth
  - POST /api/auth/login
  - GET /api/auth/me

- Product
  - GET /api/products?page=&size=&keyword=&categoryId=&brandId=
  - POST /api/products
  - GET /api/products/{id}
  - PUT /api/products/{id}
  - DELETE /api/products/{id}

- SKU
  - GET /api/skus?page=&size=&keyword=&productId=
  - POST /api/skus
  - PUT /api/skus/{id}
  - PATCH /api/skus/{id}/status

- Stock
  - GET /api/stocks?warehouseId=&skuId=&keyword=
  - POST /api/stock/in (入库)
  - POST /api/stock/out (出库)
  - GET /api/stock/movements?page=&size=

- Purchase / Sales
  - POST /api/purchase-orders
  - GET /api/purchase-orders
  - POST /api/purchase-orders/{id}/receive
  - POST /api/sales-orders
  - GET /api/sales-orders
  - POST /api/sales-orders/{id}/pay
  - POST /api/sales-orders/{id}/ship

# 7. 工程结构要求
请采用清晰的分层结构（示例，可微调但要一致）：
- controller：仅做参数接收、校验、返回
- service：业务逻辑与事务
- repository/mapper：数据库访问
- entity：数据库实体
- dto / vo：入参/出参模型
- config：安全、OpenAPI、跨域、Jackson
- common：统一响应、异常、枚举、常量

# 8. 配置与可运行性
- application.yml 中包含：数据库连接、JWT 密钥（用占位符提醒自行替换）、端口
- Flyway：提供至少一个 migration：V1__init.sql（可用占位说明从“数据库提示词”生成的DDL粘贴进来）
- 提供少量测试数据（可选）

# 9. 输出格式要求
- 先输出文件树
- 再按文件路径逐个输出完整内容，使用代码块并标注语言
- 不要输出省略号“...”代替关键代码
- 如果内容太长，请分批输出，但要保持文件完整

# 10. 需要你先确认的占位信息（如未给出则用默认）
- 项目名：pc-parts-management
- Java 包名：com.example.pcparts
- 服务端口：8080
- MySQL 库名：pc_parts
- MySQL 用户/密码：root / root

现在开始生成。
```
