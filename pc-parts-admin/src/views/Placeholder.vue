<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <span>{{ (route.meta?.title as string) || '页面' }}</span>
    </template>
    <el-empty description="建设中" />
  </el-card>
</template>
