<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const searchQuery = ref('')
const showUserMenu = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn)
const user = computed(() => userStore.user)

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({ name: 'Search', query: { q: searchQuery.value.trim() } })
    searchQuery.value = ''
  }
}

function logout() {
  userStore.logout()
  showUserMenu.value = false
  router.push('/')
}
</script>

<template>
  <header class="navbar">
    <div class="navbar-container container">
      <router-link to="/" class="logo">
        <span class="logo-icon">🎬</span>
        <span class="logo-text">豆瓣电影</span>
      </router-link>

      <nav class="nav-links hide-mobile">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/movies" class="nav-link">电影</router-link>
      </nav>

      <div class="search-box">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索电影..."
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="m21 21-4.35-4.35"/>
          </svg>
        </button>
      </div>

      <div class="nav-right">
        <template v-if="isLoggedIn">
          <div class="user-menu" @click="showUserMenu = !showUserMenu">
            <img :src="user?.avatar" alt="" class="user-avatar" />
            <span class="user-name hide-mobile">{{ user?.username }}</span>
            <div v-if="showUserMenu" class="user-dropdown">
              <router-link to="/profile" class="dropdown-item" @click="showUserMenu = false">
                我的主页
              </router-link>
              <router-link to="/settings" class="dropdown-item" @click="showUserMenu = false">
                账号设置
              </router-link>
              <div class="dropdown-divider"></div>
              <button class="dropdown-item" @click="logout">退出登录</button>
            </div>
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="btn btn-outline btn-sm">登录</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-color);
  z-index: 100;
}

.navbar-container {
  display: flex;
  align-items: center;
  gap: 24px;
  height: 64px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 18px;
}

.logo-icon {
  font-size: 24px;
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-link {
  padding: 8px 16px;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  transition: all var(--transition-fast);
}

.nav-link:hover {
  color: var(--text-primary);
  background: var(--bg-secondary);
}

.nav-link.router-link-active {
  color: var(--primary);
  background: var(--primary-light);
}

.search-box {
  flex: 1;
  max-width: 400px;
  position: relative;
}

.search-input {
  width: 100%;
  padding: 10px 44px 10px 16px;
  background: var(--bg-secondary);
  border-radius: 24px;
  font-size: 14px;
  transition: all var(--transition-fast);
}

.search-input:focus {
  background: white;
  box-shadow: var(--shadow-md);
}

.search-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  padding: 6px;
  color: var(--text-muted);
  transition: color var(--transition-fast);
}

.search-btn:hover {
  color: var(--primary);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-menu {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: 24px;
  cursor: pointer;
  transition: background var(--transition-fast);
}

.user-menu:hover {
  background: var(--bg-secondary);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 150px;
  background: white;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  animation: dropdown-in 0.2s ease-out;
}

@keyframes dropdown-in {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 12px 16px;
  text-align: left;
  font-size: 14px;
  color: var(--text-primary);
  transition: background var(--transition-fast);
}

.dropdown-item:hover {
  background: var(--bg-secondary);
}

.dropdown-divider {
  height: 1px;
  background: var(--border-color);
}

.btn-sm {
  padding: 8px 16px;
  font-size: 14px;
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
