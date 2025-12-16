<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="notfound">
    <el-result icon="warning" title="页面不存在" sub-title="请检查访问路径是否正确">
      <template #extra>
        <el-button type="primary" @click="router.replace('/')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<style scoped>
.notfound {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--sp-4) var(--sp-2);
  background: transparent;
}
</style>
