<script setup>
defineProps({
  title: {
    type: String,
    default: '暂无数据'
  },
  description: {
    type: String,
    default: ''
  },
  actionLabel: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['action'])
</script>

<template>
  <div class="flex flex-col items-center justify-center py-16 px-4">
    <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
    </svg>
    <h3 class="text-lg font-medium text-gray-900 mb-1">{{ title }}</h3>
    <p v-if="description" class="text-sm text-gray-500 mb-4 text-center max-w-sm">{{ description }}</p>
    <button
      v-if="actionLabel"
      @click="emit('action')"
      class="btn-primary"
    >
      {{ actionLabel }}
    </button>
  </div>
</template>
