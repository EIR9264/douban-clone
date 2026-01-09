<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)

const fetchUsers = async () => {
  loading.value = true
  const res = await api.adminListUsers(1, 100)
  users.value = res.items || res
  loading.value = false
}

onMounted(fetchUsers)

const updateRole = async (row, role) => {
  await api.adminUpdateUserRole(row.id, role)
  ElMessage.success('角色已更新')
  fetchUsers()
}

const updateStatus = async (row, status) => {
  await api.adminUpdateUserStatus(row.id, status)
  ElMessage.success('状态已更新')
  fetchUsers()
}
</script>

<template>
  <el-table :data="users" v-loading="loading" border>
    <el-table-column prop="username" label="用户名" />
    <el-table-column prop="email" label="邮箱" />
    <el-table-column prop="role" label="角色" width="100" />
    <el-table-column prop="status" label="状态" width="100" />
    <el-table-column label="操作" width="280">
      <template #default="{ row }">
        <el-button size="small" @click="updateRole(row, row.role === 'ADMIN' ? 'USER' : 'ADMIN')">
          切换角色
        </el-button>
        <el-button size="small" type="warning" @click="updateStatus(row, row.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE')">
          {{ row.status === 'ACTIVE' ? '封禁' : '解封' }}
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>
