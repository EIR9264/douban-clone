<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const loading = ref(false)
const page = ref(1)
const size = 20
const total = ref(0)
const reviews = ref([])

async function fetchReviews() {
  loading.value = true
  try {
    const res = await api.adminListReviews(page.value, size)
    reviews.value = res.items || []
    total.value = res.total || 0
  } catch (e) {
    ElMessage.error(e.message || '加载评论失败')
  } finally {
    loading.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm('确定删除该评论吗？', '提示', { type: 'warning' })
  try {
    await api.adminDeleteReview(row.id)
    ElMessage.success('已删除')
    fetchReviews()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

function handlePageChange(p) {
  page.value = p
  fetchReviews()
}

onMounted(fetchReviews)
</script>

<template>
  <div>
    <el-table :data="reviews" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="movieTitle" label="电影" min-width="180">
        <template #default="{ row }">
          <div>{{ row.movieTitle || `电影 #${row.movieId}` }}</div>
          <div style="color: #909399; font-size: 12px">movieId: {{ row.movieId }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="作者" width="160">
        <template #default="{ row }">
          <div>{{ row.username || `用户 #${row.userId}` }}</div>
          <div style="color: #909399; font-size: 12px">userId: {{ row.userId }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="160" />
      <el-table-column prop="likeCount" label="点赞" width="90" />
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
