<script setup>
import { ref, computed } from 'vue'
import { normalizePosterUrl } from '@/utils/image'

const props = defineProps({
  movie: {
    type: Object,
    required: true
  },
  rank: {
    type: Number,
    default: null
  }
})

const imgError = ref(false)

const ratingDisplay = computed(() => {
  const rating = props.movie.rating
  if (!rating || rating === 0) return '暂无'
  return Number(rating).toFixed(1)
})

const genresDisplay = computed(() => {
  const genres = props.movie.genres
  if (!genres) return ''
  return String(genres)
    .split(',')
    .map((g) => g.trim())
    .filter(Boolean)
    .join(' · ')
})

const posterUrl = computed(() => {
  if (imgError.value || !props.movie.poster) {
    return ''
  }
  return normalizePosterUrl(props.movie.poster)
})

function handleImgError() {
  imgError.value = true
}
</script>

<template>
  <router-link :to="`/movie/${movie.id}`" class="movie-card-link">
    <el-card shadow="hover" :body-style="{ padding: '0' }" class="movie-card">
      <div class="poster-wrapper">
        <div v-if="rank" class="rank-badge">{{ rank }}</div>
        <el-image
          :src="posterUrl"
          :alt="movie.title"
          fit="cover"
          lazy
          class="poster"
          @error="handleImgError"
        >
          <template #placeholder>
            <div class="image-placeholder">
              <el-icon class="is-loading"><Loading /></el-icon>
            </div>
          </template>
          <template #error>
            <div class="image-error">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
        <div class="poster-overlay">
          <el-icon class="play-icon"><VideoPlay /></el-icon>
        </div>
      </div>
      <div class="info">
        <h3 class="title">{{ movie.title }}</h3>
        <div class="meta">
          <div class="badges">
            <span v-if="ratingDisplay !== '暂无'" class="pill pill-rating">
              <el-icon><Star /></el-icon>
              <span>{{ ratingDisplay }}</span>
            </span>
            <span v-if="movie.hot" class="pill pill-hot">
              <el-icon><TrendCharts /></el-icon>
              <span>{{ movie.hot }}</span>
            </span>
          </div>
          <span v-if="movie.year" class="year">{{ movie.year }}</span>
        </div>
        <div class="genres" v-if="genresDisplay">
          {{ genresDisplay }}
        </div>
      </div>
    </el-card>
  </router-link>
</template>

<style scoped>
.movie-card-link {
  display: block;
  text-decoration: none;
  color: inherit;
}

.movie-card {
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid var(--separator);
}

.movie-card:hover {
  transform: translateY(-2px);
}

.poster-wrapper {
  position: relative;
  aspect-ratio: 2/3;
  overflow: hidden;
  background: #f5f5f5;
}

.rank-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 2;
  width: 30px;
  height: 30px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.95);
  color: var(--douban-orange);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.poster {
  width: 100%;
  height: 100%;
}

.poster :deep(.el-image__inner) {
  transition: transform 0.5s ease;
}

.movie-card:hover .poster :deep(.el-image__inner) {
  transform: scale(1.08);
}

.image-placeholder,
.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #c0c4cc;
  font-size: 32px;
}

.poster-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.movie-card:hover .poster-overlay {
  opacity: 1;
}

.play-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--douban-green);
  transform: scale(0.8);
  transition: transform 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.movie-card:hover .play-icon {
  transform: scale(1);
}

.info {
  padding: 12px 12px 14px;
  background: var(--bg-surface);
}

.title {
  font-size: 15px;
  font-weight: 650;
  line-height: 1.4;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: var(--text-primary);
}

.meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  font-size: 13px;
}

.badges {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.pill {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 12px;
  line-height: 1;
  border: 1px solid var(--separator);
  background: rgba(255, 255, 255, 0.7);
  color: var(--text-secondary);
  white-space: nowrap;
}

.pill :deep(.el-icon) {
  font-size: 14px;
}

.pill-rating {
  background: rgba(255, 159, 10, 0.12);
  border-color: rgba(255, 159, 10, 0.18);
  color: #b15b00;
}

.pill-hot {
  background: rgba(52, 199, 89, 0.12);
  border-color: rgba(52, 199, 89, 0.18);
  color: #1f7a36;
}

.year {
  color: var(--text-muted);
  font-size: 13px;
  white-space: nowrap;
}

.genres {
  margin-top: 10px;
  font-size: 12px;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
