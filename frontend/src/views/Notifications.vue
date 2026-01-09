<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useNotificationStore } from '@/stores/notification'
import api from '@/api'

const notificationStore = useNotificationStore()

const activeTab = ref('messages')

const loadingMessages = ref(false)
const page = ref(1)
const size = 20
const total = ref(0)
const items = ref([])

const unreadIds = computed(() => items.value.filter(m => m.status === 'UNREAD').map(m => m.id).filter(Boolean))

const loadingAnnouncements = ref(false)
const announcements = ref([])

async function loadMessages(reset = false) {
  loadingMessages.value = true
  try {
    if (reset) page.value = 1
    const res = await api.listNotifications(page.value, size)
    items.value = res.items || []
    total.value = res.total || 0
  } catch (e) {
    ElMessage.error(e.message || '加载站内信失败')
  } finally {
    loadingMessages.value = false
  }
}

async function loadAnnouncements() {
  loadingAnnouncements.value = true
  try {
    announcements.value = await api.listAnnouncements()
  } catch (e) {
    ElMessage.error(e.message || '加载公告失败')
  } finally {
    loadingAnnouncements.value = false
  }
}

async function markAllRead() {
  if (unreadIds.value.length === 0) return
  try {
    await api.markNotificationsRead(unreadIds.value)
    await Promise.all([notificationStore.loadUnread(), loadMessages(true)])
    ElMessage.success('已标记为已读')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function markOneRead(message) {
  if (!message?.id || message.status !== 'UNREAD') return
  try {
    await api.markNotificationsRead([message.id])
    await Promise.all([notificationStore.loadUnread(), loadMessages(false)])
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

function handlePageChange(p) {
  page.value = p
  loadMessages(false)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(async () => {
  await Promise.all([notificationStore.loadUnread(), loadMessages(true), loadAnnouncements()])
})
</script>

<template>
  <div class="notifications-page">
    <div class="container">
      <div class="header">
        <h1 class="page-title">
          <el-icon><Bell /></el-icon>
          消息中心
        </h1>
        <el-button type="primary" plain :disabled="unreadIds.length === 0" @click="markAllRead">
          全部标为已读
        </el-button>
      </div>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="站内信" name="messages">
          <div v-loading="loadingMessages">
            <el-empty v-if="!loadingMessages && items.length === 0" description="暂无站内信" :image-size="120" />

            <div class="msg-list" v-else>
              <el-card v-for="m in items" :key="m.id" class="msg-card" shadow="never" @click="markOneRead(m)">
                <div class="msg-title">
                  <el-tag v-if="m.status === 'UNREAD'" type="danger" size="small">未读</el-tag>
                  <span class="title">{{ m.title }}</span>
                  <span class="time">{{ new Date(m.createdAt).toLocaleString() }}</span>
                </div>
                <div class="content">{{ m.content }}</div>
              </el-card>
            </div>

            <div class="pagination" v-if="total > size && !loadingMessages">
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
        </el-tab-pane>

        <el-tab-pane label="公告" name="announcements">
          <div v-loading="loadingAnnouncements">
            <el-empty v-if="!loadingAnnouncements && announcements.length === 0" description="暂无公告" :image-size="120" />
            <div class="msg-list" v-else>
              <el-card v-for="a in announcements" :key="a.id" class="msg-card" shadow="never">
                <div class="msg-title">
                  <el-tag type="warning" size="small">公告</el-tag>
                  <span class="title">{{ a.title }}</span>
                  <span class="time">{{ new Date(a.createdAt).toLocaleString() }}</span>
                </div>
                <div class="content">{{ a.content }}</div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped>
.notifications-page {
  padding: 24px 0;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title .el-icon {
  color: var(--douban-green);
}

.msg-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.msg-card {
  border-radius: 12px;
  cursor: pointer;
}

.msg-card:hover {
  transform: none;
  box-shadow: none;
}

.msg-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.title {
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
}

.time {
  color: var(--text-muted);
  font-size: 12px;
}

.content {
  color: var(--text-secondary);
  line-height: 1.7;
  white-space: pre-wrap;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 18px;
}
</style>

