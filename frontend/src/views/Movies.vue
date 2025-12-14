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
const selectedGenre = ref('')

const genres = ['剧情', '喜剧', '动作', '爱情', '科幻', '动画', '悬疑', '惊悚', '恐怖', '犯罪', '奇幻', '战争', '音乐', '传记']

async function fetchMovies() {
  loading.value = true
  try {
    let res
    if (selectedGenre.value) {
      res = await api.getMoviesByGenre(selectedGenre.value, page.value, 20)
    } else {
      res = await api.getMovies(page.value, 20)
    }
    movies.value = res.items || []
    totalPages.value = res.totalPages || 1
  } catch (e) {
    console.error('加载电影失败:', e)
  } finally {
    loading.value = false
  }
}

function selectGenre(genre) {
  if (selectedGenre.value === genre) {
    selectedGenre.value = ''
  } else {
    selectedGenre.value = genre
  }
  page.value = 1
  fetchMovies()
}

function changePage(p) {
  if (p < 1 || p > totalPages.value) return
  page.value = p
  fetchMovies()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  fetchMovies()
})
</script>

<template>
  <div class="movies-page">
    <div class="container">
      <h1 class="page-title">全部电影</h1>

      <!-- 类型筛选 -->
      <div class="genre-filter">
        <button
          v-for="genre in genres"
          :key="genre"
          class="genre-tag"
          :class="{ active: selectedGenre === genre }"
          @click="selectGenre(genre)"
        >
          {{ genre }}
        </button>
      </div>

      <!-- 电影列表 -->
      <div class="movie-grid" v-if="!loading">
        <MovieCard
          v-for="movie in movies"
          :key="movie.id"
          :movie="movie"
        />
      </div>
      <div class="movie-grid" v-else>
        <div v-for="i in 20" :key="i" class="skeleton-card">
          <Skeleton height="200px" border-radius="12px" />
          <Skeleton width="80%" height="16px" style="margin-top: 12px" />
          <Skeleton width="50%" height="14px" style="margin-top: 8px" />
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-if="!loading && movies.length === 0">
        <p>没有找到相关电影</p>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 1">
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
.movies-page {
  padding: 20px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
}

.genre-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 24px;
}

.genre-tag {
  padding: 8px 16px;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  font-size: 14px;
  color: var(--text-secondary);
  transition: all var(--transition-fast);
}

.genre-tag:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.genre-tag.active {
  background: var(--primary);
  border-color: var(--primary);
  color: white;
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

@media (max-width: 768px) {
  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 16px;
  }
}
</style>
