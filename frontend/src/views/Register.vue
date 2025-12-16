<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度至少6位'))
  } else {
    if (form.confirmPassword !== '') {
      formRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, message: '用户名至少2个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

async function handleRegister() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.register(form.username, form.email, form.password)
      ElMessage.success('注册成功')
      router.push('/')
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
        <h1 class="auth-title">注册</h1>
        <p class="auth-subtitle">加入我们，发现更多好电影</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleRegister"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名（至少2个字符）"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱"
            size="large"
            :prefix-icon="Message"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            size="large"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
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
            @click="handleRegister"
            class="submit-btn"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider>
        <span class="divider-text">已有账号?</span>
      </el-divider>

      <div class="auth-footer">
        <el-button size="large" @click="router.push('/login')" class="login-btn">
          立即登录
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { User, Lock, Message } from '@element-plus/icons-vue'
export default {
  data() {
    return { User, Lock, Message }
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

.login-btn {
  width: 100%;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}
</style>
