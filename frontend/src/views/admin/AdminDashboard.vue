<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'

const userStore = useUserStore()
const notificationStore = useNotificationStore()

const summary = computed(() => [
  { label: '当前用户', value: userStore.user?.username || '-' },
  { label: '角色', value: userStore.user?.role || '-' },
  { label: '未读消息', value: notificationStore.unreadCount },
])
</script>

<template>
  <div class="grid">
    <div v-for="item in summary" :key="item.label" class="card">
      <div class="label">{{ item.label }}</div>
      <div class="value">{{ item.value }}</div>
    </div>
  </div>
</template>

<style scoped>
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
</style>
