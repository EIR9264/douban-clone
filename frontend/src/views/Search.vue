<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'
import MovieCard from '@/components/MovieCard.vue'

const route = useRoute()

const movies = ref([])
const loading = ref(true)
const page = ref(1)
const total = ref(0)
const pageSize = 20
const query = ref('')

async function searchMovies() {
  if (!query.value) return
  loading.value = true
  try {
    const res = await api.searchMovies(query.value, page.value, pageSize)
    movies.value = res.items || []
    total.value = res.total || 0
  } catch (e) {
    ElMessage.error('搜索失败')
    console.error('搜索失败:', e)
  } finally {
    loading.value = false
  }
}

function handlePageChange(p) {
  page.value = p
  searchMovies()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(() => route.query.q, (newQ) => {
  if (newQ) {
    query.value = newQ
    page.value = 1
    searchMovies()
  }
}, { immediate: true })
</script>

<template>
  <div class="search-page">
    <div class="container">
      <h1 class="page-title">
        <el-icon><Search /></el-icon>
        搜索结果：<span class="keyword">{{ query }}</span>
      </h1>

      <el-alert
        v-if="!loading && movies.length > 0"
        :title="`找到 ${total} 部相关电影`"
        type="success"
        :closable="false"
        show-icon
        class="result-alert"
      />

      <div class="movie-grid" v-if="!loading && movies.length > 0">
        <MovieCard
          v-for="movie in movies"
          :key="movie.id"
          :movie="movie"
        />
      </div>

      <div class="movie-grid" v-else-if="loading">
        <el-card v-for="i in 20" :key="i" class="skeleton-card" shadow="never">
          <el-skeleton animated>
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 200px; border-radius: 8px" />
              <el-skeleton-item variant="h3" style="width: 80%; margin-top: 12px" />
              <el-skeleton-item variant="text" style="width: 50%; margin-top: 8px" />
            </template>
          </el-skeleton>
        </el-card>
      </div>

      <el-empty
        v-else
        :description="`没有找到与「${query}」相关的电影`"
        :image-size="120"
      >
        <el-button type="primary" @click="$router.push('/movies')">
          浏览全部电影
        </el-button>
      </el-empty>

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
  </div>
</template>

<style scoped>
.search-page {
  padding: 24px 0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title .el-icon {
  color: var(--douban-green);
}

.keyword {
  color: var(--douban-green);
}

.result-alert {
  margin-bottom: 24px;
  border-radius: 8px;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 20px;
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
  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 16px;
  }
}
</style>
