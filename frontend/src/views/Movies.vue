<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import MovieCard from '@/components/MovieCard.vue'

const movies = ref([])
const loading = ref(true)
const page = ref(1)
const total = ref(0)
const pageSize = 20
const selectedGenres = ref([])

const genres = ['剧情', '喜剧', '动作', '爱情', '科幻', '动画', '悬疑', '惊悚', '恐怖', '犯罪', '奇幻', '战争', '音乐', '传记']

async function fetchMovies() {
  loading.value = true
  try {
    let res
    if (selectedGenres.value.length > 0) {
      res = await api.getMoviesByGenres(selectedGenres.value, page.value, pageSize)
    } else {
      res = await api.getMovies(page.value, pageSize)
    }
    movies.value = res.items || []
    total.value = res.total || 0
  } catch (e) {
    ElMessage.error('加载电影失败')
    console.error('加载电影失败:', e)
  } finally {
    loading.value = false
  }
}

function selectGenre(genre) {
  const current = selectedGenres.value.slice()
  const idx = current.indexOf(genre)
  if (idx >= 0) current.splice(idx, 1)
  else current.push(genre)
  selectedGenres.value = current
  page.value = 1
  fetchMovies()
}

function clearGenres() {
  if (selectedGenres.value.length === 0) return
  selectedGenres.value = []
  page.value = 1
  fetchMovies()
}

function handlePageChange(p) {
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
      <h1 class="page-title">
        <el-icon><Film /></el-icon>
        全部电影
      </h1>

      <!-- 类型筛选 -->
      <el-card class="filter-card" shadow="never">
        <div class="filter-header">
          <el-icon><Filter /></el-icon>
          <span>类型筛选</span>
          <transition-group name="list" tag="div" class="selected-tags">
            <el-tag
              v-for="g in selectedGenres"
              :key="g"
              type="success"
              closable
              @close="selectGenre(g)"
              size="small"
            >
              {{ g }}
            </el-tag>
          </transition-group>
          <el-button v-if="selectedGenres.length > 0" class="clear-btn" link type="primary" @click="clearGenres">
            清空
          </el-button>
        </div>
        <div class="genre-filter">
          <el-check-tag
            v-for="genre in genres"
            :key="genre"
            :checked="selectedGenres.includes(genre)"
            @change="selectGenre(genre)"
          >
            {{ genre }}
          </el-check-tag>
        </div>
      </el-card>

      <!-- 电影列表 -->
      <div class="movie-grid-wrapper" v-loading="loading">
        <transition-group v-if="movies.length > 0" name="list" tag="div" class="movie-grid">
          <MovieCard v-for="movie in movies" :key="movie.id" :movie="movie" />
        </transition-group>
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
      </div>

      <!-- 空状态 -->
      <el-empty
        v-if="!loading && movies.length === 0"
        description="没有找到相关电影"
        :image-size="120"
      >
        <el-button type="primary" @click="clearGenres">
          查看全部电影
        </el-button>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > pageSize">
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
.movies-page {
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title .el-icon {
  color: var(--douban-green);
}

.filter-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.filter-card:hover {
  transform: none;
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-weight: 500;
  color: var(--text-secondary);
  flex-wrap: wrap;
}

.selected-tags {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 8px;
}

.clear-btn {
  margin-left: auto;
}

.genre-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.genre-filter :deep(.el-check-tag) {
  transition: background-color 0.2s ease, color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.genre-filter :deep(.el-check-tag:hover) {
  transform: translateY(-1px);
}

.genre-filter :deep(.el-check-tag.is-checked) {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
}

.genre-filter .el-check-tag {
  border-radius: 20px;
  padding: 6px 16px;
}

.movie-grid-wrapper {
  min-height: 240px;
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
