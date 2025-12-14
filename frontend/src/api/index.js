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

  // 电影
  getMovies: (page = 1, size = 20) => instance.get('/movies', { params: { page, size } }),
  getMovie: (id) => instance.get(`/movies/${id}`),
  getTopMovies: (limit = 10) => instance.get('/movies/top', { params: { limit } }),
  getRecentMovies: (limit = 10) => instance.get('/movies/recent', { params: { limit } }),
  searchMovies: (q, page = 1, size = 20) => instance.get('/movies/search', { params: { q, page, size } }),
  getMoviesByGenre: (genre, page = 1, size = 20) => instance.get(`/movies/genre/${genre}`, { params: { page, size } }),

  // 首页
  getRecommendations: () => instance.get('/home/recommendations'),

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
}

export default api
