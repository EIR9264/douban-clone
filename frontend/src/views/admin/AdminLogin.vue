<script setup>
import { onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import api from '@/api'

const username = ref('')
const password = ref('')
const captchaId = ref('')
const captchaCode = ref('')
const captchaImage = ref('')
const captchaLoading = ref(false)
const loading = ref(false)
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const refreshCaptcha = async () => {
  captchaLoading.value = true
  try {
    const res = await api.getCaptcha()
    captchaId.value = res.captchaId
    captchaImage.value = res.image
  } catch {
    captchaImage.value = ''
  } finally {
    captchaLoading.value = false
  }
}

const handleLogin = async () => {
  loading.value = true
  try {
    await userStore.login(username.value, password.value, captchaId.value, captchaCode.value)
    if (userStore.user?.role !== 'ADMIN') {
      ElMessage.error('非管理员账户')
      userStore.logout()
      return
    }
    const redirect = route.query.redirect || '/admin/dashboard'
    router.push(redirect)
  } catch (e) {
    ElMessage.error(e.message)
    await refreshCaptcha()
    captchaCode.value = ''
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<template>
  <div class="login-wrap">
    <div class="card">
      <h2>管理员登录</h2>
      <el-form @submit.prevent="handleLogin">
        <el-input v-model="username" placeholder="用户名 / 邮箱" class="mb" />
        <el-input
          v-model="password"
          placeholder="密码"
          type="password"
          class="mb"
          show-password
        />
        <div class="captcha-row mb">
          <el-input v-model="captchaCode" placeholder="验证码" autocomplete="off" />
          <div class="captcha-img" @click="refreshCaptcha" title="点击刷新验证码">
            <img v-if="captchaImage" :src="captchaImage" alt="captcha" />
            <div v-else class="captcha-fallback">刷新</div>
            <div v-if="captchaLoading" class="captcha-mask">...</div>
          </div>
        </div>
        <el-button type="primary" :loading="loading" native-type="submit" block>登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}
.card {
  width: 320px;
  padding: 24px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}
.mb {
  margin-bottom: 12px;
}

.captcha-row {
  display: grid;
  grid-template-columns: 1fr 120px;
  gap: 12px;
  align-items: center;
}

.captcha-img {
  position: relative;
  height: 32px;
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
</style>
