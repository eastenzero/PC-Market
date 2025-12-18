<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock, ArrowRight } from '@element-plus/icons-vue'
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
  <div class="login-container">
    <!-- Atmospheric Background Orbs -->
    <div class="orb orb-1"></div>
    <div class="orb orb-2"></div>

    <transition name="card-entry" appear>
      <div class="login-box">
        <el-card class="login-card" :body-style="{ padding: '0' }">
          <div class="login-content">
            <div class="brand-area">
              <div class="logo-circle">
                <span class="logo-text">PC</span>
              </div>
              <h1 class="app-title">Parts Admin</h1>
              <p class="app-subtitle">销售管理系统</p>
            </div>

            <el-form
              class="login-form"
              ref="formRef"
              :model="form"
              :rules="rules"
              @keyup.enter="onSubmit"
            >
              <el-form-item prop="username">
                <el-input 
                  v-model="form.username" 
                  size="large" 
                  placeholder="用户名"
                  :prefix-icon="User"
                  class="glass-input"
                />
              </el-form-item>
              
              <el-form-item prop="password">
                <el-input
                  v-model="form.password"
                  size="large"
                  type="password"
                  show-password
                  placeholder="密码"
                  :prefix-icon="Lock"
                  class="glass-input"
                />
              </el-form-item>

              <div class="form-actions">
                <el-button 
                  size="large" 
                  type="primary" 
                  :loading="loading" 
                  class="submit-btn" 
                  @click="onSubmit"
                >
                  <span>登 录</span>
                  <el-icon class="btn-icon"><ArrowRight /></el-icon>
                </el-button>
              </div>
            </el-form>
          </div>
        </el-card>
        
        <div class="login-footer">
          &copy; 2025 PC Parts Inc.
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 48px 16px;
  /* Ensure global background shows through, but we add local orbs */
}

/* Atmospheric Orbs */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  opacity: 0.6;
}

.orb-1 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(0,122,255,0.4) 0%, rgba(0,122,255,0) 70%);
  top: 20%;
  left: 30%;
  animation: float 10s ease-in-out infinite;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(175,82,222,0.3) 0%, rgba(175,82,222,0) 70%);
  bottom: 10%;
  right: 20%;
  animation: float 14s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-30px, 40px); }
}

.login-box {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 980px;
  padding: 20px;
}

.login-card {
  --el-card-border-radius: 24px;
  /* Card itself inherits global glass styles from style.css (.el-card) */
  /* We just add specific layout tweaks */
  overflow: hidden;
}

.login-content {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  min-height: 460px;
  padding: 0;
}

.brand-area {
  text-align: left;
  margin-bottom: 0;
  padding: 48px 44px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  gap: 10px;
  background:
    radial-gradient(900px 500px at 0% 0%, rgba(0, 122, 255, 0.18), transparent 60%),
    radial-gradient(900px 500px at 60% 100%, rgba(175, 82, 222, 0.12), transparent 55%);
  border-right: 1px solid var(--app-divider);
}

.logo-circle {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #007aff, #5856d6);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 0 20px;
  box-shadow: 0 10px 20px rgba(0, 122, 255, 0.3);
  color: white;
  font-weight: 800;
  font-size: 24px;
}

.app-title {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: var(--app-text);
}

.app-subtitle {
  margin: 6px 0 0;
  font-size: 16px;
  color: var(--app-text-2);
  letter-spacing: -0.2px;
}

.login-form {
  margin-top: 0;
  padding: 48px 44px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.login-form :deep(.el-form-item:last-of-type) {
  margin-bottom: 0;
}

.glass-input {
  /* Input styles are globally handled in style.css */
  /* Just ensuring margin/spacing via form-item */
}

/* Button Customization for Login */
.submit-btn {
  width: 100%;
  height: 48px; /* Taller touch target */
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: var(--app-text-3);
  font-weight: 500;
}

@media (max-width: 768px) {
  .login-box {
    max-width: 420px;
  }

  .login-content {
    grid-template-columns: 1fr;
    min-height: unset;
  }

  .brand-area {
    text-align: center;
    align-items: center;
    border-right: none;
    border-bottom: 1px solid var(--app-divider);
  }

  .logo-circle {
    margin: 0 auto 20px;
  }

  .login-form {
    padding: 40px 32px;
  }
}

/* Entry Animation */
.card-entry-enter-active {
  transition: all 0.6s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.card-entry-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.95);
  filter: blur(10px);
}
</style>
