<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const currentUserId = computed(() => userStore.user?.id)

const users = ref([])
const loading = ref(false)
const errorText = ref('')

const editVisible = ref(false)
const saving = ref(false)
const editForm = ref({
  id: null,
  username: '',
  email: '',
  avatar: '',
  bio: '',
  role: 'USER',
  status: 'ACTIVE',
})

async function fetchUsers() {
  loading.value = true
  errorText.value = ''
  try {
    const res = await api.adminListUsers(1, 200)
    users.value = res.items || res
  } catch (e) {
    users.value = []
    errorText.value = e.message || '加载用户失败'
    ElMessage.error(errorText.value)
  } finally {
    loading.value = false
  }
}

function openEdit(row) {
  editForm.value = {
    id: row.id,
    username: row.username || '',
    email: row.email || '',
    avatar: row.avatar || '',
    bio: row.bio || '',
    role: row.role || 'USER',
    status: row.status || 'ACTIVE',
  }
  editVisible.value = true
}

async function saveEdit() {
  if (!editForm.value.id) return
  saving.value = true
  try {
    const updated = await api.adminUpdateUser(editForm.value.id, {
      username: editForm.value.username,
      email: editForm.value.email,
      avatar: editForm.value.avatar,
      bio: editForm.value.bio,
      role: editForm.value.role,
      status: editForm.value.status,
    })
    const idx = users.value.findIndex((u) => u.id === updated.id)
    if (idx >= 0) users.value[idx] = updated
    ElMessage.success('已保存')
    editVisible.value = false
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function normalizeUploadRes(res) {
  const r = res && typeof res === 'object' ? res : null
  const candidate = r?.url ? r : r?.data
  if (!candidate?.url) return null
  return { url: candidate.url, objectKey: candidate.objectKey }
}

async function uploadAvatarToMinio({ file, onSuccess, onError }) {
  try {
    const prefix = `avatars/${editForm.value.id || 'new'}`
    const res = await api.adminUploadFile(file, prefix)
    const obj = normalizeUploadRes(res)
    if (!obj) {
      ElMessage.error('上传返回异常')
      onError?.(new Error('上传返回异常'))
      return
    }
    editForm.value.avatar = obj.url
    ElMessage.success('已上传')
    onSuccess?.(res)
  } catch (e) {
    ElMessage.error(e.message || '上传失败')
    onError?.(e)
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="page">
    <el-empty v-if="!loading && errorText" :description="errorText" :image-size="120" />
    <el-table v-else :data="users" v-loading="loading" border>
      <el-table-column label="用户" min-width="220">
        <template #default="{ row }">
          <div class="user-cell">
            <el-avatar :size="34" :src="row.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="user-meta">
              <div class="username">
                {{ row.username }}
                <el-tag v-if="row.id === currentUserId" size="small" effect="plain">你</el-tag>
              </div>
              <div class="email">{{ row.email }}</div>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="role" label="角色" width="120" />
      <el-table-column prop="status" label="状态" width="120" />

      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="editVisible" title="编辑用户" width="560px">
      <el-form label-position="top">
        <el-form-item label="头像（URL）">
          <div class="avatar-edit">
            <el-avatar :size="44" :src="editForm.avatar" class="avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <el-input v-model="editForm.avatar" placeholder="https://..." size="large" />
            <el-upload :show-file-list="false" accept="image/*" :http-request="uploadAvatarToMinio">
              <el-button size="large">本地上传</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="editForm.bio" type="textarea" :rows="3" />
        </el-form-item>

        <div class="two-col">
          <el-form-item label="角色">
            <el-select v-model="editForm.role" style="width: 100%">
              <el-option label="USER" value="USER" />
              <el-option label="ADMIN" value="ADMIN" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="editForm.status" style="width: 100%">
              <el-option label="ACTIVE" value="ACTIVE" />
              <el-option label="DISABLED" value="DISABLED" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  width: 100%;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.user-meta {
  min-width: 0;
}

.username {
  font-weight: 600;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.email {
  color: var(--text-muted);
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 360px;
}

.avatar-edit {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  flex: 0 0 var(--el-avatar-size);
  width: var(--el-avatar-size);
  height: var(--el-avatar-size);
  border-radius: 50%;
  border: 1px solid var(--separator);
  background: rgba(60, 60, 67, 0.06);
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
</style>
