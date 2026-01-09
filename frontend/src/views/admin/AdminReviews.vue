<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const reviews = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  // 复用电影单条评论查询，这里取前100条演示
  const res = await api.getMovies(1, 1)
  // 无直接接口列表，这里仅演示空表
  reviews.value = []
  loading.value = false
}

onMounted(fetchData)

const remove = (row) => {
  ElMessageBox.confirm('确定删除该评论吗？', '提示').then(async () => {
    await api.adminDeleteReview(row.id)
    ElMessage.success('已删除')
    fetchData()
  })
}
</script>

<template>
  <el-empty description="请在后端提供评论列表接口以加载数据">
    <div>目前仅支持删除指定评论ID</div>
  </el-empty>
</template>
