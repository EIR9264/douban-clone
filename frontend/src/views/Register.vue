<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const isEmailValid = computed(() => !email.value || emailRegex.test(email.value))
const isFormValid = computed(() => {
  return username.value.length >= 2 &&
         emailRegex.test(email.value) &&
         password.value.length >= 6 &&
         password.value === confirmPassword.value
})

async function handleRegister() {
  if (!username.value || !email.value || !password.value) {
    error.value = '请填写所有必填项'
    return
  }

  if (!emailRegex.test(email.value)) {
    error.value = '请输入有效的邮箱地址'
    return
  }

  if (password.value !== confirmPassword.value) {
    error.value = '两次输入的密码不一致'
    return
  }

  if (password.value.length < 6) {
    error.value = '密码长度至少6位'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await userStore.register(username.value, email.value, password.value)
    router.push('/')
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
      <h1 class="auth-title">注册</h1>
      <p class="auth-subtitle">加入我们，发现更多好电影</p>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="username"
            type="text"
            class="input"
            :class="{ 'input-error': username && username.length < 2 }"
            placeholder="请输入用户名（至少2个字符）"
            autocomplete="username"
          />
          <span class="field-hint" v-if="username && username.length < 2">用户名至少2个字符</span>
        </div>

        <div class="form-group">
          <label class="form-label">邮箱</label>
          <input
            v-model="email"
            type="email"
            class="input"
            :class="{ 'input-error': email && !isEmailValid }"
            placeholder="请输入邮箱"
            autocomplete="email"
          />
          <span class="field-hint" v-if="email && !isEmailValid">请输入有效的邮箱地址</span>
        </div>

        <div class="form-group">
          <label class="form-label">密码</label>
          <input
            v-model="password"
            type="password"
            class="input"
            :class="{ 'input-error': password && password.length < 6 }"
            placeholder="请输入密码（至少6位）"
            autocomplete="new-password"
          />
          <span class="field-hint" v-if="password && password.length < 6">密码至少6位</span>
        </div>

        <div class="form-group">
          <label class="form-label">确认密码</label>
          <input
            v-model="confirmPassword"
            type="password"
            class="input"
            :class="{ 'input-error': confirmPassword && confirmPassword !== password }"
            placeholder="请再次输入密码"
            autocomplete="new-password"
          />
          <span class="field-hint" v-if="confirmPassword && confirmPassword !== password">两次密码不一致</span>
        </div>

        <div class="error-message" v-if="error">{{ error }}</div>

        <button
          type="submit"
          class="btn btn-primary btn-block"
          :disabled="loading || !isFormValid"
          :class="{ 'btn-disabled': !isFormValid }"
        >
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>

      <p class="auth-footer">
        已有账号？
        <router-link to="/login" class="auth-link">立即登录</router-link>
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

.input-error {
  border-color: #dc2626 !important;
  background: #fef2f2 !important;
}

.field-hint {
  font-size: 12px;
  color: #dc2626;
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

.btn-disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
