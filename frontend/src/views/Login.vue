<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  captchaId: '',
  captchaCode: ''
})

const captchaImage = ref('')
const captchaLoading = ref(false)

const rules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  captchaCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

async function refreshCaptcha() {
  captchaLoading.value = true
  try {
    const res = await api.getCaptcha()
    form.captchaId = res.captchaId
    captchaImage.value = res.image
  } catch {
    captchaImage.value = ''
  } finally {
    captchaLoading.value = false
  }
}

async function handleLogin() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.login(form.username, form.password, form.captchaId, form.captchaCode)
      ElMessage.success('登录成功')
      const redirect = route.query.redirect
      if (redirect) {
        router.push(redirect)
      } else if (userStore.user?.role === 'ADMIN') {
        router.push('/admin/dashboard')
      } else {
        router.push('/')
      }
    } catch (e) {
      ElMessage.error(e.message)
      await refreshCaptcha()
      form.captchaCode = ''
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="always">
      <div class="auth-header">
        <el-icon :size="48" color="#00b386"><Film /></el-icon>
        <h1 class="auth-title">登录</h1>
        <p class="auth-subtitle">欢迎回来，继续探索好电影</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名 / 邮箱" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名或邮箱"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-form-item label="验证码" prop="captchaCode">
          <div class="captcha-row">
            <el-input
              v-model="form.captchaCode"
              placeholder="请输入验证码"
              size="large"
              autocomplete="off"
            />
            <div class="captcha-img" @click="refreshCaptcha" title="点击刷新验证码">
              <img v-if="captchaImage" :src="captchaImage" alt="captcha" />
              <div v-else class="captcha-fallback">刷新</div>
              <div v-if="captchaLoading" class="captcha-mask">...</div>
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            native-type="submit"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider>
        <span class="divider-text">还没有账号?</span>
      </el-divider>

      <div class="auth-footer">
        <el-button size="large" @click="router.push('/register')" class="register-btn">
          立即注册
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { User, Lock } from '@element-plus/icons-vue'
export default {
  data() {
    return { User, Lock }
  }
}
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 140px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

.auth-card {
  width: 100%;
  max-width: 420px;
  border-radius: 16px;
  animation: card-in 0.4s ease-out;
}

.auth-card :deep(.el-card__body) {
  padding: 40px;
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-title {
  font-size: 28px;
  font-weight: 700;
  margin: 16px 0 8px;
  color: var(--text-primary);
}

.auth-subtitle {
  color: var(--text-muted);
  font-size: 14px;
}

.submit-btn {
  width: 100%;
}

.captcha-row {
  display: grid;
  grid-template-columns: 1fr 120px;
  gap: 12px;
  width: 100%;
  align-items: center;
}

.captcha-img {
  position: relative;
  height: 44px;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid var(--separator);
  background: rgba(60, 60, 67, 0.06);
  cursor: pointer;
  user-select: none;
}

.captcha-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.captcha-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  font-size: 13px;
}

.captcha-mask {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  font-weight: 600;
}

.divider-text {
  color: var(--text-muted);
  font-size: 13px;
}

.auth-footer {
  text-align: center;
}

.register-btn {
  width: 100%;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}
</style>
