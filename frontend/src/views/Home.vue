<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import MovieCard from '@/components/MovieCard.vue'

const topRated = ref([])
const recent = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await api.getRecommendations()
    topRated.value = res.topRated || []
    recent.value = res.recent || []
  } catch (e) {
    ElMessage.error('加载推荐失败')
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
          <h2 class="section-title">
            <el-icon><Star /></el-icon>
            高分电影
          </h2>
          <router-link to="/movies" class="view-all">
            查看更多
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <div class="movie-grid" v-if="!loading">
          <MovieCard
            v-for="movie in topRated"
            :key="movie.id"
            :movie="movie"
          />
        </div>
        <div class="movie-grid" v-else>
          <el-card v-for="i in 5" :key="i" class="skeleton-card" shadow="never">
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
    </section>

    <!-- Recent -->
    <section class="section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Film /></el-icon>
            最新上映
          </h2>
          <router-link to="/movies" class="view-all">
            查看更多
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <div class="movie-grid" v-if="!loading">
          <MovieCard
            v-for="movie in recent"
            :key="movie.id"
            :movie="movie"
          />
        </div>
        <div class="movie-grid" v-else>
          <el-card v-for="i in 5" :key="i" class="skeleton-card" shadow="never">
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
    </section>
  </div>
</template>

<style scoped>
.hero {
  background: linear-gradient(135deg, #00b386 0%, #009970 100%);
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
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title .el-icon {
  color: var(--douban-orange);
}

.view-all {
  font-size: 14px;
  color: var(--douban-green);
  display: flex;
  align-items: center;
  gap: 4px;
  transition: opacity 0.2s;
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
  border-radius: 12px;
}

.skeleton-card:hover {
  transform: none;
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
