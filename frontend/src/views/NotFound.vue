<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="page">
    <el-empty description="页面不存在" :image-size="120">
      <el-button type="primary" @click="router.push('/')">返回首页</el-button>
    </el-empty>
  </div>
</template>

<style scoped>
.page {
  padding: 40px 0;
}
</style>

