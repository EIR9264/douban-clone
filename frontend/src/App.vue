<script setup>
import { watch, onMounted, onBeforeUnmount, computed } from 'vue'
import NavBar from '@/components/NavBar.vue'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'
import { useRoute } from 'vue-router'

const userStore = useUserStore()
const notificationStore = useNotificationStore()
const route = useRoute()

const isAdminRoute = computed(() => route.matched?.some((r) => r.path === '/admin'))
const viewKey = computed(() => (isAdminRoute.value ? '/admin' : route.fullPath))
const transitionName = computed(() => (isAdminRoute.value ? 'fade' : 'page'))

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
        <transition :name="transitionName" mode="out-in">
          <component :is="Component" :key="viewKey" />
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
