<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import MovieCard from '@/components/MovieCard.vue'
import Skeleton from '@/components/Skeleton.vue'

const topRated = ref([])
const recent = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await api.getRecommendations()
    topRated.value = res.topRated || []
    recent.value = res.recent || []
  } catch (e) {
    console.error('加载推荐失败:', e)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="home">
    <!-- Hero Banner -->
    <section class="hero">
      <div class="container">
        <h1 class="hero-title">发现好电影</h1>
        <p class="hero-subtitle">数百万人使用的电影社区，记录你看过的每一部电影</p>
      </div>
    </section>

    <!-- Top Rated -->
    <section class="section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">高分电影</h2>
          <router-link to="/movies" class="view-all">查看更多 →</router-link>
        </div>

        <div class="movie-grid" v-if="!loading">
          <MovieCard
            v-for="movie in topRated"
            :key="movie.id"
            :movie="movie"
          />
        </div>
        <div class="movie-grid" v-else>
          <div v-for="i in 5" :key="i" class="skeleton-card">
            <Skeleton height="200px" border-radius="12px" />
            <Skeleton width="80%" height="16px" style="margin-top: 12px" />
            <Skeleton width="50%" height="14px" style="margin-top: 8px" />
          </div>
        </div>
      </div>
    </section>

    <!-- Recent -->
    <section class="section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">最新上映</h2>
          <router-link to="/movies" class="view-all">查看更多 →</router-link>
        </div>

        <div class="movie-grid" v-if="!loading">
          <MovieCard
            v-for="movie in recent"
            :key="movie.id"
            :movie="movie"
          />
        </div>
        <div class="movie-grid" v-else>
          <div v-for="i in 5" :key="i" class="skeleton-card">
            <Skeleton height="200px" border-radius="12px" />
            <Skeleton width="80%" height="16px" style="margin-top: 12px" />
            <Skeleton width="50%" height="14px" style="margin-top: 8px" />
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.hero {
  background: linear-gradient(135deg, #00b51d 0%, #009a18 100%);
  color: white;
  padding: 60px 0;
  margin-bottom: 32px;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 12px;
  animation: fade-up 0.6s ease-out;
}

.hero-subtitle {
  font-size: 18px;
  opacity: 0.9;
  animation: fade-up 0.6s ease-out 0.1s both;
}

@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-title {
  font-size: 22px;
  font-weight: 600;
}

.view-all {
  font-size: 14px;
  color: var(--primary);
  transition: opacity var(--transition-fast);
}

.view-all:hover {
  opacity: 0.8;
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

@media (max-width: 768px) {
  .hero {
    padding: 40px 0;
  }

  .hero-title {
    font-size: 32px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 16px;
  }
}
</style>
