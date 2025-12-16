<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { Monitor, Goods, Timer } from '@element-plus/icons-vue'

const authStore = useAuthStore()

const currentHour = new Date().getHours()
const greeting = computed(() => {
  if (currentHour < 12) return '早上好'
  if (currentHour < 18) return '下午好'
  return '晚上好'
})
</script>

<template>
  <div class="dashboard">
    <div class="welcome-banner">
      <div class="welcome-text">
        <h1>{{ greeting }}，{{ authStore.user?.nickname || '管理员' }}</h1>
        <p>欢迎回到 PC 配件销售管理系统</p>
      </div>
      <div class="welcome-decoration">
        <!-- Abstract Decoration -->
        <div class="circle c1"></div>
        <div class="circle c2"></div>
      </div>
    </div>

    <div class="stats-grid">
      <el-card class="stat-card" shadow="never">
        <div class="stat-icon blue">
          <img src="https://api.iconify.design/ri:user-star-line.svg?color=%23007aff" alt="icon" style="width:24px; opacity:0.8"/>
        </div>
        <div class="stat-info">
          <div class="stat-label">当前用户</div>
          <div class="stat-value">{{ authStore.user?.username }}</div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="never">
        <div class="stat-icon green">
          <el-icon><Monitor /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-label">系统状态</div>
          <div class="stat-value">运行正常</div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="never">
        <div class="stat-icon orange">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-label">当前角色</div>
          <div class="stat-value small">{{ authStore.user?.roles?.[0] || 'Admin' }}</div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="never">
        <div class="stat-icon purple">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-label">登录时间</div>
          <div class="stat-value small">{{ new Date().toLocaleDateString() }}</div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  animation: fade-in 0.6s ease-out;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.welcome-banner {
  position: relative;
  border-radius: var(--app-radius-lg);
  background: linear-gradient(135deg, #007aff, #5ac8fa);
  padding: 40px;
  color: white;
  overflow: hidden;
  margin-bottom: var(--sp-3);
  box-shadow: 0 10px 30px rgba(0, 122, 255, 0.25);
  /* Glass gloss overlay */
  isolation: isolate;
}

.welcome-text {
  position: relative;
  z-index: 2;
}

.welcome-text h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.welcome-text p {
  margin: 8px 0 0;
  opacity: 0.9;
  font-size: 15px;
}

.welcome-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.c1 {
  width: 200px;
  height: 200px;
  top: -40px;
  right: -40px;
}

.c2 {
  width: 120px;
  height: 120px;
  bottom: 20px;
  right: 140px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--sp-2);
}

.stat-card {
  display: flex;
  align-items: center;
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 24px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
}

.stat-icon.blue { background: rgba(0, 122, 255, 0.1); color: #007aff; }
.stat-icon.green { background: rgba(52, 199, 89, 0.1); color: #34c759; }
.stat-icon.orange { background: rgba(255, 149, 0, 0.1); color: #ff9500; }
.stat-icon.purple { background: rgba(175, 82, 222, 0.1); color: #af52de; }

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: var(--app-text-2);
  margin-bottom: 4px;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--app-text);
  letter-spacing: -0.3px;
}

.stat-value.small {
  font-size: 16px;
}
</style>
