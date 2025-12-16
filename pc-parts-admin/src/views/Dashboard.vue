<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <span>仪表盘</span>
    </template>
    <div>当前用户：{{ authStore.user?.nickname || authStore.user?.username || '-' }}</div>
    <div>角色：{{ authStore.user?.roles?.join(', ') || '-' }}</div>
  </el-card>
</template>
