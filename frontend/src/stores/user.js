import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!token.value)

  async function login(username, password, captchaId, captchaCode) {
    const res = await api.login({ username, password, captchaId, captchaCode })
    token.value = res.token
    user.value = res.user
    localStorage.setItem('token', res.token)
    return res
  }

  async function register(username, email, password) {
    const res = await api.register({ username, email, password })
    token.value = res.token
    user.value = res.user
    localStorage.setItem('token', res.token)
    return res
  }

  async function fetchUser() {
    if (!token.value) return
    try {
      const res = await api.getCurrentUser()
      user.value = res
    } catch (e) {
      logout()
    }
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  // 初始化时获取用户信息
  if (token.value) {
    fetchUser()
  }

  return {
    user,
    token,
    isLoggedIn,
    login,
    register,
    fetchUser,
    logout
  }
})
