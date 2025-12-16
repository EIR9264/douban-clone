<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  score: {
    type: Number,
    default: 0
  },
  readonly: {
    type: Boolean,
    default: false
  },
  size: {
    type: String,
    default: 'default' // small, default, large
  },
  showText: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:score'])

// Element Plus el-rate 使用的是5星制，我们的评分是10分制
const rateValue = ref(props.score / 2)

watch(() => props.score, (newVal) => {
  rateValue.value = newVal / 2
})

const ratingTexts = ['很差', '较差', '还行', '推荐', '力荐']

function handleChange(val) {
  emit('update:score', val * 2)
}
</script>

<template>
  <div class="rating-stars-wrapper" :class="[`size-${size}`]">
    <el-rate
      v-model="rateValue"
      :disabled="readonly"
      :texts="ratingTexts"
      :show-text="showText && !readonly"
      :allow-half="readonly"
      @change="handleChange"
    />
    <span v-if="showText && readonly && score > 0" class="rating-text">
      {{ ratingTexts[Math.ceil(score / 2) - 1] }}
    </span>
  </div>
</template>

<style scoped>
.rating-stars-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.rating-stars-wrapper :deep(.el-rate) {
  height: auto;
}

.rating-stars-wrapper :deep(.el-rate__icon) {
  margin-right: 2px;
}

.rating-stars-wrapper.size-small :deep(.el-rate__icon),
.rating-stars-wrapper.size-sm :deep(.el-rate__icon) {
  font-size: 14px !important;
}

.rating-stars-wrapper.size-default :deep(.el-rate__icon),
.rating-stars-wrapper.size-md :deep(.el-rate__icon) {
  font-size: 20px !important;
}

.rating-stars-wrapper.size-large :deep(.el-rate__icon),
.rating-stars-wrapper.size-lg :deep(.el-rate__icon) {
  font-size: 28px !important;
}

.rating-text {
  font-size: 14px;
  color: var(--douban-orange);
  font-weight: 500;
}

.rating-stars-wrapper.size-small .rating-text,
.rating-stars-wrapper.size-sm .rating-text {
  font-size: 12px;
}

.rating-stars-wrapper.size-large .rating-text,
.rating-stars-wrapper.size-lg .rating-text {
  font-size: 16px;
}

/* 悬浮提示文字样式 */
.rating-stars-wrapper :deep(.el-rate__text) {
  color: var(--douban-orange);
  font-weight: 500;
}
</style>
