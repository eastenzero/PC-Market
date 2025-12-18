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
          <img src="@/assets/images/user-icon.svg" alt="icon" style="width:24px; opacity:0.8"/>
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
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.welcome-banner {
  position: relative;
  border-radius: var(--app-radius-lg);
  background:
    linear-gradient(135deg, rgba(0, 122, 255, 0.14), rgba(175, 82, 222, 0.10)),
    var(--app-surface);
  padding: 36px;
  color: var(--app-text);
  overflow: hidden;
  margin-bottom: 0;
  box-shadow: var(--app-shadow-1), var(--app-glass-edge);
  backdrop-filter: blur(var(--app-blur)) saturate(var(--app-saturate));
  /* Glass gloss overlay */
  isolation: isolate;
}

.welcome-text {
  position: relative;
  z-index: 2;
}

.welcome-text h1 {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.welcome-text p {
  margin: 8px 0 0;
  color: var(--app-text-2);
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
  background: radial-gradient(circle, rgba(0, 122, 255, 0.22) 0%, rgba(0, 122, 255, 0) 70%);
  filter: blur(0);
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
  grid-template-columns: repeat(4, minmax(0, 1fr));
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
  padding: 20px;
}

.stat-icon {
  width: 44px;
  height: 44px;
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
  font-size: 17px;
  font-weight: 700;
  color: var(--app-text);
  letter-spacing: -0.3px;
}

.stat-value.small {
  font-size: 16px;
}

@media (max-width: 1024px) {
  .welcome-banner {
    padding: 28px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .welcome-banner {
    padding: 22px;
  }

  .welcome-text h1 {
    font-size: 24px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
