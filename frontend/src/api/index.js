import axios from 'axios'

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
instance.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.error || '请求失败'
    return Promise.reject(new Error(message))
  }
)

const api = {
  // 认证
  login: (data) => instance.post('/auth/login', data),
  register: (data) => instance.post('/auth/register', data),
  getCurrentUser: () => instance.get('/users/me'),
  updateUser: (data) => instance.put('/users/me', data),

  // 通知
  listUnreadNotifications: () => instance.get('/notifications/unread'),
  listNotifications: (page = 1, size = 20) => instance.get('/notifications', { params: { page, size } }),
  markNotificationsRead: (ids) => instance.post('/notifications/read', ids),

  // 电影
  getMovies: (page = 1, size = 20) => instance.get('/movies', { params: { page, size } }),
  getMovie: (id) => instance.get(`/movies/${id}`),
  getTopMovies: (limit = 10) => instance.get('/movies/top', { params: { limit } }),
  getRecentMovies: (limit = 10) => instance.get('/movies/recent', { params: { limit } }),
  getHotMovies: (limit = 10) => instance.get('/movies/hot', { params: { limit } }),
  getMostReviewedMovies: (limit = 10) => instance.get('/movies/most-reviewed', { params: { limit } }),
  getMostWishedMovies: (limit = 10) => instance.get('/movies/most-wished', { params: { limit } }),
  getMostWatchedMovies: (limit = 10) => instance.get('/movies/most-watched', { params: { limit } }),
  searchMovies: (q, page = 1, size = 20) => instance.get('/movies/search', { params: { q, page, size } }),
  getMoviesByGenre: (genre, page = 1, size = 20) => instance.get(`/movies/genre/${genre}`, { params: { page, size } }),

  // 首页
  getRecommendations: () => instance.get('/home/recommendations'),

  // 评论排行
  getTopReviews: (page = 1, size = 20) => instance.get('/reviews/top', { params: { page, size } }),

  // 互动
  rateMovie: (id, score) => instance.post(`/movies/${id}/rating`, { score }),
  removeRating: (id) => instance.delete(`/movies/${id}/rating`),
  getUserRating: (id) => instance.get(`/movies/${id}/rating`),
  getReviews: (id, page = 1, size = 10) => instance.get(`/movies/${id}/reviews`, { params: { page, size } }),
  createReview: (id, data) => instance.post(`/movies/${id}/reviews`, data),
  deleteReview: (reviewId) => instance.delete(`/movies/reviews/${reviewId}`),
  likeReview: (reviewId) => instance.post(`/movies/reviews/${reviewId}/like`),
  collectMovie: (id, status) => instance.post(`/movies/${id}/collect`, { status }),
  removeCollection: (id) => instance.delete(`/movies/${id}/collect`),
  getMovieStatus: (id) => instance.get(`/movies/${id}/status`),

  // 收藏
  getCollections: (status = 'wish', page = 1, size = 20) =>
    instance.get('/collections', { params: { status, page, size } }),

  // 管理员 - 电影
  adminCreateMovie: (data) => instance.post('/admin/movies', data),
  adminUpdateMovie: (id, data) => instance.put(`/admin/movies/${id}`, data),
  adminDeleteMovie: (id) => instance.delete(`/admin/movies/${id}`),

  // 管理员 - 用户
  adminListUsers: (page = 1, size = 20) => instance.get('/admin/users', { params: { page, size } }),
  adminUpdateUserRole: (id, role) => instance.put(`/admin/users/${id}/role`, { role }),
  adminUpdateUserStatus: (id, status) => instance.put(`/admin/users/${id}/status`, { status }),

  // 管理员 - 评论
  adminDeleteReview: (id) => instance.delete(`/admin/reviews/${id}`),

  // 管理员 - 公告 / 站内信
  adminListAnnouncements: () => instance.get('/admin/announcements'),
  adminCreateAnnouncement: (data) => instance.post('/admin/announcements', data),
  adminToggleAnnouncement: (id, active) => instance.put(`/admin/announcements/${id}/active`, null, { params: { active } }),
  adminDeleteAnnouncement: (id) => instance.delete(`/admin/announcements/${id}`),
  adminSendMessage: (data) => instance.post('/admin/messages', data),
  adminBroadcastMessage: (data) => instance.post('/admin/messages/broadcast', data),
}

export default api
