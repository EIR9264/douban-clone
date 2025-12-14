<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api'
import { useUserStore } from '@/stores/user'
import RatingStars from '@/components/RatingStars.vue'
import Skeleton from '@/components/Skeleton.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const movie = ref(null)
const reviews = ref([])
const userRating = ref(0)
const userCollection = ref(null)
const loading = ref(true)
const showReviewModal = ref(false)
const reviewTitle = ref('')
const reviewContent = ref('')
const submitting = ref(false)
const imgError = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn)

const posterUrl = computed(() => {
  if (imgError.value || !movie.value?.poster) {
    return 'https://via.placeholder.com/280x420/f0f0f0/999?text=No+Image'
  }
  return movie.value.poster
})

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
    console.error('加载电影详情失败:', e)
  } finally {
    loading.value = false
  }
}

async function handleRate(score) {
  if (!isLoggedIn.value) return
  try {
    // 如果点击已选中的评分，则取消评分
    if (score === userRating.value) {
      await api.removeRating(movie.value.id)
      userRating.value = 0
    } else {
      await api.rateMovie(movie.value.id, score)
      userRating.value = score
    }
    const updated = await api.getMovie(movie.value.id)
    movie.value.rating = updated.rating
    movie.value.ratingCount = updated.ratingCount
  } catch (e) {
    alert(e.message)
  }
}

async function handleCollect(status) {
  if (!isLoggedIn.value) return
  try {
    if (userCollection.value === status) {
      await api.removeCollection(movie.value.id)
      userCollection.value = null
    } else {
      await api.collectMovie(movie.value.id, status)
      userCollection.value = status
    }
  } catch (e) {
    alert(e.message)
  }
}

async function submitReview() {
  if (!reviewContent.value.trim()) {
    alert('请输入评论内容')
    return
  }
  submitting.value = true
  try {
    const newReview = await api.createReview(movie.value.id, {
      title: reviewTitle.value,
      content: reviewContent.value
    })
    // 添加当前用户的评分到新评论
    newReview.userRating = userRating.value
    newReview.username = userStore.user?.username
    newReview.avatar = userStore.user?.avatar
    reviews.value.unshift(newReview)
    showReviewModal.value = false
    reviewTitle.value = ''
    reviewContent.value = ''
  } catch (e) {
    alert(e.message)
  } finally {
    submitting.value = false
  }
}

async function deleteReview(reviewId) {
  if (!confirm('确定要删除这条评论吗？')) return
  try {
    await api.deleteReview(reviewId)
    reviews.value = reviews.value.filter(r => r.id !== reviewId)
  } catch (e) {
    alert(e.message)
  }
}

function goToLogin() {
  router.push({ name: 'Login', query: { redirect: route.fullPath } })
}

function handleImgError() {
  imgError.value = true
}

onMounted(() => {
  fetchMovie()
})
</script>

