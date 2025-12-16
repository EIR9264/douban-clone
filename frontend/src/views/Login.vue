<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.login(form.username, form.password)
      ElMessage.success('登录成功')
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } catch (e) {
      ElMessage.error(e.message)
    } finally {
      loading.value = false
    }
  })
}
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

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="submit-btn"
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
