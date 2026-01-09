<script setup>
import { RouterView } from 'vue-router'
import { useUserStore } from '@/stores/user'

const menuItems = [
  { path: '/admin/dashboard', label: '仪表盘' },
  { path: '/admin/movies', label: '电影管理' },
  { path: '/admin/users', label: '用户管理' },
  { path: '/admin/reviews', label: '评论管理' },
  { path: '/admin/ratings', label: '评分管理' },
  { path: '/admin/messages', label: '站内信' },
  { path: '/admin/announcements', label: '公告' },
]

const userStore = useUserStore()
</script>

<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="brand">Admin</div>
      <el-menu router :default-active="$route.path" class="menu">
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          {{ item.label }}
        </el-menu-item>
      </el-menu>
      <div class="user">
        <div class="name">{{ userStore.user?.username }}</div>
        <div class="role">角色：{{ userStore.user?.role }}</div>
      </div>
    </aside>
    <section class="content">
      <RouterView v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" :key="$route.fullPath" />
        </transition>
      </RouterView>
    </section>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-page);
}
.sidebar {
  width: 240px;
  background: rgba(255, 255, 255, 0.88);
  color: var(--text-primary);
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--separator);
  backdrop-filter: blur(12px);
  position: sticky;
  top: 64px;
  height: calc(100vh - 64px);
  overflow: auto;
}
.brand {
  padding: 18px 16px 12px;
  font-weight: 700;
  text-align: left;
  font-size: 18px;
  letter-spacing: 0.2px;
}
.menu {
  border-right: none;
  background: transparent;
  padding: 6px 10px;
}
.user {
  margin-top: auto;
  padding: 12px 16px 16px;
  font-size: 12px;
  border-top: 1px solid var(--separator);
  background: transparent;
}
.content {
  flex: 1;
  padding: 16px 16px 28px;
}

.menu :deep(.el-menu-item) {
  border-radius: 12px;
  margin: 4px 0;
  height: 42px;
  line-height: 42px;
  color: var(--text-secondary);
}

.menu :deep(.el-menu-item:hover) {
  background: rgba(60, 60, 67, 0.06);
}

.menu :deep(.el-menu-item.is-active) {
  color: var(--douban-green);
  background: rgba(52, 199, 89, 0.14);
  font-weight: 600;
}
</style>
