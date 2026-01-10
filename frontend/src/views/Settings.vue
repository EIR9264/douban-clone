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

const pwdFormRef = ref()
const pwdLoading = ref(false)
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const form = ref({
  username: '',
  bio: '',
  avatar: ''
})

async function uploadAvatar({ file, onSuccess, onError }) {
  try {
    const res = await api.uploadMyAvatar(file)
    const updated = res.user || res
    const url = res.url || updated?.avatar
    if (url) form.value.avatar = url
    if (updated?.id) userStore.user = updated
    ElMessage.success('头像已更新')
    onSuccess?.(res)
  } catch (e) {
    ElMessage.error(e.message || '上传失败')
    onError?.(e)
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, message: '用户名至少2个字符', trigger: 'blur' }
  ]
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.value.newPassword) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

onMounted(() => {
  if (user.value) {
    form.value.username = user.value.username || ''
    form.value.bio = user.value.bio || ''
    form.value.avatar = user.value.avatar || ''
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
        bio: form.value.bio,
        avatar: form.value.avatar
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

async function changePassword() {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    pwdLoading.value = true
    try {
      await api.changePassword({
        oldPassword: pwdForm.value.oldPassword,
        newPassword: pwdForm.value.newPassword
      })
      ElMessage.success('密码已修改，请重新登录')
      userStore.logout()
      router.push('/login')
    } catch (e) {
      ElMessage.error(e.message)
    } finally {
      pwdLoading.value = false
      pwdForm.value.oldPassword = ''
      pwdForm.value.newPassword = ''
      pwdForm.value.confirmPassword = ''
    }
  })
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
              <el-avatar :size="80" :src="form.avatar || user?.avatar">
                <el-icon :size="32"><User /></el-icon>
              </el-avatar>
              <div class="avatar-input">
                <el-input
                  v-model="form.avatar"
                  placeholder="请输入头像图片链接（URL）"
                  size="large"
                />
                <div class="avatar-actions">
                  <el-upload
                    :show-file-list="false"
                    accept="image/*"
                    :http-request="uploadAvatar"
                  >
                    <el-button size="large">本地上传</el-button>
                  </el-upload>
                </div>
              </div>
            </div>
            <div class="avatar-hint">支持粘贴图片 URL 或本地上传（上传会自动保存）</div>
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

      <el-card class="settings-card" shadow="never">
        <template #header>
          <div class="card-header">
            <el-icon><Lock /></el-icon>
            修改密码
          </div>
        </template>

        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-position="top">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password size="large" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password size="large" />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password size="large" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" :loading="pwdLoading" @click="changePassword">
              {{ pwdLoading ? '提交中...' : '确认修改' }}
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

.avatar-input {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.avatar-actions {
  display: flex;
  justify-content: flex-end;
}

.avatar-hint {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 8px;
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
