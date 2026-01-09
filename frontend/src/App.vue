<script setup>
import { watch, onMounted, onBeforeUnmount } from 'vue'
import NavBar from '@/components/NavBar.vue'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'

const userStore = useUserStore()
const notificationStore = useNotificationStore()

onMounted(() => {
  const token = userStore.token
  if (token) {
    notificationStore.connect(token)
    notificationStore.loadUnread()
  }
})

watch(
  () => userStore.token,
  (token) => {
    if (token) {
      notificationStore.connect(token)
      notificationStore.loadUnread()
    } else {
      notificationStore.disconnect()
    }
  }
)

onBeforeUnmount(() => {
  notificationStore.disconnect()
})
</script>

<template>
  <div class="app">
    <NavBar />
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" :key="$route.fullPath" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 24px 0;
}
</style>
