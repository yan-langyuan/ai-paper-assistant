<script setup>
import { watch } from 'vue'

const props = defineProps({
  message: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'info'
  },
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close'])

watch(() => props.visible, (val) => {
  if (val) {
    setTimeout(() => {
      emit('close')
    }, 2000)
  }
})

const typeStyles = {
  info: 'bg-blue-500',
  success: 'bg-green-500',
  error: 'bg-red-500',
  warning: 'bg-yellow-500'
}
</script>

<template>
  <div
    v-if="visible"
    class="fixed top-4 right-4 z-50 transition-all duration-300"
  >
    <div
      class="px-4 py-3 rounded-lg shadow-lg text-white text-sm flex items-center space-x-2"
      :class="typeStyles[type] || typeStyles.info"
    >
      <span>{{ message }}</span>
      <button @click="emit('close')" class="ml-2 text-white opacity-70 hover:opacity-100">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  </div>
</template>
