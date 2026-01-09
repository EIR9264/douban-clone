<script setup>
import { RouterView, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMenu, ElMenuItem } from 'element-plus'

const router = useRouter()
const menuItems = [
  { path: '/admin/dashboard', label: '仪表盘' },
  { path: '/admin/movies', label: '电影管理' },
  { path: '/admin/users', label: '用户管理' },
  { path: '/admin/reviews', label: '评论审核' },
  { path: '/admin/messages', label: '站内信' },
  { path: '/admin/announcements', label: '公告' }
]

const userStore = useUserStore()
</script>

<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="brand">Admin</div>
      <ElMenu router :default-active="$route.path">
        <ElMenuItem v-for="item in menuItems" :key="item.path" :index="item.path">
          {{ item.label }}
        </ElMenuItem>
      </ElMenu>
      <div class="user">
        <div class="name">{{ userStore.user?.username }}</div>
        <div class="role">角色：{{ userStore.user?.role }}</div>
      </div>
    </aside>
    <section class="content">
      <RouterView />
    </section>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}
.sidebar {
  width: 220px;
  background: #1f2d3d;
  color: #fff;
  display: flex;
  flex-direction: column;
}
.brand {
  padding: 16px;
  font-weight: 700;
  text-align: center;
}
.user {
  margin-top: auto;
  padding: 12px 16px;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.08);
}
.content {
  flex: 1;
  padding: 16px;
}
</style>
