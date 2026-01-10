<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const movies = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const activeTab = ref('basic')

const form = ref({
  title: '',
  originalTitle: '',
  year: null,
  directors: '',
  actors: '',
  genres: '',
  country: '',
  language: '',
  duration: null,
  poster: '',
  summary: '',
  images: '',
})

const media = ref({
  trailer: null, // { url, objectKey? }
  stills: [], // [{ url, objectKey? }]
})

const posterPreview = computed(() => form.value.poster)

function normalizeMediaItem(item) {
  if (!item) return null
  if (typeof item === 'string') return item.trim() ? { url: item.trim() } : null
  if (typeof item === 'object' && item.url) return { url: String(item.url).trim(), objectKey: item.objectKey || undefined }
  return null
}

function parseMedia(raw) {
  if (!raw) return { trailer: null, stills: [] }
  const str = String(raw).trim()
  if (!str) return { trailer: null, stills: [] }
  try {
    const parsed = JSON.parse(str)
    if (Array.isArray(parsed)) {
      return { trailer: null, stills: parsed.map(normalizeMediaItem).filter(Boolean) }
    }
    const trailer = normalizeMediaItem(parsed?.trailer)
    const stills = Array.isArray(parsed?.stills) ? parsed.stills.map(normalizeMediaItem).filter(Boolean) : []
    return { trailer, stills }
  } catch {
    return { trailer: null, stills: str.split(',').map(s => s.trim()).filter(Boolean).map((url) => ({ url })) }
  }
}

function serializeMedia(value) {
  const trailer = normalizeMediaItem(value?.trailer)
  const stills = Array.isArray(value?.stills) ? value.stills.map(normalizeMediaItem).filter(Boolean) : []
  if (!trailer && stills.length === 0) return ''
  return JSON.stringify({ trailer: trailer || null, stills })
}

async function fetchMovies() {
  loading.value = true
  try {
    const res = await api.getMovies(1, 200)
    movies.value = res.items || res
  } finally {
    loading.value = false
  }
}

onMounted(fetchMovies)

function resetForm() {
  form.value = {
    title: '',
    originalTitle: '',
    year: null,
    directors: '',
    actors: '',
    genres: '',
    country: '',
    language: '',
    duration: null,
    poster: '',
    summary: '',
    images: '',
  }
  media.value = { trailer: null, stills: [] }
  activeTab.value = 'basic'
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  form.value = {
    title: row.title || '',
    originalTitle: row.originalTitle || '',
    year: row.year || null,
    directors: row.directors || '',
    actors: row.actors || '',
    genres: row.genres || '',
    country: row.country || '',
    language: row.language || '',
    duration: row.duration || null,
    poster: row.poster || '',
    summary: row.summary || '',
    images: row.images || '',
  }
  media.value = parseMedia(row.images)
  activeTab.value = 'basic'
  dialogVisible.value = true
}

