<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const searchQuery = ref('')

const isLoggedIn = computed(() => userStore.isLoggedIn)
const user = computed(() => userStore.user)

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({ name: 'Search', query: { q: searchQuery.value.trim() } })
    searchQuery.value = ''
  }
}

function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'settings') {
    router.push('/settings')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/')
  }
}
</script>

<template>
  <header class="navbar">
    <div class="navbar-container container">
      <router-link to="/" class="logo">
        <el-icon :size="28" color="#00b386"><Film /></el-icon>
        <span class="logo-text">豆瓣电影</span>
      </router-link>

      <el-menu
        mode="horizontal"
        :ellipsis="false"
        router
        class="nav-menu hide-mobile"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/movies">电影</el-menu-item>
      </el-menu>

      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索电影..."
          :prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        />
      </div>

      <div class="nav-right">
        <template v-if="isLoggedIn">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-trigger">
              <el-avatar :size="32" :src="user?.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name hide-mobile">{{ user?.username }}</span>
              <el-icon class="hide-mobile"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  我的主页
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  账号设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" round @click="router.push('/login')">
            登录
          </el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script>
import { Search } from '@element-plus/icons-vue'
export default {
  data() {
    return { Search }
  }
}
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #f0f0f0;
  z-index: 100;
}

.navbar-container {
  display: flex;
  align-items: center;
  gap: 24px;
  height: 64px;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 18px;
  color: var(--text-primary);
}

.logo-text {
  background: linear-gradient(135deg, #00b386, #00a67d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  border: none !important;
  background: transparent !important;
  height: auto !important;
  display: flex;
  align-items: center;
}

.nav-menu :deep(.el-menu-item) {
  font-size: 15px;
  border-bottom: none !important;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
  border-radius: 20px;
  margin: 0 4px;
}

.nav-menu :deep(.el-menu-item:hover) {
  background-color: #f5f5f5 !important;
}

.nav-menu :deep(.el-menu-item.is-active) {
  color: var(--douban-green) !important;
  background-color: var(--el-color-primary-light-9) !important;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.search-box :deep(.el-input__wrapper) {
  border-radius: 20px;
  background-color: #f5f5f5;
  box-shadow: none !important;
}

.search-box :deep(.el-input__wrapper:hover),
.search-box :deep(.el-input__wrapper:focus-within) {
  background-color: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1) !important;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: 24px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-trigger:hover {
  background: #f5f5f5;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .navbar-container {
    gap: 12px;
  }

  .logo-text {
    display: none;
  }

  .search-box {
    max-width: none;
  }
}
</style>
