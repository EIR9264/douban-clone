<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  if (!username.value || !password.value) {
    error.value = '请填写用户名和密码'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await userStore.login(username.value, password.value)
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-title">登录</h1>
      <p class="auth-subtitle">欢迎回来，继续探索好电影</p>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label class="form-label">用户名 / 邮箱</label>
          <input
            v-model="username"
            type="text"
            class="input"
            placeholder="请输入用户名或邮箱"
            autocomplete="username"
          />
        </div>

        <div class="form-group">
          <label class="form-label">密码</label>
          <input
            v-model="password"
            type="password"
            class="input"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </div>

        <div class="error-message" v-if="error">{{ error }}</div>

        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <p class="auth-footer">
        还没有账号？
        <router-link to="/register" class="auth-link">立即注册</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: calc(100vh - 140px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.auth-card {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: var(--radius-lg);
  padding: 40px;
  box-shadow: var(--shadow-lg);
  animation: card-in 0.4s ease-out;
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
}

.auth-title {
  font-size: 28px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 8px;
}

.auth-subtitle {
  text-align: center;
  color: var(--text-muted);
  margin-bottom: 32px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
}

.error-message {
  padding: 12px;
  background: #fef2f2;
  color: #dc2626;
  border-radius: var(--radius-md);
  font-size: 14px;
  text-align: center;
}

.btn-block {
  width: 100%;
  padding: 14px;
  font-size: 16px;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-muted);
}

.auth-link {
  color: var(--primary);
  font-weight: 500;
}

.auth-link:hover {
  text-decoration: underline;
}
</style>
