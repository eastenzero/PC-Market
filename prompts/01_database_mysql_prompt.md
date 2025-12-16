# MySQL 数据库设计提示词（电脑配件销售管理系统）

将下面 `提示词正文` 整段复制给大模型使用。你只需要把其中的「占位符」按需替换即可。

```text
你是一名资深数据库架构师。

# 1. 项目背景
我要做一个【电脑配件销售管理系统】（PC Parts Sales Management System），用于管理：商品、库存、采购、销售、客户、供应商、用户与权限、基础报表。

目标：请为该系统设计一套【可直接落地】的 MySQL 8.0 数据库表结构（DDL + 索引 + 约束 + 种子数据）。

# 2. 运行环境与约束
- 数据库：MySQL 8.0
- 引擎：InnoDB
- 字符集/排序规则：utf8mb4 / utf8mb4_0900_ai_ci
- 命名：全部 snake_case，小写
- 主键：bigint（建议自增或雪花，默认自增）
- 金额：decimal(12,2)
- 数量：int（或 decimal(12,3) 视业务需要；默认 int）
- 时间：datetime
- 软删除：deleted tinyint(1) 默认 0
- 审计字段：created_at, created_by, updated_at, updated_by

# 3. 业务范围（默认假设）
如未说明则按以下默认：
- 单公司、多仓库（warehouse）
- 以“商品（product）+ SKU（product_sku）”管理规格（如容量/颜色/接口等）
- 库存变动用“库存流水（inventory_movement）”记录；同时维护“库存汇总（inventory_stock）”方便查询
- 采购入库：purchase_order（采购单）+ stock_in（入库单，可由采购单生成）
- 销售出库：sales_order（销售单）+ stock_out（出库单，可由销售单生成）
- 允许订单状态流转（草稿/已提交/已审核/已完成/已取消等）

如果你认为需要澄清的问题（例如：是否需要序列号管理、是否支持多门店/多租户、是否区分线上/线下、是否有应收应付），请先列出【澄清问题清单】；但仍要基于默认假设继续输出完整设计。

# 4. 需要的核心实体（必须覆盖）
1) 权限与用户（RBAC）
- sys_user
- sys_role
- sys_user_role
（如你认为需要：sys_permission、sys_role_permission 可加）

2) 主数据
- brand（品牌）
- product_category（分类，支持树形）
- product（SPU）
- product_sku（SKU，可带条码/编码、价格、成本、状态）

3) 合作方
- customer（客户）
- supplier（供应商）

4) 仓库与库存
- warehouse（仓库）
- inventory_stock（SKU 在仓库的当前库存，可包含 version 乐观锁字段）
- inventory_movement（库存流水：入库/出库/调整；关联业务单据）

5) 采购
- purchase_order
- purchase_order_item
- stock_in
- stock_in_item

6) 销售
- sales_order
- sales_order_item
- stock_out
- stock_out_item

7) 财务/收款（简化版即可）
- payment_record（针对销售单的收款记录；支持多次收款）

8) 审计（可选但推荐）
- audit_log（记录关键操作）

# 5. 输出要求（请严格按顺序输出）
A. 【表清单】
- 用表格列出：表名 / 中文名 / 用途说明

B. 【字段设计说明】
- 每个表列出关键字段与设计理由（尤其是：金额字段、状态字段、索引、外键、软删除策略）

C. 【完整 DDL】（必须可直接执行）
- 先 CREATE DATABASE（可选）
- 再按依赖顺序 CREATE TABLE
- 必须包含：主键、唯一约束、外键（如你认为外键会影响性能可选择不加，但需解释并用应用层保证）、索引
- 每张表都包含统一的审计字段与 deleted 字段

D. 【索引策略】
- 汇总说明每张表的核心索引（查询场景：商品列表检索、订单按单号/时间范围查询、库存查询、客户/供应商检索）

E. 【种子数据】
- 插入：
  - 角色（admin/sales/warehouse/finance 等）
  - 一个管理员账号（用户名/密码用占位符，例如 admin / 123456；并提醒上线需修改与加密）
  - 若干分类、品牌、SKU 示例数据（最少 5 个 SKU）

F. 【一致性与并发说明】
- 说明：创建销售单/出库时如何避免超卖（建议：库存扣减使用事务 + 行级锁或乐观锁）
- 说明：库存流水与库存汇总的关系

G. 【额外加分】
- 如你愿意：给出一份“订单号生成规则”建议（如 SO+日期+序号）

# 6. 输出格式
- DDL 与种子数据请分别放在代码块中：
  - ```sql
  - ...
  - ```
- 不要输出伪代码；SQL 必须可运行。

现在开始输出。
```
