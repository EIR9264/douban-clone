<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import MovieCard from '@/components/MovieCard.vue'
import { normalizePosterUrl } from '@/utils/image'

const activeTab = ref('hot')

const loadingMovies = ref(false)
const hotMovies = ref([])
const topRated = ref([])
const mostReviewed = ref([])
const mostWished = ref([])
const mostWatched = ref([])

const loadingReviews = ref(false)
const reviewPage = ref(1)
const reviewTotal = ref(0)
const reviewPageSize = 20
const topReviews = ref([])

async function loadMovieRankings() {
  loadingMovies.value = true
  try {
    const [hot, top, reviewed, wished, watched] = await Promise.all([
      api.getHotMovies(20),
      api.getTopMovies(20),
      api.getMostReviewedMovies(20),
      api.getMostWishedMovies(20),
      api.getMostWatchedMovies(20),
    ])
    hotMovies.value = hot || []
    topRated.value = top || []
    mostReviewed.value = reviewed || []
    mostWished.value = wished || []
    mostWatched.value = watched || []
  } catch (e) {
    ElMessage.error(e.message || '加载排行榜失败')
  } finally {
    loadingMovies.value = false
  }
}

async function loadTopReviews() {
  loadingReviews.value = true
  try {
    const res = await api.getTopReviews(reviewPage.value, reviewPageSize)
    topReviews.value = res.items || []
    reviewTotal.value = res.total || 0
  } catch (e) {
    ElMessage.error(e.message || '加载评论排行失败')
  } finally {
    loadingReviews.value = false
  }
}

