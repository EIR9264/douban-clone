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
    const message = error.response?.data?.error || error.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

const api = {
  // 认证
  login: (data) => instance.post('/auth/login', data),
  register: (data) => instance.post('/auth/register', data),
  getCaptcha: () => instance.get('/auth/captcha'),
  getCurrentUser: () => instance.get('/users/me'),
  updateUser: (data) => instance.put('/users/me', data),
  changePassword: (data) => instance.put('/users/me/password', data),
  uploadMyAvatar: (file) => {
    const fd = new FormData()
    fd.append('file', file)
    return instance.post('/users/me/avatar', fd, { timeout: 120000 })
  },

  // 通知
  listUnreadNotifications: () => instance.get('/notifications/unread'),
  listNotifications: (page = 1, size = 20) => instance.get('/notifications', { params: { page, size } }),
  markNotificationsRead: (ids) => instance.post('/notifications/read', ids),
  listAnnouncements: () => instance.get('/announcements'),

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
  getMoviesByGenres: (genres = [], page = 1, size = 20) =>
    instance.get('/movies/genres', { params: { genres: Array.isArray(genres) ? genres.join(',') : genres, page, size } }),

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
  adminUploadFile: (file, prefix = '') => {
    const fd = new FormData()
    fd.append('file', file)
    return instance.post('/admin/uploads', fd, {
      params: { prefix },
      // 上传时让浏览器/axios自动设置 multipart boundary
      timeout: 120000,
    })
  },
  adminDeleteUpload: (objectKey) => instance.delete('/admin/uploads', { params: { objectKey } }),

  // 管理员 - 用户
  adminListUsers: (page = 1, size = 20) => instance.get('/admin/users', { params: { page, size } }),
  adminUpdateUserRole: (id, role) => instance.put(`/admin/users/${id}/role`, { role }),
  adminUpdateUserStatus: (id, status) => instance.put(`/admin/users/${id}/status`, { status }),
  adminUpdateUser: (id, data) => instance.put(`/admin/users/${id}`, data),

  // 管理员 - 评论
  adminListReviews: (page = 1, size = 20) => instance.get('/admin/reviews', { params: { page, size } }),
  adminDeleteReview: (id) => instance.delete(`/admin/reviews/${id}`),

  // 管理员 - 评分
  adminListRatings: (page = 1, size = 20) => instance.get('/admin/ratings', { params: { page, size } }),
  adminUpdateRatingScore: (id, score) => instance.put(`/admin/ratings/${id}`, { score }),
  adminDeleteRating: (id) => instance.delete(`/admin/ratings/${id}`),

  // 管理员 - 公告 / 站内信
  adminListAnnouncements: () => instance.get('/admin/announcements'),
  adminCreateAnnouncement: (data) => instance.post('/admin/announcements', data),
  adminToggleAnnouncement: (id, active) => instance.put(`/admin/announcements/${id}/active`, null, { params: { active } }),
  adminDeleteAnnouncement: (id) => instance.delete(`/admin/announcements/${id}`),
  adminSendMessage: (data) => instance.post('/admin/messages', data),
  adminBroadcastMessage: (data) => instance.post('/admin/messages/broadcast', data),
}

export default api