async function save() {
  saving.value = true
  try {
    const payload = { ...form.value, images: serializeMedia(media.value) }
    if (editingId.value) {
      await api.adminUpdateMovie(editingId.value, payload)
    } else {
      await api.adminCreateMovie(payload)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await fetchMovies()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除电影「${row.title}」吗？`, '提示', { type: 'warning' })
    await api.adminDeleteMovie(row.id)
    ElMessage.success('已删除')
    fetchMovies()
  } catch {}
}

async function uploadToMinio({ file, onSuccess, onError }, prefix) {
  try {
    const res = await api.adminUploadFile(file, prefix)
    onSuccess(res)
  } catch (e) {
    ElMessage.error(e.message || '上传失败')
    onError(e)
  }
}

function normalizeUploadRes(res) {
  const r = res && typeof res === 'object' ? res : null
  const candidate = r?.url ? r : r?.data
  if (!candidate?.url) return null
  return { url: candidate.url, objectKey: candidate.objectKey }
}

function handleTrailerUploadSuccess(res) {
  const obj = normalizeUploadRes(res)
  if (!obj) {
    ElMessage.error('上传返回异常')
    return
  }
  media.value.trailer = obj
}

function handleStillUploadSuccess(res) {
  const obj = normalizeUploadRes(res)
  if (!obj) {
    ElMessage.error('上传返回异常')
    return
  }
  media.value.stills.push(obj)
}

async function addStillByUrl() {
  try {
    const { value } = await ElMessageBox.prompt('请输入图片链接（URL）', '添加剧照', {
      confirmButtonText: '添加',
      cancelButtonText: '取消',
      inputPattern: /^https?:\/\//,
      inputErrorMessage: '请输入以 http(s):// 开头的链接',
    })
    media.value.stills.push({ url: value })
  } catch {}
}

async function removeStill(idx) {
  const item = media.value.stills[idx]
  if (!item) return
  try {
    if (item.objectKey) {
      await api.adminDeleteUpload(item.objectKey)
    }
  } catch (e) {
    ElMessage.error(e.message || '删除对象失败')
  } finally {
    media.value.stills.splice(idx, 1)
  }
}
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <div class="title">电影管理</div>
      <el-button type="primary" @click="openCreate">新增电影</el-button>
    </div>

    <el-table :data="movies" v-loading="loading" border class="table">
      <el-table-column label="电影" min-width="260">
        <template #default="{ row }">
          <div class="movie-cell">
            <el-image v-if="row.poster" :src="row.poster" fit="cover" class="poster" />
            <div class="movie-meta">
              <div class="name">{{ row.title }}</div>
              <div class="sub">{{ row.year || '-' }} · {{ row.genres || '-' }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="rating" label="评分" width="90" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑电影' : '新增电影'" width="860px">
      <el-tabs v-model="activeTab" class="dialog-tabs">
        <el-tab-pane label="基本信息" name="basic">
          <div class="form-grid">
            <el-form label-position="top" class="form">
              <div class="two-col">
                <el-form-item label="标题">
                  <el-input v-model="form.title" />
                </el-form-item>
                <el-form-item label="原名">
                  <el-input v-model="form.originalTitle" />
                </el-form-item>
              </div>
              <div class="two-col">
                <el-form-item label="年份">
                  <el-input v-model="form.year" />
                </el-form-item>
                <el-form-item label="时长（分钟）">
                  <el-input v-model="form.duration" />
                </el-form-item>
              </div>
              <el-form-item label="导演">
                <el-input v-model="form.directors" />
              </el-form-item>
              <el-form-item label="演员">
                <el-input v-model="form.actors" />
              </el-form-item>
              <div class="two-col">
                <el-form-item label="类型（逗号分隔）">
                  <el-input v-model="form.genres" />
                </el-form-item>
                <el-form-item label="地区 / 语言">
                  <el-input v-model="form.country" placeholder="地区" />
                  <div style="height: 8px" />
                  <el-input v-model="form.language" placeholder="语言" />
                </el-form-item>
              </div>
              <el-form-item label="简介">
                <el-input v-model="form.summary" type="textarea" :rows="5" />
              </el-form-item>
            </el-form>

            <div class="side">
              <div class="side-title">海报</div>
              <div class="poster-box">
                <el-image v-if="posterPreview" :src="posterPreview" fit="cover" class="poster-preview" />
                <div v-else class="poster-empty">暂无海报</div>
              </div>
              <el-input v-model="form.poster" placeholder="海报链接（URL）" />
              <div class="hint">也可以在「媒体」里上传到 MinIO 后自动填充</div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="媒体" name="media">
          <div class="media-grid">
            <el-card shadow="never" class="media-card">
              <template #header>
                <div class="media-head">
                  <span>预告片</span>
                  <el-upload
                    :show-file-list="false"
                    accept="video/*"
                    :http-request="(o) => uploadToMinio(o, `movies/${editingId || 'new'}/trailers`)"
                    @success="handleTrailerUploadSuccess"
                  >
                    <el-button>上传</el-button>
                  </el-upload>
                </div>
              </template>
              <el-input
                placeholder="粘贴视频链接（或上传）"
                :model-value="media.trailer?.url || ''"
                @update:model-value="(v) => (media.trailer = v ? { ...(media.trailer || {}), url: v } : null)"
              />
              <div class="hint">支持 mp4/webm 等直链；外链可在详情页打开</div>
            </el-card>

            <el-card shadow="never" class="media-card">
              <template #header>
                <div class="media-head">
                  <span>剧照</span>
                  <div class="media-actions">
                    <el-button @click="addStillByUrl">添加链接</el-button>
                    <el-upload
                      multiple
                      :show-file-list="false"
                      accept="image/*"
                      :http-request="(o) => uploadToMinio(o, `movies/${editingId || 'new'}/stills`)"
                      @success="handleStillUploadSuccess"
                    >
                      <el-button type="primary">上传</el-button>
                    </el-upload>
                  </div>
                </div>
              </template>

              <div class="stills-grid" v-if="media.stills.length > 0">
                <div v-for="(s, idx) in media.stills" :key="s.objectKey || s.url" class="still-item">
                  <el-image :src="s.url" fit="cover" class="still-img" />
                  <el-button class="still-del" circle size="small" type="danger" @click="removeStill(idx)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
              <el-empty v-else description="暂无剧照" :image-size="80" />
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.table :deep(.el-table__cell) {
  vertical-align: middle;
}

.movie-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.poster {
  width: 44px;
  height: 66px;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid var(--separator);
}

.movie-meta .name {
  font-weight: 600;
  color: var(--text-primary);
}

.movie-meta .sub {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-muted);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 260px;
  gap: 16px;
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.side {
  border-left: 1px solid var(--separator);
  padding-left: 16px;
}

.side-title {
  font-weight: 600;
  margin-bottom: 10px;
}

.poster-box {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--separator);
  background: rgba(60, 60, 67, 0.06);
  height: 340px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.poster-preview {
  width: 100%;
  height: 100%;
}

.poster-empty {
  color: var(--text-muted);
  font-size: 13px;
}

.hint {
  color: var(--text-muted);
  font-size: 12px;
  margin-top: 8px;
}

.media-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.media-card {
  border-radius: 16px;
}

.media-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.media-actions {
  display: inline-flex;
  gap: 8px;
}

.stills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 10px;
}

.still-item {
  position: relative;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid var(--separator);
  background: rgba(60, 60, 67, 0.06);
}

.still-img {
  width: 100%;
  height: 90px;
  display: block;
}

.still-del {
  position: absolute;
  top: 8px;
  right: 8px;
}

@media (max-width: 900px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  .side {
    border-left: none;
    padding-left: 0;
  }
}
</style>
