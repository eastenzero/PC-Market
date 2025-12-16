<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  username: 'admin',
  password: '123456',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function onSubmit() {
  try {
    await formRef.value?.validate()
    loading.value = true

    await authStore.login(form.username, form.password)

    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    await router.replace(redirect || '/')
  } catch (e: any) {
    ElMessage.error(e?.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login">
    <el-card class="login-card" shadow="never">
      <div class="login-brand">
        <div class="login-title">PC配件销售管理系统</div>
        <div class="login-subtitle">请使用管理员账号登录以继续</div>
      </div>
      <el-form
        class="login-form"
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @keyup.enter="onSubmit"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" size="large" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            size="large"
            type="password"
            show-password
            autocomplete="current-password"
          />
        </el-form-item>
        <el-button size="large" type="primary" :loading="loading" style="width: 100%" @click="onSubmit">
          登录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--sp-4) var(--sp-2);
  background: transparent;
}

.login-card {
  width: min(420px, calc(100vw - 32px));
}

.login-card :deep(.el-card__body) {
  padding: var(--sp-3);
}

.login-title {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.2px;
  margin-bottom: 4px;
}

.login-subtitle {
  font-size: 14px;
  color: var(--app-text-2);
  margin-bottom: var(--sp-3);
}

.login-form :deep(.el-form-item) {
  margin-bottom: var(--sp-2);
}
</style>
