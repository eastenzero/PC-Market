# 开发完成度与待办清单（实时同步）

更新时间：2025-12-15

## 项目概览

- **后端**：`pc-parts-management`（Spring Boot / MyBatis-Plus / Flyway / JWT）
- **前端**：`pc-parts-admin`（Vue 3 / TypeScript / Vite / Element Plus / Pinia）
- **接口约定**：统一响应 `{ code, message, data }`；分页返回 `{ total, items }`

## 完成度总览

状态说明：✅ 已完成；⚠️ 部分完成；❌ 未开发

| 模块 | 后端 | 前端 | 备注/缺口 |
| --- | --- | --- | --- |
| 认证（登录 / 当前用户） | ✅ | ✅ | 前端已接入路由守卫、Axios 拦截器、退出登录 |
| 仪表盘 | ✅（/me 提供信息） | ⚠️ | 目前为最小展示（用户/角色），可后续增强 |
| 品牌管理 | ✅ | ✅ | CRUD + 分页 + 关键字 |
| 分类管理 | ✅ | ✅ | 已实现 `CategoryList` 页面 + `api/category` + `types/category` |
| 商品（SPU）管理 | ✅ | ✅ | 已实现 `ProductList` 页面 + `api/product` + `types/product` |
| SKU 管理 | ✅ | ✅ | 已实现 `SkuList` 页面 + `api/sku` + `types/sku` |
| 仓库管理 | ✅ | ✅ | 已实现 `WarehouseList` 页面 + `api/warehouse` + `types/warehouse` |
| 库存查询 | ✅ | ✅ | 已实现 `StockList` 页面 + `api/stock` + `types/stock` |
| 入库（手工入库） | ✅ | ✅ | 已实现 `StockInbound` 页面 + `api/stock` + `types/stock` |
| 出库（手工出库） | ✅ | ✅ | 已实现 `StockOutbound` 页面 + `api/stock` + `types/stock` |
| 库存流水 | ✅ | ✅ | 已实现 `StockMovements` 页面 + `api/stock` + `types/stock` |
| 供应商管理 | ✅ | ✅ | 已实现 `SupplierList` 页面 + `api/supplier` + `types/supplier` |
| 客户管理 | ✅ | ✅ | 已实现 `CustomerList` 页面 + `api/customer` + `types/customer` |
| 采购订单 | ✅ | ✅ | 已实现 `PurchaseOrderList` 页面 + `api/purchaseOrder` + `types/purchaseOrder` |
| 销售订单/收款/出库 | ✅ | ✅ | 已实现 `SalesOrderList` 页面 + `api/salesOrder` + `types/salesOrder` |
| 系统管理（用户/角色） | ✅ | ✅ | 已实现 `UserList`/`RoleList` 页面 + `api/systemUser`/`api/systemRole` + `types/systemUser`/`types/systemRole` |

## 待办清单（按优先级）

### P0（阻断功能闭环）

- [x] 后端：补齐系统管理 API（用户/角色 CRUD + 用户角色绑定）
- [x] 后端：SKU 补齐 `GET /api/skus/{id}`（以及删除接口如需）
- [x] 前端：分类管理（页面 + API + types）
- [x] 前端：商品（SPU）管理（页面 + API + types）
- [x] 前端：SKU 管理（页面 + API + types）

### P1（核心业务链路）

- [x] 前端：仓库管理（页面 + API + types）
- [x] 前端：库存查询 / 入库 / 出库 / 库存流水（页面 + API + types）
- [x] 前端：供应商/客户管理（页面 + API + types）
- [x] 前端：采购订单（列表/新建/收货）
- [x] 前端：销售订单（列表/新建/收款/发货）

### P2（体验与完善）

- [ ] 前端：仪表盘增强（统计卡片/图表等）
- [ ] 统一：补充必要的交互细节（状态 Tag、表单校验、错误提示一致性）

## 变更记录

- 2025-12-15：首次扫描完成。后端除系统管理外基本齐全；前端除登录/仪表盘/品牌管理外大部分为占位页。
- 2025-12-15：补齐后端系统管理（用户/角色）接口；SKU 补齐详情接口与删除接口。
- 2025-12-15：完成前端分类管理页面与接口封装。
- 2025-12-15：完成前端商品（SPU）与 SKU 管理页面与接口封装。
- 2025-12-15：完成前端仓库管理页面与接口封装。
- 2025-12-15：完成前端库存查询/入库/出库/库存流水页面与接口封装。
- 2025-12-15：完成前端供应商/客户管理页面与接口封装。
- 2025-12-15：完成前端采购订单/销售订单页面与接口封装。
- 2025-12-15：完成前端系统管理（用户/角色）页面与接口封装。
- 2025-12-15：基础验证通过：前端 `npm run build` 成功；后端 `mvn -DskipTests package` 成功。
