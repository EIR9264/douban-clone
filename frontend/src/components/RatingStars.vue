<script setup>
import { ref, computed } from 'vue'

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
    default: 'md' // sm, md, lg
  }
})

const emit = defineEmits(['update:score'])

const hoverIndex = ref(0)

const stars = computed(() => {
  // 如果有悬浮状态，显示悬浮预览；否则显示实际评分
  const displayScore = !props.readonly && hoverIndex.value > 0
    ? hoverIndex.value * 2
    : props.score

  const fullStars = Math.floor(displayScore / 2)
  const hasHalf = displayScore % 2 >= 1
  const result = []
  for (let i = 1; i <= 5; i++) {
    if (i <= fullStars) {
      result.push('full')
    } else if (i === fullStars + 1 && hasHalf) {
      result.push('half')
    } else {
      result.push('empty')
    }
  }
  return result
})

// 评分文字提示
const ratingLabels = ['', '很差', '较差', '还行', '推荐', '力荐']
const hoverLabel = computed(() => {
  if (props.readonly || hoverIndex.value === 0) return ''
  return ratingLabels[hoverIndex.value]
})

function handleClick(index) {
  if (props.readonly) return
  emit('update:score', index * 2)
}

function handleMouseEnter(index) {
  if (props.readonly) return
  hoverIndex.value = index
}

function handleMouseLeave() {
  if (props.readonly) return
  hoverIndex.value = 0
}
</script>

<template>
  <div
    class="rating-stars-wrapper"
    :class="{ readonly }"
    @mouseleave="handleMouseLeave"
  >
    <div class="rating-stars" :class="[`size-${size}`]">
      <span
        v-for="(star, index) in stars"
        :key="index"
        class="star"
        :class="[star, { 'is-hovering': !readonly && hoverIndex > 0 }]"
        @click="handleClick(index + 1)"
        @mouseenter="handleMouseEnter(index + 1)"
      >
        <svg viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
        </svg>
      </span>
    </div>
    <transition name="fade">
      <span v-if="hoverLabel" class="rating-label" :class="`size-${size}`">
        {{ hoverLabel }}
      </span>
    </transition>
  </div>
</template>

<style scoped>
.rating-stars-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.rating-stars {
  display: inline-flex;
  gap: 2px;
}

.star {
  cursor: pointer;
  transition: transform 0.15s cubic-bezier(0.4, 0, 0.2, 1),
              color 0.15s ease;
  color: #ddd;
}

.star.full {
  color: var(--secondary);
}

.star.half {
  color: var(--secondary);
  opacity: 0.6;
}

.star.is-hovering.full {
  animation: star-pop 0.2s ease-out;
}

@keyframes star-pop {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
  100% {
    transform: scale(1.1);
  }
}

.rating-stars-wrapper:not(.readonly) .star:hover {
  transform: scale(1.2);
}

.readonly .star {
  cursor: default;
}

.star svg {
  display: block;
}

.size-sm .star svg {
  width: 14px;
  height: 14px;
}

.size-md .star svg {
  width: 20px;
  height: 20px;
}

.size-lg .star svg {
  width: 28px;
  height: 28px;
}

.rating-label {
  font-weight: 500;
  color: var(--secondary);
  white-space: nowrap;
}

.rating-label.size-sm {
  font-size: 12px;
}

.rating-label.size-md {
  font-size: 14px;
}

.rating-label.size-lg {
  font-size: 16px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateX(-4px);
}
</style>
