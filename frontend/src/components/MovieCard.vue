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
    return 'https://via.placeholder.com/200x280/f0f0f0/999?text=No+Image'
  }
  return props.movie.poster
})

function handleImgError() {
  imgError.value = true
}
</script>

<template>
  <router-link :to="`/movie/${movie.id}`" class="movie-card">
    <div class="poster-wrapper">
      <img
        :src="posterUrl"
        :alt="movie.title"
        class="poster"
        loading="lazy"
        @error="handleImgError"
        referrerpolicy="no-referrer"
      />
      <div class="poster-overlay">
        <span class="play-icon">▶</span>
      </div>
    </div>
    <div class="info">
      <h3 class="title">{{ movie.title }}</h3>
      <div class="meta">
        <span class="rating" v-if="ratingDisplay !== '暂无'">
          <span class="star">★</span>
          {{ ratingDisplay }}
        </span>
        <span class="year">{{ movie.year }}</span>
      </div>
      <div class="genres" v-if="movie.genres">
        {{ movie.genres }}
      </div>
    </div>
  </router-link>
</template>

<style scoped>
.movie-card {
  display: block;
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.movie-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.movie-card:active {
  transform: translateY(-2px) scale(0.99);
}

.poster-wrapper {
  position: relative;
  aspect-ratio: 2/3;
  overflow: hidden;
  background: var(--bg-tertiary);
}

.poster {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.movie-card:hover .poster {
  transform: scale(1.05);
}

.poster-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.movie-card:hover .poster-overlay {
  opacity: 1;
}

.play-icon {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: var(--primary);
  transform: scale(0.8);
  transition: transform var(--transition-normal);
}

.movie-card:hover .play-icon {
  transform: scale(1);
}

.info {
  padding: 12px;
}

.title {
  font-size: 14px;
  font-weight: 600;
  line-height: 1.4;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.rating {
  color: var(--secondary);
  font-weight: 600;
}

.star {
  font-size: 12px;
}

.year {
  color: var(--text-muted);
}

.genres {
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
