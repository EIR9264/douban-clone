<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const username = ref('')
const password = ref('')
const loading = ref(false)
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const handleLogin = async () => {
  loading.value = true
  try {
    await userStore.login(username.value, password.value)
    if (userStore.user?.role !== 'ADMIN') {
      ElMessage.error('非管理员账户')
      return
    }
    const redirect = route.query.redirect || '/admin/dashboard'
    router.push(redirect)
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-wrap">
    <div class="card">
      <h2>管理员登录</h2>
      <el-input v-model="username" placeholder="用户名 / 邮箱" class="mb" />
      <el-input v-model="password" placeholder="密码" type="password" class="mb" />
      <el-button type="primary" :loading="loading" @click="handleLogin" block>登录</el-button>
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
</style>
