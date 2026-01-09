<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const form = ref({ title: '', content: '' })
const loading = ref(false)

const fetchList = async () => {
  loading.value = true
  list.value = await api.adminListAnnouncements()
  loading.value = false
}

onMounted(fetchList)

const create = async () => {
  await api.adminCreateAnnouncement(form.value)
  ElMessage.success('已发布')
  form.value = { title: '', content: '' }
  fetchList()
}

const toggle = async (row) => {
  await api.adminToggleAnnouncement(row.id, !row.active)
  ElMessage.success('状态已更新')
  fetchList()
}

const remove = async (row) => {
  await api.adminDeleteAnnouncement(row.id)
  ElMessage.success('已删除')
  fetchList()
}
</script>

<template>
  <div class="grid">
    <div class="card">
      <h3>发布公告</h3>
      <el-form label-width="90px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" rows="4" v-model="form.content" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="create">发布</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="card">
      <h3>公告列表</h3>
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="active" label="状态" width="100">
          <template #default="{ row }">{{ row.active ? '启用' : '停用' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="toggle(row)">{{ row.active ? '停用' : '启用' }}</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.grid {
  display: grid;
  gap: 16px;
}
.card {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
</style>
