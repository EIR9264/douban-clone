<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'
import { Film, User, ChatDotRound, Star, Bell } from '@element-plus/icons-vue'

const userStore = useUserStore()
const notificationStore = useNotificationStore()
const router = useRouter()

const summary = computed(() => [
  { label: '当前用户', value: userStore.user?.username || '-' },
  { label: '角色', value: userStore.user?.role || '-' },
  { label: '未读消息', value: notificationStore.unreadCount },
])

const quickLinks = computed(() => [
  { title: '电影管理', desc: '新增/编辑影片与媒体', path: '/admin/movies', icon: Film, tint: 'rgba(52, 199, 89, 0.14)' },
  { title: '用户管理', desc: '查看/编辑用户资料', path: '/admin/users', icon: User, tint: 'rgba(0, 122, 255, 0.12)' },
  { title: '评论管理', desc: '处理评论与点赞', path: '/admin/reviews', icon: ChatDotRound, tint: 'rgba(255, 149, 0, 0.14)' },
  { title: '评分管理', desc: '仅删除评分记录', path: '/admin/ratings', icon: Star, tint: 'rgba(175, 82, 222, 0.14)' },
  { title: '站内信', desc: '向用户发送站内信', path: '/admin/messages', icon: Bell, tint: 'rgba(255, 59, 48, 0.12)' },
  { title: '公告', desc: '发布/下线站内公告', path: '/admin/announcements', icon: Bell, tint: 'rgba(90, 200, 250, 0.14)' },
])

function go(path) {
  router.push(path)
}
</script>

<template>
  <div class="dashboard">
    <div class="grid">
      <div v-for="item in summary" :key="item.label" class="card">
        <div class="label">{{ item.label }}</div>
        <div class="value">{{ item.value }}</div>
      </div>
    </div>

    <div class="section">
      <div class="section-title">快速入口</div>
      <div class="quick-grid">
        <el-card
          v-for="item in quickLinks"
          :key="item.path"
          class="quick-card"
          shadow="never"
          @click="go(item.path)"
        >
          <div class="quick-row">
            <div class="icon" :style="{ background: item.tint }">
              <el-icon :size="20">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="meta">
              <div class="title">{{ item.title }}</div>
              <div class="desc">{{ item.desc }}</div>
            </div>
            <div class="arrow">›</div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.card {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.label {
  color: #666;
  font-size: 14px;
}
.value {
  font-size: 22px;
  font-weight: 700;
  margin-top: 6px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  margin: 4px 2px 10px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}

.quick-card {
  cursor: pointer;
}

.quick-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
  flex: 0 0 auto;
}

.meta {
  flex: 1;
  min-width: 0;
}

.title {
  font-weight: 700;
  color: var(--text-primary);
  font-size: 15px;
}

.desc {
  color: var(--text-muted);
  font-size: 12px;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.arrow {
  color: var(--text-muted);
  font-size: 20px;
  line-height: 1;
  padding: 0 4px;
}
</style>
