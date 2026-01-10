<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'
import MovieCard from '@/components/MovieCard.vue'

const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const activeTab = ref('wish')
const collections = ref([])
const loading = ref(true)
const page = ref(1)
const total = ref(0)
const pageSize = 20

async function fetchCollections() {
  loading.value = true
  try {
    const res = await api.getCollections(activeTab.value, page.value, pageSize)
    collections.value = res.items || []
    total.value = res.total || 0
  } catch (e) {
    ElMessage.error('加载收藏失败')
    console.error('加载收藏失败:', e)
  } finally {
    loading.value = false
  }
}

function handleTabChange(tab) {
  activeTab.value = tab
  page.value = 1
  fetchCollections()
}

function handlePageChange(p) {
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
      <!-- 用户信息卡片 -->
      <el-card class="profile-header" shadow="never">
        <div class="profile-content">
          <el-avatar :size="100" :src="user?.avatar">
            <el-icon :size="40"><User /></el-icon>
          </el-avatar>
          <div class="profile-info">
            <h1 class="profile-name">{{ user?.username }}</h1>
            <p class="profile-bio">{{ user?.bio || '这个人很懒，什么都没写' }}</p>
            <p class="profile-meta">
              <el-icon><Calendar /></el-icon>
              加入于 {{ user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : '-' }}
            </p>
          </div>
          <el-button class="edit-btn" @click="router.push('/settings')">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </el-card>

      <!-- 收藏内容 -->
      <el-card class="collection-section" shadow="never">
        <template #header>
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="想看" name="wish">
              <template #label>
                <el-icon><Star /></el-icon>
                想看
              </template>
            </el-tab-pane>
            <el-tab-pane label="在看" name="watching">
              <template #label>
                <el-icon><View /></el-icon>
                在看
              </template>
            </el-tab-pane>
            <el-tab-pane label="看过" name="watched">
              <template #label>
                <el-icon><Select /></el-icon>
                看过
              </template>
            </el-tab-pane>
          </el-tabs>
        </template>

        <transition name="fade" mode="out-in">
          <div :key="activeTab" class="collection-body">
            <div class="movie-grid-wrapper" v-loading="loading">
              <transition-group v-if="collections.length > 0" name="list" tag="div" class="movie-grid">
                <MovieCard v-for="item in collections" :key="item.id" :movie="item.movie" />
              </transition-group>

              <el-empty
                v-if="!loading && collections.length === 0"
                :description="`还没有${activeTab === 'wish' ? '想看' : activeTab === 'watching' ? '在看' : '看过'}的电影`"
                :image-size="120"
              >
                <el-button type="primary" @click="router.push('/movies')">去发现电影</el-button>
              </el-empty>
            </div>

            <div class="pagination-wrapper" v-if="total > pageSize && !loading">
              <el-pagination
                v-model:current-page="page"
                :page-size="pageSize"
                :total="total"
                layout="prev, pager, next"
                background
                @current-change="handlePageChange"
              />
            </div>
          </div>
        </transition>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  padding: 24px 0;
}

.profile-header {
  margin-bottom: 24px;
  border-radius: 16px;
  animation: fade-up 0.4s ease-out;
}

.profile-header:hover {
  transform: none;
}

@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
}

.profile-content {
  display: flex;
  align-items: center;
  gap: 24px;
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.profile-bio {
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.profile-meta {
  font-size: 13px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 4px;
}

.edit-btn {
  align-self: flex-start;
}

.collection-section {
  border-radius: 16px;
}

.collection-section:hover {
  transform: none;
}

.collection-section :deep(.el-card__header) {
  padding-bottom: 0;
  border-bottom: none;
}

.collection-section :deep(.el-tabs__header) {
  margin: 0;
}

.collection-section :deep(.el-tabs__item) {
  display: flex;
  align-items: center;
  gap: 6px;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 20px;
}

.movie-grid-wrapper {
  min-height: 240px;
}

.skeleton-card {
  border-radius: 12px;
}

.skeleton-card:hover {
  transform: none;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
    text-align: center;
  }

  .edit-btn {
    align-self: center;
  }

  .profile-meta {
    justify-content: center;
  }

  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 16px;
  }
}
</style>
