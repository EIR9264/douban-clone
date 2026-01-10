<script setup>
import { ref, onMounted, computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'
import { useUserStore } from '@/stores/user'
import RatingStars from '@/components/RatingStars.vue'
import { normalizePosterUrl } from '@/utils/image'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const movie = ref(null)
const reviews = ref([])
const likingIds = reactive(new Set())
const userRating = ref(0)
const userCollection = ref(null)
const loading = ref(true)
const showReviewModal = ref(false)
const reviewTitle = ref('')
const reviewContent = ref('')
const submitting = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn)

const posterUrl = computed(() => {
  if (!movie.value?.poster) {
    return ''
  }
  return normalizePosterUrl(movie.value.poster)
})

function parseMovieMedia(raw) {
  if (!raw) return { trailer: null, stills: [] }
  const str = String(raw).trim()
  if (!str) return { trailer: null, stills: [] }
  try {
    const parsed = JSON.parse(str)
    if (Array.isArray(parsed)) {
      return { trailer: null, stills: parsed.map(normalizeMediaItem).filter(Boolean) }
    }
    const trailer = normalizeMediaItem(parsed?.trailer)
    const stills = Array.isArray(parsed?.stills) ? parsed.stills.map(normalizeMediaItem).filter(Boolean) : []
    return { trailer, stills }
  } catch {
    return { trailer: null, stills: str.split(',').map(s => s.trim()).filter(Boolean).map((url) => ({ url })) }
  }
}

function normalizeMediaItem(item) {
  if (!item) return null
  if (typeof item === 'string') return item.trim() ? { url: item.trim() } : null
  if (typeof item === 'object' && item.url) return { url: String(item.url).trim() }
  return null
}

const media = computed(() => parseMovieMedia(movie.value?.images))
const trailerUrl = computed(() => media.value.trailer?.url || '')
const stillUrls = computed(() => media.value.stills.map(s => s.url).filter(Boolean))

async function fetchMovie() {
  loading.value = true
  try {
    const id = route.params.id
    movie.value = await api.getMovie(id)

    const reviewRes = await api.getReviews(id, 1, 10)
    reviews.value = reviewRes.items || []

    if (isLoggedIn.value) {
      const status = await api.getMovieStatus(id)
      userRating.value = status.rating || 0
      userCollection.value = status.collection
    }
  } catch (e) {
    ElMessage.error(e.message || '加载电影详情失败')
  } finally {
    loading.value = false
  }
}

async function toggleReviewLike(review) {
  if (!isLoggedIn.value) return
  if (!review?.id) return

  if (likingIds.has(review.id)) return
  likingIds.add(review.id)
  try {
    const res = await api.likeReview(review.id)
    review.liked = !!res.liked
    if (res.likeCount !== undefined) review.likeCount = res.likeCount
    else if (res.likes !== undefined) review.likeCount = res.likes
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    likingIds.delete(review.id)
  }
}

async function handleRate(score) {
  if (!isLoggedIn.value) return
  try {
    if (score === userRating.value) {
      await api.removeRating(movie.value.id)
      userRating.value = 0
      ElMessage.success('已取消评分')
    } else {
      await api.rateMovie(movie.value.id, score)
      userRating.value = score
      ElMessage.success('评分成功')
    }
    const updated = await api.getMovie(movie.value.id)
    movie.value.rating = updated.rating
    movie.value.ratingCount = updated.ratingCount
  } catch (e) {
    ElMessage.error(e.message)
  }
}

async function handleCollect(status) {
  if (!isLoggedIn.value) return
  try {
    if (userCollection.value === status) {
      await api.removeCollection(movie.value.id)
      userCollection.value = null
      ElMessage.success('已取消收藏')
    } else {
      await api.collectMovie(movie.value.id, status)
      userCollection.value = status
      ElMessage.success(status === 'wish' ? '已标记想看' : '已标记看过')
    }
  } catch (e) {
    ElMessage.error(e.message)
  }
}

