# Vue 前端提示词（电脑配件销售管理系统）

将下面 `提示词正文` 整段复制给大模型使用；把占位符按你的实际接口地址、项目名替换。

```text
你是一名资深前端架构师，擅长 Vue 3 + TypeScript + 企业级后台管理系统。

# 1. 目标
为【电脑配件销售管理系统】生成一个【可直接运行】的前端管理后台项目。

要求你输出：
- 完整项目文件树
- 每个关键文件的完整代码（不要省略）
- 能 `pnpm install && pnpm dev`（或 npm/yarn）启动

# 2. 技术栈约束（默认）
- Vue 3 + Vite
- TypeScript
- Vue Router
- Pinia
- UI 组件库：Element Plus
- HTTP：Axios
- 图表（可选）：ECharts
- 代码风格：ESLint + Prettier（可简化，但项目必须可运行）

# 3. 业务页面范围（至少这些）
- 登录页 Login
- 首页仪表盘 Dashboard（可用简单卡片 + 图表占位）
- 商品管理
  - 分类管理（树/列表）
  - 品牌管理
  - 商品 SPU 列表/新增/编辑
  - SKU 列表/新增/编辑/启用禁用
- 库存管理
  - 库存查询（按仓库/关键字）
  - 入库单创建
  - 出库单创建
  - 库存流水列表
- 采购管理
  - 采购单列表/新建/详情
- 销售管理
  - 销售单列表/新建/详情
  - 收款登记
- 合作方
  - 客户管理
  - 供应商管理
- 系统管理（可先做最小可用）
  - 用户管理
  - 角色管理

# 4. UI/交互要求
- 经典后台布局：左侧菜单 + 顶部栏 + 主内容区
- 表格页：支持搜索区 + 表格 + 分页
- 新增/编辑：使用 Drawer 或 Dialog 表单
- 表单校验：必填、长度、数值范围
- 状态字段：用 Tag 显示（启用/禁用、订单状态等）

# 5. 权限与鉴权（必须实现）
- 登录成功后保存 JWT token（localStorage 或 pinia 持久化）
- Axios 请求拦截器：自动携带 Authorization: Bearer <token>
- Axios 响应拦截器：
  - code!=0 统一提示
  - 401 自动跳转登录并清理 token
- 路由守卫：未登录禁止进入
- 菜单权限：基于角色（可先用前端静态角色配置；若后端提供 /me 返回角色则动态生成）

# 6. 与后端的接口约定
- baseURL：{{BACKEND_BASE_URL}}（例如 http://localhost:8080）
- 所有接口路径以 /api 开头
- 统一响应：{ code, message, data }
- 分页返回建议：{ total, items }

# 7. 需要你提供的前端工程组织
- src/api：按模块拆分 API
- src/stores：Pinia
- src/router：路由与守卫
- src/layout：Layout 组件
- src/views：页面
- src/components：通用组件
- src/utils：axios 实例、auth、格式化
- src/types：TypeScript 类型

# 8. 建议的接口封装
- 使用一个 axios 实例：
  - timeout、baseURL
  - request/response interceptor
- 每个模块提供：
  - list/create/update/delete
- TS 类型：
  - ApiResponse<T>
  - PageResult<T>

# 9. 输出格式要求
- 先输出文件树
- 再按文件路径逐个输出完整内容，使用代码块标注语言
- 不要用“...”省略关键代码

# 10. 你可以默认的占位信息（如未给出）
- 项目名：pc-parts-admin
- 包管理器：pnpm
- 后端地址：http://localhost:8080

现在开始生成。
```
