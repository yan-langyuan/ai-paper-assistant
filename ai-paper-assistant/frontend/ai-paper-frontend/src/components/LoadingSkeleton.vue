<script setup>
defineProps({
  type: {
    type: String,
    default: 'text'
  },
  count: {
    type: Number,
    default: 3
  }
})
</script>

<template>
  <div class="animate-pulse space-y-4">
    <!-- Card skeleton -->
    <template v-if="type === 'card'">
      <div
        v-for="i in count"
        :key="i"
        class="bg-white rounded-xl border border-gray-200 p-6 space-y-3"
      >
        <div class="h-5 bg-gray-200 rounded w-3/4"></div>
        <div class="h-3 bg-gray-100 rounded w-1/2"></div>
        <div class="h-3 bg-gray-100 rounded w-full"></div>
        <div class="h-3 bg-gray-100 rounded w-2/3"></div>
      </div>
    </template>

    <!-- List skeleton -->
    <template v-else-if="type === 'list'">
      <div
        v-for="i in count"
        :key="i"
        class="flex items-center space-x-4 p-4 bg-white rounded-lg border border-gray-200"
      >
        <div class="h-4 w-4 bg-gray-200 rounded"></div>
        <div class="flex-1 space-y-2">
          <div class="h-4 bg-gray-200 rounded w-3/5"></div>
          <div class="h-3 bg-gray-100 rounded w-2/5"></div>
        </div>
        <div class="h-6 w-16 bg-gray-100 rounded"></div>
      </div>
    </template>

    <!-- Text skeleton -->
    <template v-else>
      <div v-for="i in count" :key="i" class="space-y-2">
        <div class="h-4 bg-gray-200 rounded" :style="{ width: (60 + Math.random() * 30) + '%' }"></div>
        <div class="h-3 bg-gray-100 rounded w-full"></div>
        <div class="h-3 bg-gray-100 rounded w-5/6"></div>
        <div class="h-3 bg-gray-100 rounded w-4/6"></div>
      </div>
    </template>
  </div>
</template>