async function submitReview() {
  if (!reviewContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  submitting.value = true
  try {
    const newReview = await api.createReview(movie.value.id, {
      title: reviewTitle.value,
      content: reviewContent.value
    })
    newReview.userRating = userRating.value ? userRating.value : null
    newReview.liked = false
    newReview.likeCount = newReview.likeCount ?? 0
    newReview.username = userStore.user?.username
    newReview.avatar = userStore.user?.avatar
    reviews.value.unshift(newReview)
    showReviewModal.value = false
    reviewTitle.value = ''
    reviewContent.value = ''
    ElMessage.success('评论发布成功')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    submitting.value = false
  }
}

async function deleteReview(reviewId) {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.deleteReview(reviewId)
    reviews.value = reviews.value.filter(r => r.id !== reviewId)
    ElMessage.success('删除成功')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

function goToLogin() {
  router.push({ name: 'Login', query: { redirect: route.fullPath } })
}

onMounted(() => {
  fetchMovie()
})
</script>

<template>
  <div class="movie-detail">
    <div class="container">
      <!-- Loading skeleton -->
      <div class="detail-skeleton" v-if="loading">
        <div class="skeleton-poster">
          <el-skeleton animated>
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 400px; border-radius: 12px" />
            </template>
          </el-skeleton>
        </div>
        <div class="skeleton-info">
          <el-skeleton animated :rows="6" />
        </div>
      </div>

      <!-- Movie content -->
      <div class="detail-content" v-else-if="movie">
        <div class="poster-section">
          <el-image :src="posterUrl" :alt="movie.title" fit="cover" class="poster">
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>

        <div class="info-section">
          <h1 class="title">{{ movie.title }}</h1>
          <p class="original-title" v-if="movie.originalTitle">{{ movie.originalTitle }}</p>

          <el-card class="rating-card" shadow="never">
            <div class="rating-box">
              <div class="rating-score">
                <span class="score">{{ movie.rating ? Number(movie.rating).toFixed(1) : '暂无' }}</span>
                <span class="label">豆瓣评分</span>
              </div>
              <el-divider direction="vertical" />
              <div class="rating-count">
                <el-icon><User /></el-icon>
                {{ movie.ratingCount || 0 }} 人评价
              </div>
            </div>
          </el-card>

          <el-descriptions :column="1" border class="meta-list">
            <el-descriptions-item label="导演" v-if="movie.directors">
              {{ movie.directors }}
            </el-descriptions-item>
            <el-descriptions-item label="主演" v-if="movie.actors">
              {{ movie.actors }}
            </el-descriptions-item>
            <el-descriptions-item label="类型" v-if="movie.genres">
              <el-tag v-for="genre in movie.genres.split(',')" :key="genre" size="small" class="genre-tag">
                {{ genre }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="地区" v-if="movie.country">
              {{ movie.country }}
            </el-descriptions-item>
            <el-descriptions-item label="年份" v-if="movie.year">
              {{ movie.year }}
            </el-descriptions-item>
            <el-descriptions-item label="片长" v-if="movie.duration">
              {{ movie.duration }} 分钟
            </el-descriptions-item>
          </el-descriptions>

          <!-- User actions -->
          <el-card class="user-actions-card" shadow="never">
            <template v-if="isLoggedIn">
              <div class="my-rating">
                <span class="action-label">我的评分</span>
                <RatingStars :score="userRating" size="large" show-text @update:score="handleRate" />
              </div>

              <el-divider />

              <div class="collect-buttons">
                <el-button
                  :type="userCollection === 'wish' ? 'primary' : 'default'"
                  size="large"
                  @click="handleCollect('wish')"
                >
                  <el-icon><Star /></el-icon>
                  想看
                </el-button>
                <el-button
                  :type="userCollection === 'watched' ? 'primary' : 'default'"
                  size="large"
                  @click="handleCollect('watched')"
                >
                  <el-icon><Select /></el-icon>
                  看过
                </el-button>
              </div>
            </template>
            <template v-else>
              <el-empty description="登录后可以评分、收藏和评论" :image-size="60">
                <el-button type="primary" @click="goToLogin">立即登录</el-button>
              </el-empty>
            </template>
          </el-card>
        </div>
      </div>

      <!-- Summary section -->
      <el-card class="summary-section" shadow="never" v-if="movie">
        <template #header>
          <span class="section-title">剧情简介</span>
        </template>
        <p class="summary">{{ movie.summary || '暂无简介' }}</p>
      </el-card>

      <!-- Media section -->
      <el-card class="summary-section" shadow="never" v-if="movie">
        <template #header>
          <span class="section-title">预告片与剧照</span>
        </template>

        <el-empty
          v-if="!trailerUrl && stillUrls.length === 0"
          description="暂无预告片/剧照"
          :image-size="80"
        />

        <div class="media-section" v-if="trailerUrl">
          <div class="media-title">预告片</div>
          <video v-if="/\\.(mp4|webm|ogg)(\\?.*)?$/i.test(trailerUrl)" class="trailer" controls :src="trailerUrl" />
          <el-link v-else :href="trailerUrl" target="_blank" type="primary">打开预告片链接</el-link>
        </div>

        <div class="media-section" v-if="stillUrls.length > 0">
          <div class="media-title">剧照</div>
          <div class="still-grid">
            <el-image
              v-for="url in stillUrls"
              :key="url"
              :src="url"
              fit="cover"
              class="still"
              :preview-src-list="stillUrls"
              preview-teleported
            />
          </div>
        </div>
      </el-card>

      <!-- Reviews section -->
      <el-card class="reviews-section" shadow="never" v-if="movie">
        <template #header>
          <div class="section-header">
            <span class="section-title">短评</span>
            <el-button type="primary" @click="showReviewModal = true" v-if="isLoggedIn">
              <el-icon><Edit /></el-icon>
              写评论
            </el-button>
            <el-button @click="goToLogin" v-else>登录后评论</el-button>
          </div>
        </template>

        <div class="reviews-list">
          <div class="review-item" v-for="review in reviews" :key="review.id">
            <div class="review-header">
              <el-avatar :size="40" :src="review.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="review-meta">
                <span class="review-author">{{ review.username }}</span>
                <RatingStars v-if="review.userRating" :score="review.userRating" size="small" readonly />
              </div>
              <el-button
                v-if="isLoggedIn && review.userId === userStore.user?.id"
                type="danger"
                :icon="Delete"
                circle
                size="small"
                @click="deleteReview(review.id)"
              />
            </div>
            <h4 class="review-title" v-if="review.title">{{ review.title }}</h4>
            <p class="review-content">{{ review.content }}</p>
            <div class="review-footer">
              <span class="review-time">
                <el-icon><Clock /></el-icon>
                {{ new Date(review.createdAt).toLocaleDateString() }}
              </span>
              <el-tooltip content="登录后才可点赞" placement="top" :disabled="isLoggedIn">
                <span class="review-like-wrap">
                  <el-button
                    class="review-like-btn"
                    link
                    :type="review.liked ? 'primary' : undefined"
                    :loading="likingIds.has(review.id)"
                    :disabled="!isLoggedIn"
                    @click="toggleReviewLike(review)"
                  >
                    <el-icon><Pointer /></el-icon>
                    {{ review.likeCount || 0 }} 有用
                  </el-button>
                </span>
              </el-tooltip>
            </div>
          </div>

          <el-empty v-if="reviews.length === 0" description="还没有评论，来写第一条吧" />
        </div>
      </el-card>

      <!-- Review dialog -->
      <el-dialog v-model="showReviewModal" title="写短评" width="500px">
        <el-form>
          <el-form-item label="标题">
            <el-input v-model="reviewTitle" placeholder="标题（可选）" />
          </el-form-item>
          <el-form-item label="内容">
            <el-input
              v-model="reviewContent"
              type="textarea"
              placeholder="写下你的评论..."
              :rows="6"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showReviewModal = false">取消</el-button>
          <el-button type="primary" @click="submitReview" :loading="submitting">
            发布
          </el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { Delete } from '@element-plus/icons-vue'
export default {
  data() {
    return { Delete }
  }
}
</script>

<style scoped>
.movie-detail {
  padding: 24px 0;
}

.detail-skeleton {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 40px;
}

.detail-content {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 40px;
  animation: fade-up 0.5s ease-out;
}

@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
}

.poster-section {
  position: sticky;
  top: 88px;
  align-self: start;
}

.poster {
  width: 100%;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  overflow: hidden;
}

.poster :deep(.el-image__inner) {
  aspect-ratio: 2/3;
}

.image-error {
  width: 100%;
  aspect-ratio: 2/3;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #c0c4cc;
  font-size: 48px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.original-title {
  color: var(--text-muted);
  margin-bottom: 20px;
}

.rating-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.rating-box {
  display: flex;
  align-items: center;
  gap: 20px;
}

.rating-score {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score {
  font-size: 40px;
  font-weight: 700;
  color: var(--douban-orange);
  line-height: 1;
}

.label {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

.rating-count {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--text-muted);
  font-size: 14px;
}

.meta-list {
  margin-bottom: 20px;
}

.genre-tag {
  margin-right: 6px;
}

.user-actions-card {
  border-radius: 12px;
}

.my-rating {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-label {
  font-size: 14px;
  color: var(--text-muted);
  white-space: nowrap;
}

.collect-buttons {
  display: flex;
  gap: 12px;
}

.collect-buttons .el-button {
  flex: 1;
}

.summary-section,
.reviews-section {
  margin-top: 24px;
  border-radius: 12px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.summary {
  line-height: 1.8;
  color: var(--text-secondary);
}

.media-section + .media-section {
  margin-top: 18px;
}

.media-title {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.trailer {
  width: 100%;
  max-height: 420px;
  border-radius: 12px;
  background: #000;
}

.still-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 10px;
}

.still {
  width: 100%;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--separator);
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-meta {
  flex: 1;
}

.review-author {
  font-weight: 500;
  display: block;
  margin-bottom: 4px;
  color: var(--text-primary);
}

.review-title {
  font-size: 16px;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.review-content {
  color: var(--text-secondary);
  line-height: 1.6;
}

.review-footer {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  font-size: 12px;
  color: var(--text-muted);
}

.review-footer span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.review-like-btn {
  padding: 0;
  height: auto;
  font-size: 12px;
}

.review-like-wrap {
  display: inline-flex;
}

.review-like-btn :deep(.el-icon) {
  margin-right: 4px;
}

@media (max-width: 768px) {
  .detail-skeleton,
  .detail-content {
    grid-template-columns: 1fr;
  }

  .poster-section {
    position: static;
    max-width: 200px;
    margin: 0 auto;
  }

  .title {
    font-size: 24px;
    text-align: center;
  }

  .original-title {
    text-align: center;
  }
}
</style>
