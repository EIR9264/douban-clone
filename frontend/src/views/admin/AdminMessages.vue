<script setup>
import { ref } from 'vue'
import api from '@/api'
import { ElMessage } from 'element-plus'

const form = ref({
  receiverId: null,
  title: '',
  content: '',
})

const send = async () => {
  await api.adminSendMessage(form.value)
  ElMessage.success('已发送')
}

const broadcast = async () => {
  await api.adminBroadcastMessage(form.value)
  ElMessage.success('已广播')
}
</script>

<template>
  <div class="card">
    <h3>发送站内信</h3>
    <el-form label-width="90px">
      <el-form-item label="接收用户ID">
        <el-input v-model="form.receiverId" placeholder="用户ID" />
      </el-form-item>
      <el-form-item label="标题">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" rows="4" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="send">发送</el-button>
        <el-button type="success" @click="broadcast">广播</el-button>
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
