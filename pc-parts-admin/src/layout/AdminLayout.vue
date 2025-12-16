<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  Box,
  Briefcase,
  Grid,
  HomeFilled,
  Histogram,
  Setting,
  ShoppingCart,
  User,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)

async function onLogout() {
  try {
    await ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' })
    authStore.logout()
    await router.replace('/login')
  } catch {
  }
}
</script>

<template>
  <el-container class="layout">
    <el-aside class="layout-aside" width="208px">
      <div class="logo">PC Parts Admin</div>
      <el-menu
        class="layout-menu"
        :default-active="activeMenu"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-sub-menu index="/product">
          <template #title>
            <el-icon><Grid /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/product/brands">品牌管理</el-menu-item>
          <el-menu-item index="/product/categories">分类管理</el-menu-item>
          <el-menu-item index="/product/products">商品管理</el-menu-item>
          <el-menu-item index="/product/skus">SKU管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/inventory">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item index="/inventory/stocks">库存查询</el-menu-item>
          <el-menu-item index="/inventory/inbound">入库管理</el-menu-item>
          <el-menu-item index="/inventory/outbound">出库管理</el-menu-item>
          <el-menu-item index="/inventory/movements">库存流水</el-menu-item>
          <el-menu-item index="/inventory/warehouses">仓库管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/purchase">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>采购管理</span>
          </template>
          <el-menu-item index="/purchase/orders">采购订单</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/sales">
          <template #title>
            <el-icon><Histogram /></el-icon>
            <span>销售管理</span>
          </template>
          <el-menu-item index="/sales/orders">销售订单</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/partner">
          <template #title>
            <el-icon><Briefcase /></el-icon>
            <span>合作伙伴</span>
          </template>
          <el-menu-item index="/partner/suppliers">供应商管理</el-menu-item>
          <el-menu-item index="/partner/customers">客户管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/users">用户管理</el-menu-item>
          <el-menu-item index="/system/roles">角色管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">{{ route.meta?.title || '管理后台' }}</div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-trigger">
              <el-avatar class="user-avatar" :size="28" :icon="User" />
              <span class="user-name">{{ authStore.user?.nickname || authStore.user?.username || '用户' }}</span>
              <el-icon class="icon-right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="onLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="app-page" mode="out-in">
            <div>
              <component :is="Component" />
            </div>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100vh;
}

.layout-aside {
  padding: var(--sp-2) var(--sp-1);
  background: var(--app-surface);
  /* The glass edge variable includes the border definition now */
  box-shadow: var(--app-glass-edge); 
  backdrop-filter: saturate(var(--app-saturate)) blur(var(--app-blur));
  z-index: 10; /* Ensure it floats above content if needed */
}

.logo {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center; /* Center align for modern feel */
  margin: 0 8px var(--sp-2);
  padding: 0 12px;
  border-radius: var(--app-radius);
  color: var(--app-text);
  font-weight: 700;
  letter-spacing: -0.4px;
  background: rgba(255, 255, 255, 0.4); /* Subtle backing */
  box-shadow: inset 0 0 0 1px rgba(0,0,0,0.05);
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 var(--sp-2);
  background: var(--app-surface);
  /* Glass edge creates the separator */
  box-shadow: var(--app-glass-edge), 0 4px 20px rgba(0,0,0,0.02);
  backdrop-filter: saturate(var(--app-saturate)) blur(var(--app-blur));
  position: sticky;
  top: 0;
  z-index: 20;
}

.header-left {
  font-weight: 700;
  font-size: 17px;
  letter-spacing: -0.4px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-trigger {
  cursor: pointer;
  user-select: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px 4px 4px; /* Tighter padding */
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.04); /* Light capsule */
  transition: all 0.2s ease;
}

.user-trigger:hover {
  background: rgba(0, 0, 0, 0.08);
}

.user-trigger:active {
  transform: scale(0.98);
}

.user-avatar {
  background: var(--app-primary);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 122, 255, 0.2);
}

.user-name {
  font-weight: 600;
  font-size: 13px;
  margin-right: 4px;
}

.icon-right {
  color: var(--app-text-3);
  font-size: 12px;
}

.layout-main {
  padding: var(--sp-2);
  /* Main background is handled by body in style.css */
  background: transparent; 
}

/* Sidebar Menu Tweaks */
.layout-aside :deep(.el-menu) {
  border-right: none;
  background: transparent;
}

.layout-aside :deep(.el-menu-item),
.layout-aside :deep(.el-sub-menu__title) {
  height: 42px; /* Slightly taller/shorter depending on pref, 42 is good for dense */
  line-height: 42px;
  border-radius: var(--app-radius);
  margin: 4px 8px;
  color: var(--app-text-2);
  font-weight: 500;
}

.layout-aside :deep(.el-menu-item:hover),
.layout-aside :deep(.el-sub-menu__title:hover) {
  background: rgba(0, 0, 0, 0.04);
  color: var(--app-text);
}

.layout-aside :deep(.el-menu-item.is-active) {
  color: var(--app-primary);
  background: rgba(0, 122, 255, 0.1);
  font-weight: 600;
}

/* Active Indicator */
.layout-aside :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: -12px; /* Outside the item */
  top: 10px;
  bottom: 10px;
  width: 4px;
  border-radius: 0 4px 4px 0;
  background: var(--app-primary);
  display: none; /* Disable the side bar line, prefer the bg highlight for this style */
}
</style>
