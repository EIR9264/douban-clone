<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const loading = ref(false)
const page = ref(1)
const size = 20
const total = ref(0)
const ratings = ref([])

async function fetchRatings() {
  loading.value = true
  try {
    const res = await api.adminListRatings(page.value, size)
    ratings.value = res.items || []
    total.value = res.total || 0
  } catch (e) {
    ElMessage.error(e.message || '加载评分失败')
  } finally {
    loading.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm('确定删除该评分吗？', '提示', { type: 'warning' })
  try {
    await api.adminDeleteRating(row.id)
    ElMessage.success('已删除')
    fetchRatings()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

function handlePageChange(p) {
  page.value = p
  fetchRatings()
}

onMounted(fetchRatings)
</script>

<template>
  <div>
    <el-table :data="ratings" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="movieTitle" label="电影" min-width="180">
        <template #default="{ row }">
          <div>{{ row.movieTitle || `电影 #${row.movieId}` }}</div>
          <div style="color: #909399; font-size: 12px">movieId: {{ row.movieId }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户" width="160">
        <template #default="{ row }">
          <div>{{ row.username || `用户 #${row.userId}` }}</div>
          <div style="color: #909399; font-size: 12px">userId: {{ row.userId }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="score" label="评分" width="110" />
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="{ row }">
          {{ row.createdAt ? new Date(row.createdAt).toLocaleString() : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: center; margin-top: 16px" v-if="total > size">
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev, pager, next"
        background
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>
