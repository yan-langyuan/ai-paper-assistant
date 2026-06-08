<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '确认操作'
  },
  message: {
    type: String,
    default: '确定要执行此操作吗？'
  },
  confirmText: {
    type: String,
    default: '确认'
  },
  danger: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['confirm', 'cancel'])
</script>

<template>
  <div
    v-if="visible"
    class="fixed inset-0 bg-black bg-opacity-40 z-50 flex items-center justify-center p-4"
    @click="emit('cancel')"
  >
    <div
      class="bg-white rounded-xl shadow-xl max-w-sm w-full p-6"
      @click.stop
    >
      <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ title }}</h3>
      <p class="text-sm text-gray-600 mb-6">{{ message }}</p>
      <div class="flex justify-end space-x-3">
        <button @click="emit('cancel')" class="btn-secondary">
          取消
        </button>
        <button
          @click="emit('confirm')"
          :class="danger ? 'btn-danger' : 'btn-primary'"
        >
          {{ confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>
