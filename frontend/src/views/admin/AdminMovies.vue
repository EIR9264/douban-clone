<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { ElMessage } from 'element-plus'

const movies = ref([])
const loading = ref(false)
const formVisible = ref(false)
const editingId = ref(null)
const form = ref({})

const fetchMovies = async () => {
  loading.value = true
  const res = await api.getMovies(1, 100)
  movies.value = res.items || res
  loading.value = false
}

onMounted(fetchMovies)

const openCreate = () => {
  editingId.value = null
  form.value = {}
  formVisible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.value = { ...row }
  formVisible.value = true
}

const save = async () => {
  try {
    if (editingId.value) {
      await api.adminUpdateMovie(editingId.value, form.value)
    } else {
      await api.adminCreateMovie(form.value)
    }
    ElMessage.success('保存成功')
    formVisible.value = false
    fetchMovies()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const remove = async (row) => {
  await api.adminDeleteMovie(row.id)
  ElMessage.success('已删除')
  fetchMovies()
}
</script>

<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="openCreate">新增电影</el-button>
    </div>
    <el-table :data="movies" v-loading="loading" border>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="genres" label="类型" />
      <el-table-column prop="year" label="年份" width="80" />
      <el-table-column prop="rating" label="评分" width="80" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" :title="editingId ? '编辑电影' : '新增电影'" width="600">
      <el-form label-width="90px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="原名"><el-input v-model="form.originalTitle" /></el-form-item>
        <el-form-item label="年份"><el-input v-model="form.year" /></el-form-item>
        <el-form-item label="导演"><el-input v-model="form.directors" /></el-form-item>
        <el-form-item label="演员"><el-input v-model="form.actors" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="form.genres" /></el-form-item>
        <el-form-item label="国家"><el-input v-model="form.country" /></el-form-item>
        <el-form-item label="语言"><el-input v-model="form.language" /></el-form-item>
        <el-form-item label="时长"><el-input v-model="form.duration" /></el-form-item>
        <el-form-item label="海报"><el-input v-model="form.poster" /></el-form-item>
        <el-form-item label="图片"><el-input v-model="form.images" /></el-form-item>
        <el-form-item label="简介"><el-input type="textarea" v-model="form.summary" rows="4" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}
</style>
