 # PC-Market
 
 PC-Market 是一个面向 PC 配件的进销存/管理后台示例项目，包含：
 
 - **后端**：`pc-parts-management`（Spring Boot 3 / Spring Security / JWT / MyBatis-Plus / Flyway）
 - **前端**：`pc-parts-admin`（Vue 3 / TypeScript / Vite / Element Plus / Pinia）
 
 ## 功能概览
 
 - **认证**：登录、当前用户信息（JWT）
 - **基础资料**：品牌、分类、商品（SPU）、SKU
 - **库存**：仓库、库存查询、入库、出库、库存流水
 - **往来**：供应商、客户
 - **单据**：采购订单、销售订单（含收款/发货相关流程）
 - **系统管理**：用户、角色、用户角色绑定
 
 项目接口统一返回结构：`{ code, message, data }`；分页返回：`{ total, items }`。
 
 ## 目录结构
 
 - `pc-parts-management/`：后端服务（默认端口 `8080`）
 - `pc-parts-admin/`：前端管理端（Vite DevServer 默认端口 `5173`）
 - `database/`：SQL 参考文件（schema/seed，主要用于对照/手工导入）
 - `DEVELOPMENT_STATUS.md`：功能完成度与待办清单
 
 ## 环境要求
 
 - **JDK**：17+
 - **Maven**：3.8+
 - **Node.js**：18+（建议 18/20 LTS）
 - **MySQL**：8.0+（如使用 MySQL 模式）
 
 ## 快速开始（推荐：MySQL）
 
 ### 1) 准备数据库
 
 默认配置位于 `pc-parts-management/src/main/resources/application.yml`：
 
 - 数据库：`pc_parts`
 - 用户名：`pc_parts`
 - 密码：`pc_parts`
 
 你可以按需创建数据库和账号（示例）：
 
 ```sql
 CREATE DATABASE pc_parts DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
 CREATE USER 'pc_parts'@'localhost' IDENTIFIED BY 'pc_parts';
 GRANT ALL PRIVILEGES ON pc_parts.* TO 'pc_parts'@'localhost';
 FLUSH PRIVILEGES;
 ```
 
 后端使用 **Flyway** 自动建表与初始化数据（见 `src/main/resources/db/migration/`）。
 
 ### 2) 启动后端
 
 ```bash
 mvn spring-boot:run
 ```
 
 启动后可访问：
 
 - Swagger UI：`http://localhost:8080/swagger-ui.html`
 
 ### 3) 启动前端
 
 前端通过 Vite 代理 `/api` 到 `http://localhost:8080`（见 `pc-parts-admin/vite.config.ts`）。
 
 ```bash
 npm install
 npm run dev
 ```
 
 访问：`http://localhost:5173`
 
 ### 默认账号
 
 初始化数据中包含管理员账号（见 `V3__seed.sql`）：
 
 - 用户名：`admin`
 - 密码：`123456`
 
 ## 快速开始（可选：H2 内存库）
 
 后端提供 H2 profile（见 `pc-parts-management/src/main/resources/application-h2.yml`），用于无需安装 MySQL 的快速体验：
 
 ```bash
 mvn spring-boot:run -Dspring-boot.run.profiles=h2
 ```
 
 H2 模式数据为内存数据，服务退出后会清空。
 
 ## 构建
 
 ### 后端
 
 ```bash
 mvn -DskipTests package
 ```
 
 产物：`pc-parts-management/target/*.jar`
 
 ### 前端
 
 ```bash
 npm run build
 npm run preview
 ```
 
 产物：`pc-parts-admin/dist/`
 
 ## 配置说明（重要）
 
 - **JWT Secret**：`application.yml` 中 `jwt.secret` 默认为 `CHANGE_ME_TO_A_RANDOM_LONG_SECRET`，部署前请务必修改。
 - **账号密码策略**：示例数据里管理员密码使用 `{noop}123456`（仅用于演示），生产环境请使用安全密码与加密存储。
 
 ## 开发进度
 
 见 `DEVELOPMENT_STATUS.md`。