<template>
  <div class="movie-detail">
    <div class="container">
      <div class="detail-skeleton" v-if="loading">
        <div class="skeleton-poster">
          <Skeleton height="400px" border-radius="16px" />
        </div>
        <div class="skeleton-info">
          <Skeleton width="60%" height="32px" />
          <Skeleton width="40%" height="20px" style="margin-top: 16px" />
          <Skeleton width="100%" height="100px" style="margin-top: 24px" />
        </div>
      </div>

      <div class="detail-content" v-else-if="movie">
        <div class="poster-section">
          <img
            :src="posterUrl"
            :alt="movie.title"
            class="poster"
            @error="handleImgError"
            referrerpolicy="no-referrer"
          />
        </div>

        <div class="info-section">
          <h1 class="title">{{ movie.title }}</h1>
          <p class="original-title" v-if="movie.originalTitle">{{ movie.originalTitle }}</p>

          <div class="rating-box">
            <div class="rating-score">
              <span class="score">{{ movie.rating ? Number(movie.rating).toFixed(1) : '暂无' }}</span>
              <span class="label">豆瓣评分</span>
            </div>
            <div class="rating-count">
              {{ movie.ratingCount || 0 }} 人评价
            </div>
          </div>

          <div class="meta-list">
            <div class="meta-item" v-if="movie.directors">
              <span class="meta-label">导演</span>
              <span class="meta-value">{{ movie.directors }}</span>
            </div>
            <div class="meta-item" v-if="movie.actors">
              <span class="meta-label">主演</span>
              <span class="meta-value">{{ movie.actors }}</span>
            </div>
            <div class="meta-item" v-if="movie.genres">
              <span class="meta-label">类型</span>
              <span class="meta-value">{{ movie.genres }}</span>
            </div>
            <div class="meta-item" v-if="movie.country">
              <span class="meta-label">地区</span>
              <span class="meta-value">{{ movie.country }}</span>
            </div>
            <div class="meta-item" v-if="movie.year">
              <span class="meta-label">年份</span>
              <span class="meta-value">{{ movie.year }}</span>
            </div>
            <div class="meta-item" v-if="movie.duration">
              <span class="meta-label">片长</span>
              <span class="meta-value">{{ movie.duration }} 分钟</span>
            </div>
          </div>

          <!-- 用户操作 - 根据登录状态显示不同内容 -->
          <div class="user-actions">
            <template v-if="isLoggedIn">
              <div class="my-rating">
                <span class="action-label">我的评分</span>
                <RatingStars
                  :score="userRating"
                  size="lg"
                  @update:score="handleRate"
                />
              </div>

              <div class="collect-buttons">
                <button
                  class="collect-btn"
                  :class="{ active: userCollection === 'wish' }"
                  @click="handleCollect('wish')"
                >
                  想看
                </button>
                <button
                  class="collect-btn"
                  :class="{ active: userCollection === 'watched' }"
                  @click="handleCollect('watched')"
                >
                  看过
                </button>
              </div>
            </template>
            <template v-else>
              <div class="login-prompt">
                <p>登录后可以评分、收藏和评论</p>
                <button class="btn btn-primary" @click="goToLogin">立即登录</button>
              </div>
            </template>
          </div>
        </div>
      </div>

      <div class="summary-section" v-if="movie">
        <h2 class="section-title">剧情简介</h2>
        <p class="summary">{{ movie.summary || '暂无简介' }}</p>
      </div>

      <div class="reviews-section" v-if="movie">
        <div class="section-header">
          <h2 class="section-title">短评</h2>
          <button class="btn btn-primary" @click="showReviewModal = true" v-if="isLoggedIn">
            写评论
          </button>
          <button class="btn btn-outline" @click="goToLogin" v-else>
            登录后评论
          </button>
        </div>

        <div class="reviews-list">
          <div class="review-item" v-for="review in reviews" :key="review.id">
            <div class="review-header">
              <img :src="review.avatar || 'https://img.icons8.com/fluency/96/user-male-circle.png'" alt="" class="review-avatar" />
              <div class="review-meta">
                <span class="review-author">{{ review.username }}</span>
                <RatingStars :score="review.userRating || 0" size="sm" readonly />
              </div>
              <button
                v-if="isLoggedIn && review.userId === userStore.user?.id"
                class="delete-review-btn"
                @click="deleteReview(review.id)"
                title="删除评论"
              >
                ×
              </button>
            </div>
            <h4 class="review-title" v-if="review.title">{{ review.title }}</h4>
            <p class="review-content">{{ review.content }}</p>
            <div class="review-footer">
              <span class="review-time">{{ new Date(review.createdAt).toLocaleDateString() }}</span>
              <span class="review-likes">{{ review.likes || 0 }} 有用</span>
            </div>
          </div>

          <div class="empty-reviews" v-if="reviews.length === 0">
            还没有评论，来写第一条吧
          </div>
        </div>
      </div>

      <div class="modal-overlay" v-if="showReviewModal" @click.self="showReviewModal = false">
        <div class="modal-content review-modal">
          <h3 class="modal-title">写短评</h3>
          <input
            v-model="reviewTitle"
            type="text"
            placeholder="标题（可选）"
            class="input"
          />
          <textarea
            v-model="reviewContent"
            placeholder="写下你的评论..."
            class="input review-textarea"
            rows="6"
          ></textarea>
          <div class="modal-actions">
            <button class="btn btn-secondary" @click="showReviewModal = false">取消</button>
            <button class="btn btn-primary" @click="submitReview" :disabled="submitting">
              {{ submitting ? '提交中...' : '发布' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.movie-detail {
  padding: 20px 0;
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
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
}

.title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.original-title {
  color: var(--text-muted);
  margin-bottom: 20px;
}

.rating-box {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  margin-bottom: 24px;
}

.rating-score {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score {
  font-size: 36px;
  font-weight: 700;
  color: var(--secondary);
}

.label {
  font-size: 12px;
  color: var(--text-muted);
}

.rating-count {
  color: var(--text-muted);
  font-size: 14px;
}

.meta-list {
  margin-bottom: 24px;
}

.meta-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.meta-label {
  color: var(--text-muted);
  min-width: 50px;
}

.meta-value {
  color: var(--text-primary);
}

.user-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.action-label {
  font-size: 14px;
  color: var(--text-muted);
  margin-right: 12px;
}

.my-rating {
  display: flex;
  align-items: center;
}

.collect-buttons {
  display: flex;
  gap: 12px;
}

.collect-btn {
  flex: 1;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: 14px;
  transition: all var(--transition-fast);
}

.collect-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.collect-btn.active {
  background: var(--primary);
  border-color: var(--primary);
  color: white;
}

.login-prompt {
  text-align: center;
  padding: 20px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
}

.login-prompt p {
  color: var(--text-muted);
  margin-bottom: 12px;
}

.summary-section,
.reviews-section {
  margin-top: 40px;
  padding-top: 40px;
  border-top: 1px solid var(--border-color);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.summary {
  line-height: 1.8;
  color: var(--text-secondary);
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
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

.delete-review-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  font-size: 18px;
  color: var(--text-muted);
  transition: all var(--transition-fast);
}

.delete-review-btn:hover {
  background: #fef2f2;
  color: #dc2626;
}

.review-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.review-author {
  font-weight: 500;
  display: block;
  margin-bottom: 4px;
}

.review-title {
  font-size: 16px;
  margin-bottom: 8px;
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

.empty-reviews {
  text-align: center;
  padding: 40px;
  color: var(--text-muted);
}

.review-modal {
  width: 500px;
  max-width: 90vw;
}

.modal-title {
  font-size: 18px;
  margin-bottom: 20px;
}

.review-textarea {
  margin-top: 12px;
  resize: none;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
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
  }
}
</style>
