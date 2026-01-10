<script setup>
import { onMounted, ref } from 'vue'
import api from '@/api'
import { ElMessage } from 'element-plus'

const userOptions = ref([])

const form = ref({
  receiverId: null,
  title: '',
  content: '',
})

async function loadUsers() {
  try {
    const res = await api.adminListUsers(1, 200)
    userOptions.value = res.items || []
  } catch {
    userOptions.value = []
  }
}

const send = async () => {
  try {
    if (!form.value.receiverId) {
      ElMessage.warning('请填写接收用户ID')
      return
    }
    if (!form.value.title?.trim()) {
      ElMessage.warning('请填写标题')
      return
    }
    if (!form.value.content?.trim()) {
      ElMessage.warning('请填写内容')
      return
    }
    await api.adminSendMessage({
      receiverId: Number(form.value.receiverId),
      title: form.value.title,
      content: form.value.content,
    })
    ElMessage.success('已发送')
  } catch (e) {
    ElMessage.error(e.message || '发送失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<template>
  <div class="card">
    <h3>发送站内信</h3>
    <el-form label-width="90px">
      <el-form-item label="接收用户ID">
        <el-select
          v-model="form.receiverId"
          filterable
          placeholder="选择用户（或手动输入ID）"
          style="width: 100%"
          allow-create
          default-first-option
        >
          <el-option
            v-for="u in userOptions"
            :key="u.id"
            :label="`${u.username} (#${u.id})`"
            :value="u.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="标题">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" rows="4" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="send">发送</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.card {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
</style>
