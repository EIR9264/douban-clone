<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const username = ref('')
const bio = ref('')
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const message = ref('')
const messageType = ref('success')

onMounted(() => {
  if (user.value) {
    username.value = user.value.username || ''
    bio.value = user.value.bio || ''
  }
})

async function updateProfile() {
  loading.value = true
  message.value = ''
  try {
    const updated = await api.updateUser({
      username: username.value,
      bio: bio.value
    })
    userStore.user = updated
    message.value = '个人信息更新成功'
    messageType.value = 'success'
  } catch (e) {
    message.value = e.message
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}

function logout() {
  userStore.logout()
  router.push('/')
}
</script>

<template>
  <div class="settings-page">
    <div class="container">
      <h1 class="page-title">账号设置</h1>

      <div class="settings-card">
        <h2 class="card-title">个人信息</h2>

        <div class="form-group">
          <label class="form-label">头像</label>
          <div class="avatar-section">
            <img :src="user?.avatar" alt="" class="current-avatar" />
            <p class="avatar-hint">暂不支持修改头像</p>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="username"
            type="text"
            class="input"
            placeholder="请输入用户名"
          />
        </div>

        <div class="form-group">
          <label class="form-label">邮箱</label>
          <input
            :value="user?.email"
            type="email"
            class="input"
            disabled
          />
          <span class="field-hint">邮箱不可修改</span>
        </div>

        <div class="form-group">
          <label class="form-label">个人简介</label>
          <textarea
            v-model="bio"
            class="input"
            rows="3"
            placeholder="介绍一下自己..."
          ></textarea>
        </div>

        <div class="message" :class="messageType" v-if="message">
          {{ message }}
        </div>

        <button
          class="btn btn-primary"
          @click="updateProfile"
          :disabled="loading"
        >
          {{ loading ? '保存中...' : '保存修改' }}
        </button>
      </div>

      <div class="settings-card danger-zone">
        <h2 class="card-title">账号操作</h2>
        <p class="danger-text">退出当前账号</p>
        <button class="btn btn-danger" @click="logout">
          退出登录
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-page {
  padding: 20px 0;
  max-width: 600px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
}

.settings-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-sm);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.current-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-hint {
  font-size: 13px;
  color: var(--text-muted);
}

.field-hint {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

.input:disabled {
  background: var(--bg-tertiary);
  cursor: not-allowed;
}

textarea.input {
  resize: none;
}

.message {
  padding: 12px;
  border-radius: var(--radius-md);
  margin-bottom: 16px;
  font-size: 14px;
}

.message.success {
  background: #ecfdf5;
  color: #059669;
}

.message.error {
  background: #fef2f2;
  color: #dc2626;
}

.danger-zone {
  border: 1px solid #fecaca;
}

.danger-zone .card-title {
  color: #dc2626;
}

.danger-text {
  color: var(--text-muted);
  margin-bottom: 16px;
  font-size: 14px;
}

.btn-danger {
  background: #dc2626;
  color: white;
}

.btn-danger:hover {
  background: #b91c1c;
}
</style>