function handleReviewPageChange(p) {
  reviewPage.value = p
  loadTopReviews()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function formatDate(value) {
  if (!value) return ''
  const raw = String(value)
  const normalized = raw.includes('T') ? raw : raw.replace(' ', 'T')
  const d = new Date(normalized)
  if (Number.isNaN(d.getTime())) return raw
  return d.toLocaleDateString()
}

onMounted(async () => {
  await Promise.all([loadMovieRankings(), loadTopReviews()])
})
</script>

<template>
  <div class="rankings-page">
    <div class="container">
      <h1 class="page-title">
        <el-icon><Trophy /></el-icon>
        排行榜
      </h1>

      <el-tabs v-model="activeTab" class="rank-tabs">
        <el-tab-pane label="热度榜" name="hot">
          <transition name="fade" mode="out-in">
            <div v-if="activeTab === 'hot'" key="hot">
              <div v-loading="loadingMovies">
                <transition-group name="list" tag="div" class="movie-grid">
                  <MovieCard v-for="(movie, idx) in hotMovies" :key="movie.id" :movie="movie" :rank="idx + 1" />
                </transition-group>
              </div>
              <el-empty v-if="!loadingMovies && hotMovies.length === 0" description="暂无数据" :image-size="120" />
            </div>
          </transition>
        </el-tab-pane>

        <el-tab-pane label="高分榜" name="top">
          <transition name="fade" mode="out-in">
            <div v-if="activeTab === 'top'" key="top">
              <div v-loading="loadingMovies">
                <transition-group name="list" tag="div" class="movie-grid">
                  <MovieCard v-for="(movie, idx) in topRated" :key="movie.id" :movie="movie" :rank="idx + 1" />
                </transition-group>
              </div>
              <el-empty v-if="!loadingMovies && topRated.length === 0" description="暂无数据" :image-size="120" />
            </div>
          </transition>
        </el-tab-pane>

        <el-tab-pane label="最多评论" name="reviewed">
          <transition name="fade" mode="out-in">
            <div v-if="activeTab === 'reviewed'" key="reviewed">
              <div v-loading="loadingMovies">
                <transition-group name="list" tag="div" class="movie-grid">
                  <MovieCard v-for="(movie, idx) in mostReviewed" :key="movie.id" :movie="movie" :rank="idx + 1" />
                </transition-group>
              </div>
              <el-empty v-if="!loadingMovies && mostReviewed.length === 0" description="暂无数据" :image-size="120" />
            </div>
          </transition>
        </el-tab-pane>

        <el-tab-pane label="最多想看" name="wished">
          <transition name="fade" mode="out-in">
            <div v-if="activeTab === 'wished'" key="wished">
              <div v-loading="loadingMovies">
                <transition-group name="list" tag="div" class="movie-grid">
                  <MovieCard v-for="(movie, idx) in mostWished" :key="movie.id" :movie="movie" :rank="idx + 1" />
                </transition-group>
              </div>
              <el-empty v-if="!loadingMovies && mostWished.length === 0" description="暂无数据" :image-size="120" />
            </div>
          </transition>
        </el-tab-pane>

        <el-tab-pane label="最多看过" name="watched">
          <transition name="fade" mode="out-in">
            <div v-if="activeTab === 'watched'" key="watched">
              <div v-loading="loadingMovies">
                <transition-group name="list" tag="div" class="movie-grid">
                  <MovieCard v-for="(movie, idx) in mostWatched" :key="movie.id" :movie="movie" :rank="idx + 1" />
                </transition-group>
              </div>
              <el-empty v-if="!loadingMovies && mostWatched.length === 0" description="暂无数据" :image-size="120" />
            </div>
          </transition>
        </el-tab-pane>

        <el-tab-pane label="评论排行" name="reviews">
          <transition name="fade" mode="out-in">
            <div v-if="activeTab === 'reviews'" key="reviews" v-loading="loadingReviews">
              <transition-group
                name="list"
                tag="div"
                class="review-list"
                v-if="!loadingReviews && topReviews.length > 0"
              >
                <el-card v-for="(review, idx) in topReviews" :key="review.id" class="review-card" shadow="never">
                  <div class="review-row">
                    <div class="rank-num">
                      {{ (reviewPage - 1) * reviewPageSize + idx + 1 }}
                    </div>
                    <router-link :to="`/movie/${review.movieId}`" class="poster-link">
                      <el-image :src="normalizePosterUrl(review.moviePoster)" fit="cover" class="poster-thumb" lazy>
                        <template #error>
                          <div class="poster-fallback">
                            <el-icon><Picture /></el-icon>
                          </div>
                        </template>
                      </el-image>
                    </router-link>
                    <div class="review-body">
                      <div class="review-head">
                        <div class="movie-title">
                          <router-link :to="`/movie/${review.movieId}`" class="movie-link">
                            {{ review.movieTitle || `电影 #${review.movieId}` }}
                          </router-link>
                          <span v-if="review.movieYear" class="movie-year">{{ review.movieYear }}</span>
                        </div>
                        <span v-if="review.createdAt" class="time">{{ formatDate(review.createdAt) }}</span>
                      </div>
                      <div class="review-meta">
                        <el-avatar :size="24" :src="review.avatar" class="avatar">
                          <el-icon><User /></el-icon>
                        </el-avatar>
                        <span class="username">{{ review.username || '匿名用户' }}</span>
                        <el-tag
                          v-if="review.userRating !== null && review.userRating !== undefined"
                          size="small"
                          type="info"
                          effect="plain"
                          class="user-rating"
                        >
                          {{ review.userRating }} 分
                        </el-tag>
                        <el-tag size="small" type="warning" effect="plain" class="likes">
                          <el-icon><Pointer /></el-icon>
                          {{ review.likes || 0 }}
                        </el-tag>
                      </div>
                      <div class="review-content">
                        <span v-if="review.title" class="review-title">{{ review.title }}</span>
                        <span class="review-text">{{ review.content }}</span>
                      </div>
                    </div>
                  </div>
                </el-card>
              </transition-group>

              <el-empty v-if="!loadingReviews && topReviews.length === 0" description="暂无评论" :image-size="120" />

              <div class="pagination-wrapper" v-if="reviewTotal > reviewPageSize && !loadingReviews">
                <el-pagination
                  v-model:current-page="reviewPage"
                  :page-size="reviewPageSize"
                  :total="reviewTotal"
                  layout="prev, pager, next"
                  background
                  @current-change="handleReviewPageChange"
                />
              </div>
            </div>
          </transition>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped>
.rankings-page {
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title .el-icon {
  color: var(--douban-orange);
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 20px;
  min-height: 120px;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-card {
  border-radius: 12px;
}

.review-card:hover {
  transform: none;
  box-shadow: none;
}

.review-row {
  display: grid;
  grid-template-columns: 44px 64px 1fr;
  gap: 14px;
  align-items: start;
}

.rank-num {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: var(--el-color-primary-light-9);
  color: var(--douban-green);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.movie-link {
  font-weight: 600;
  color: var(--text-primary);
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-link:hover {
  color: var(--douban-green);
}

.review-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
  min-width: 0;
}

.movie-title {
  display: flex;
  align-items: baseline;
  gap: 8px;
  min-width: 0;
  flex: 1 1 auto;
}

.movie-year {
  color: var(--text-muted);
  font-size: 13px;
  flex: 0 0 auto;
}

.time {
  color: var(--text-muted);
  font-size: 12px;
  flex: 0 0 auto;
  white-space: nowrap;
}

.poster-link {
  display: block;
  width: 64px;
  height: 96px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--separator);
  background: var(--bg-card);
}

.poster-thumb {
  width: 64px;
  height: 96px;
  display: block;
}

.poster-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  background: rgba(60, 60, 67, 0.06);
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: var(--text-muted);
  font-size: 13px;
}

.avatar {
  flex: 0 0 auto;
}

.username {
  color: var(--text-secondary);
  font-weight: 500;
}

.likes :deep(.el-icon) {
  margin-right: 2px;
}

.user-rating {
  margin-left: 2px;
}

.review-content {
  color: var(--text-secondary);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.review-title {
  color: var(--text-primary);
  margin-right: 6px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 16px;
  }

  .review-row {
    grid-template-columns: 36px 56px 1fr;
    gap: 12px;
  }

  .rank-num {
    width: 36px;
    height: 36px;
    border-radius: 10px;
  }

  .poster-link,
  .poster-thumb {
    width: 56px;
    height: 84px;
    border-radius: 10px;
  }
}
</style>
