<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const formRef = ref()
const loading = ref(false)

const form = ref({
  username: '',
  bio: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, message: '用户名至少2个字符', trigger: 'blur' }
  ]
}

onMounted(() => {
  if (user.value) {
    form.value.username = user.value.username || ''
    form.value.bio = user.value.bio || ''
  }
})

async function updateProfile() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const updated = await api.updateUser({
        username: form.value.username,
        bio: form.value.bio
      })
      userStore.user = updated
      ElMessage.success('个人信息更新成功')
    } catch (e) {
      ElMessage.error(e.message)
    } finally {
      loading.value = false
    }
  })
}

async function logout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  } catch {
    // 取消操作
  }
}
</script>

<template>
  <div class="settings-page">
    <div class="container">
      <h1 class="page-title">
        <el-icon><Setting /></el-icon>
        账号设置
      </h1>

      <el-card class="settings-card" shadow="never">
        <template #header>
          <div class="card-header">
            <el-icon><User /></el-icon>
            个人信息
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
        >
          <el-form-item label="头像">
            <div class="avatar-section">
              <el-avatar :size="80" :src="user?.avatar">
                <el-icon :size="32"><User /></el-icon>
              </el-avatar>
              <span class="avatar-hint">暂不支持修改头像</span>
            </div>
          </el-form-item>

          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item label="邮箱">
            <el-input
              :model-value="user?.email"
              size="large"
              disabled
              :prefix-icon="Message"
            />
            <div class="field-hint">
              <el-icon><Lock /></el-icon>
              邮箱不可修改
            </div>
          </el-form-item>

          <el-form-item label="个人简介">
            <el-input
              v-model="form.bio"
              type="textarea"
              :rows="3"
              placeholder="介绍一下自己..."
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="updateProfile"
            >
              {{ loading ? '保存中...' : '保存修改' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="settings-card danger-zone" shadow="never">
        <template #header>
          <div class="card-header danger">
            <el-icon><Warning /></el-icon>
            账号操作
          </div>
        </template>

        <p class="danger-text">退出当前账号后，需要重新登录才能使用完整功能</p>
        <el-button type="danger" size="large" @click="logout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </el-card>
    </div>
  </div>
</template>

<script>
import { User, Message, Lock } from '@element-plus/icons-vue'
export default {
  data() {
    return { User, Message, Lock }
  }
}
</script>

<style scoped>
.settings-page {
  padding: 24px 0;
}

.settings-page > .container {
  max-width: 600px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title .el-icon {
  color: var(--douban-green);
}

.settings-card {
  margin-bottom: 24px;
  border-radius: 16px;
}

.settings-card:hover {
  transform: none;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.card-header.danger {
  color: #dc2626;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-hint {
  font-size: 13px;
  color: var(--text-muted);
}

.field-hint {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.danger-zone {
  border: 1px solid #fecaca;
}

.danger-text {
  color: var(--text-muted);
  margin-bottom: 16px;
  font-size: 14px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}
</style>
