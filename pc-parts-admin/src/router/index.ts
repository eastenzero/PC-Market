import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/',
      component: () => import('@/layout/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '仪表盘', requiresAuth: true },
        },
        {
          path: 'product/brands',
          name: 'BrandList',
          component: () => import('@/views/product/BrandList.vue'),
          meta: { title: '品牌管理', requiresAuth: true },
        },
        {
          path: 'product/categories',
          name: 'CategoryList',
          component: () => import('@/views/product/CategoryList.vue'),
          meta: { title: '分类管理', requiresAuth: true },
        },
        {
          path: 'product/products',
          name: 'ProductList',
          component: () => import('@/views/product/ProductList.vue'),
          meta: { title: '商品管理', requiresAuth: true },
        },
        {
          path: 'product/skus',
          name: 'SkuList',
          component: () => import('@/views/product/SkuList.vue'),
          meta: { title: 'SKU管理', requiresAuth: true },
        },
        {
          path: 'inventory/stocks',
          name: 'StockList',
          component: () => import('@/views/inventory/StockList.vue'),
          meta: { title: '库存查询', requiresAuth: true },
        },
        {
          path: 'inventory/inbound',
          name: 'StockInbound',
          component: () => import('@/views/inventory/StockInbound.vue'),
          meta: { title: '入库管理', requiresAuth: true },
        },
        {
          path: 'inventory/outbound',
          name: 'StockOutbound',
          component: () => import('@/views/inventory/StockOutbound.vue'),
          meta: { title: '出库管理', requiresAuth: true },
        },
        {
          path: 'inventory/movements',
          name: 'StockMovements',
          component: () => import('@/views/inventory/StockMovements.vue'),
          meta: { title: '库存流水', requiresAuth: true },
        },
        {
          path: 'inventory/warehouses',
          name: 'WarehouseList',
          component: () => import('@/views/inventory/WarehouseList.vue'),
          meta: { title: '仓库管理', requiresAuth: true },
        },
        {
          path: 'purchase/orders',
          name: 'PurchaseOrderList',
          component: () => import('@/views/purchase/PurchaseOrderList.vue'),
          meta: { title: '采购订单', requiresAuth: true },
        },
        {
          path: 'sales/orders',
          name: 'SalesOrderList',
          component: () => import('@/views/sales/SalesOrderList.vue'),
          meta: { title: '销售订单', requiresAuth: true },
        },
        {
          path: 'partner/suppliers',
          name: 'SupplierList',
          component: () => import('@/views/partner/SupplierList.vue'),
          meta: { title: '供应商管理', requiresAuth: true },
        },
        {
          path: 'partner/customers',
          name: 'CustomerList',
          component: () => import('@/views/partner/CustomerList.vue'),
          meta: { title: '客户管理', requiresAuth: true },
        },
        {
          path: 'system/users',
          name: 'UserList',
          component: () => import('@/views/system/UserList.vue'),
          meta: { title: '用户管理', requiresAuth: true },
        },
        {
          path: 'system/roles',
          name: 'RoleList',
          component: () => import('@/views/system/RoleList.vue'),
          meta: { title: '角色管理', requiresAuth: true },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/NotFound.vue'),
    },
  ],
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (to.path === '/login') {
    if (authStore.token) return { path: '/' }
    return true
  }

  const requiresAuth = to.matched.some((r) => r.meta?.requiresAuth)
  if (!requiresAuth) return true

  if (!authStore.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (!authStore.user) {
    try {
      await authStore.fetchMe()
    } catch {
      authStore.logout()
      return { path: '/login', query: { redirect: to.fullPath } }
    }
  }

  return true
})

export default router
