<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/api'
import MovieCard from '@/components/MovieCard.vue'
import Skeleton from '@/components/Skeleton.vue'

const route = useRoute()

const movies = ref([])
const loading = ref(true)
const page = ref(1)
const totalPages = ref(1)
const query = ref('')

async function searchMovies() {
  if (!query.value) return
  loading.value = true
  try {
    const res = await api.searchMovies(query.value, page.value, 20)
    movies.value = res.items || []
    totalPages.value = res.totalPages || 1
  } catch (e) {
    console.error('搜索失败:', e)
  } finally {
    loading.value = false
  }
}

function changePage(p) {
  if (p < 1 || p > totalPages.value) return
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
        搜索结果：<span class="keyword">{{ query }}</span>
      </h1>

      <div class="movie-grid" v-if="!loading && movies.length > 0">
        <MovieCard
          v-for="movie in movies"
          :key="movie.id"
          :movie="movie"
        />
      </div>

      <div class="movie-grid" v-else-if="loading">
        <div v-for="i in 20" :key="i" class="skeleton-card">
          <Skeleton height="200px" border-radius="12px" />
          <Skeleton width="80%" height="16px" style="margin-top: 12px" />
          <Skeleton width="50%" height="14px" style="margin-top: 8px" />
        </div>
      </div>

      <div class="empty-state" v-else>
        <p>没有找到与「{{ query }}」相关的电影</p>
      </div>

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
</template>

<style scoped>
.search-page {
  padding: 20px 0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
}

.keyword {
  color: var(--primary);
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 20px;
}

.skeleton-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 12px;
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
  background: var(--bg-primary);
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
</style>
