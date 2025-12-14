<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import api from '@/api'
import MovieCard from '@/components/MovieCard.vue'
import Skeleton from '@/components/Skeleton.vue'

const userStore = useUserStore()

const user = computed(() => userStore.user)
const activeTab = ref('wish')
const collections = ref([])
const loading = ref(true)
const page = ref(1)
const totalPages = ref(1)

const tabs = [
  { key: 'wish', label: '想看' },
  { key: 'watching', label: '在看' },
  { key: 'watched', label: '看过' }
]

async function fetchCollections() {
  loading.value = true
  try {
    const res = await api.getCollections(activeTab.value, page.value, 20)
    collections.value = res.items || []
    totalPages.value = res.totalPages || 1
  } catch (e) {
    console.error('加载收藏失败:', e)
  } finally {
    loading.value = false
  }
}

function switchTab(tab) {
  activeTab.value = tab
  page.value = 1
  fetchCollections()
}

function changePage(p) {
  if (p < 1 || p > totalPages.value) return
  page.value = p
  fetchCollections()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  fetchCollections()
})
</script>

<template>
  <div class="profile-page">
    <div class="container">
      <!-- 用户信息 -->
      <div class="profile-header">
        <img :src="user?.avatar" alt="" class="profile-avatar" />
        <div class="profile-info">
          <h1 class="profile-name">{{ user?.username }}</h1>
          <p class="profile-bio">{{ user?.bio || '这个人很懒，什么都没写' }}</p>
          <p class="profile-meta">
            加入于 {{ user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : '-' }}
          </p>
        </div>
      </div>

      <!-- 收藏标签页 -->
      <div class="collection-section">
        <div class="tabs">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            class="tab-btn"
            :class="{ active: activeTab === tab.key }"
            @click="switchTab(tab.key)"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 电影列表 -->
        <div class="movie-grid" v-if="!loading && collections.length > 0">
          <MovieCard
            v-for="item in collections"
            :key="item.id"
            :movie="item.movie"
          />
        </div>

        <div class="movie-grid" v-else-if="loading">
          <div v-for="i in 10" :key="i" class="skeleton-card">
            <Skeleton height="200px" border-radius="12px" />
            <Skeleton width="80%" height="16px" style="margin-top: 12px" />
            <Skeleton width="50%" height="14px" style="margin-top: 8px" />
          </div>
        </div>

        <div class="empty-state" v-else>
          <p>还没有{{ tabs.find(t => t.key === activeTab)?.label }}的电影</p>
          <router-link to="/movies" class="btn btn-primary" style="margin-top: 16px">
            去发现电影
          </router-link>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="totalPages > 1 && !loading">
          <button
            class="page-btn"
            :disabled="page === 1"
            @click="changePage(page - 1)"
          >
            上一页
          </button>
          <span class="page-info">{{ page }} / {{ totalPages }}</span>
          <button
            class="page-btn"
            :disabled="page === totalPages"
            @click="changePage(page + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  padding: 20px 0;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 32px;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  margin-bottom: 32px;
  animation: fade-up 0.4s ease-out;
}

@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
}

.profile-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: var(--shadow-md);
}

.profile-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.profile-bio {
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.profile-meta {
  font-size: 13px;
  color: var(--text-muted);
}

.collection-section {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 16px;
}

.tab-btn {
  padding: 10px 24px;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  transition: all var(--transition-fast);
}

.tab-btn:hover {
  color: var(--text-primary);
  background: var(--bg-secondary);
}

.tab-btn.active {
  color: white;
  background: var(--primary);
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 20px;
}

.skeleton-card {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-muted);
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
}

.page-btn {
  padding: 10px 20px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: 14px;
  transition: all var(--transition-fast);
}

.page-btn:hover:not(:disabled) {
  border-color: var(--primary);
  color: var(--primary);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 16px;
  }
}
</style>
