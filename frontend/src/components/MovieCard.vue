<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  movie: {
    type: Object,
    required: true
  }
})

const imgError = ref(false)

const ratingDisplay = computed(() => {
  const rating = props.movie.rating
  if (!rating || rating === 0) return '暂无'
  return Number(rating).toFixed(1)
})

const posterUrl = computed(() => {
  if (imgError.value || !props.movie.poster) {
    return ''
  }
  return props.movie.poster
})

function handleImgError() {
  imgError.value = true
}
</script>

<template>
  <router-link :to="`/movie/${movie.id}`" class="movie-card-link">
    <el-card shadow="hover" :body-style="{ padding: '0' }" class="movie-card">
      <div class="poster-wrapper">
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
          <el-tag v-if="ratingDisplay !== '暂无'" type="warning" size="small" effect="plain">
            <el-icon><Star /></el-icon>
            {{ ratingDisplay }}
          </el-tag>
          <span class="year">{{ movie.year }}</span>
        </div>
        <div class="genres" v-if="movie.genres">
          {{ movie.genres }}
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
}

.movie-card:hover {
  transform: translateY(-6px);
}

.poster-wrapper {
  position: relative;
  aspect-ratio: 2/3;
  overflow: hidden;
  background: #f5f5f5;
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
  padding: 14px;
}

.title {
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: var(--text-primary);
}

.meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.meta :deep(.el-tag) {
  border-radius: 4px;
}

.meta :deep(.el-tag .el-icon) {
  margin-right: 2px;
}

.year {
  color: var(--text-muted);
  font-size: 13px;
}

.genres {
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
