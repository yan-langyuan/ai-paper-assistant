<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  level: {
    type: String,
    default: 'medium'
  },
  percentage: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['close', 'confirm'])

const confirmed = ref(false)

watch(() => props.visible, (val) => {
  if (val) confirmed.value = false
})

function handleClose() {
  emit('close')
}

function handleConfirm() {
  if (props.level === 'high' && !confirmed.value) return
  emit('confirm')
}
</script>

<template>
  <!-- Overlay -->
  <div
    v-if="visible"
    class="fixed inset-0 bg-black bg-opacity-40 z-50 flex items-center justify-center p-4"
    @click="handleClose"
  >
    <!-- Modal -->
    <div
      class="bg-white rounded-xl shadow-xl max-w-md w-full p-6"
      @click.stop
    >
      <!-- Icon -->
      <div class="flex justify-center mb-4">
        <div
          class="w-12 h-12 rounded-full flex items-center justify-center"
          :class="level === 'high' ? 'bg-red-100' : 'bg-yellow-100'"
        >
          <svg
            class="w-6 h-6"
            :class="level === 'high' ? 'text-red-500' : 'text-yellow-500'"
            fill="currentColor" viewBox="0 0 20 20"
          >
            <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
        </div>
      </div>

      <!-- Title -->
      <h3 class="text-lg font-semibold text-center mb-2" :class="level === 'high' ? 'text-red-600' : 'text-yellow-600'">
        {{ level === 'high' ? 'AI使用风险提示' : 'AI使用建议' }}
      </h3>

      <!-- Medium level content -->
      <div v-if="level === 'medium'" class="space-y-3">
        <p class="text-sm text-gray-600 text-center">
          建议您谨慎使用AI生成的内容，请务必：
        </p>
        <ul class="text-sm text-gray-600 space-y-2 list-disc list-inside">
          <li>对AI生成内容进行人工审核和修改</li>
          <li>避免直接复制粘贴AI生成的段落</li>
          <li>符合学校或期刊的学术规范要求</li>
        </ul>
        <div class="flex justify-center mt-4">
          <button @click="handleConfirm" class="btn-primary">
            知道了
          </button>
        </div>
      </div>

      <!-- High level content -->
      <div v-if="level === 'high'" class="space-y-3">
        <p class="text-sm text-gray-600">
          检测到您的文本与现有内容高度相似（{{ percentage }}%）。AI辅助降重可能影响原文的学术准确性，建议在导师指导下使用。
        </p>
        <label class="flex items-start space-x-3 cursor-pointer">
          <input
            type="checkbox"
            v-model="confirmed"
            class="mt-1 h-4 w-4 text-primary border-gray-300 rounded"
          />
          <span class="text-sm text-gray-600">我已知晓上述风险，并愿意自行承担使用AI辅助降重的责任</span>
        </label>
        <div class="flex space-x-3 mt-4">
          <button @click="handleClose" class="btn-secondary flex-1">
            取消
          </button>
          <button
            @click="handleConfirm"
            class="btn-primary flex-1"
            :disabled="!confirmed"
          >
            继续使用
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
