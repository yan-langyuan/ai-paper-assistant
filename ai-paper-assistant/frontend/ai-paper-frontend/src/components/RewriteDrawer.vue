<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  originalText: {
    type: String,
    default: ''
  },
  similarity: {
    type: Number,
    default: 0
  },
  schemes: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['close', 'apply'])

function closeDrawer() {
  emit('close')
}

function getSimilarityLevel(similarity) {
  if (similarity > 80) return 'high'
  if (similarity >= 50) return 'medium'
  return 'low'
}

const levelColors = {
  high: 'bg-red-100 text-red-700',
  medium: 'bg-yellow-100 text-yellow-700',
  low: 'bg-green-100 text-green-700'
}
</script>

<template>
  <!-- Overlay -->
  <div
    v-if="visible"
    class="fixed inset-0 bg-black bg-opacity-30 z-40 transition-opacity"
    @click="closeDrawer"
  ></div>

  <!-- Drawer -->
  <div
    class="fixed top-0 right-0 h-full bg-white shadow-xl z-50 transform transition-transform duration-300 overflow-y-auto"
    :class="visible ? 'translate-x-0' : 'translate-x-full'"
    style="width: 420px; max-width: 100vw;"
  >
    <!-- Header -->
    <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 z-10">
      <div class="flex items-center justify-between">
        <h3 class="text-lg font-semibold text-gray-900">改写建议</h3>
        <button @click="closeDrawer" class="text-gray-400 hover:text-gray-600">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>

    <div class="px-6 py-4 space-y-6">
      <!-- Original text -->
      <div>
        <h4 class="text-sm font-medium text-gray-700 mb-2">原文</h4>
        <div class="bg-gray-50 rounded-lg p-3 border border-gray-200">
          <p class="text-sm text-gray-700">{{ originalText }}</p>
        </div>
        <div class="mt-2">
          <span
            class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium"
            :class="levelColors[getSimilarityLevel(similarity)]"
          >
            相似度 {{ similarity }}%
          </span>
        </div>
      </div>

      <!-- Scheme Cards -->
      <div v-for="scheme in schemes" :key="scheme.type" class="border rounded-lg overflow-hidden">
        <div
          class="px-4 py-3 flex items-center justify-between"
          :class="{
            'bg-green-50 border-b border-green-100': scheme.type === 'A',
            'bg-blue-50 border-b border-blue-100': scheme.type === 'B',
            'bg-purple-50 border-b border-purple-100': scheme.type === 'C'
          }"
        >
          <div>
            <span
              class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium"
              :class="{
                'bg-green-100 text-green-700': scheme.type === 'A',
                'bg-blue-100 text-blue-700': scheme.type === 'B',
                'bg-purple-100 text-purple-700': scheme.type === 'C'
              }"
            >
              {{ scheme.label }}
            </span>
          </div>
          <div v-if="scheme.type === 'C' && scheme.warning" class="relative group">
            <svg class="w-4 h-4 text-yellow-500 cursor-help" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
            </svg>
            <div class="absolute right-0 top-6 w-64 bg-gray-900 text-white text-xs rounded-lg p-3 shadow-lg opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-10">
              {{ scheme.warning }}
            </div>
          </div>
        </div>
        <div class="p-4">
          <p class="text-sm text-gray-700 leading-relaxed">{{ scheme.text }}</p>

          <!-- Diff coloring indicators -->
          <div v-if="scheme.keptTerms && scheme.keptTerms.length" class="mt-3">
            <p class="text-xs text-gray-500 mb-1">保留词汇:</p>
            <div class="flex flex-wrap gap-1">
              <span
                v-for="term in scheme.keptTerms"
                :key="term"
                class="inline-flex items-center px-2 py-0.5 bg-green-100 text-green-700 rounded text-xs"
              >
                {{ term }}
              </span>
            </div>
          </div>
          <div v-if="scheme.changes && scheme.changes.length" class="mt-2">
            <p class="text-xs text-gray-500 mb-1">修改部分:</p>
            <div class="flex flex-wrap gap-1">
              <span
                v-for="change in scheme.changes"
                :key="change"
                class="inline-flex items-center px-2 py-0.5 bg-yellow-100 text-yellow-700 rounded text-xs"
              >
                {{ change }}
              </span>
            </div>
          </div>

          <!-- Apply button -->
          <div class="mt-3">
            <button
              @click="emit('apply', scheme)"
              class="w-full py-2 px-4 text-sm font-medium rounded-lg border transition-colors"
              :class="{
                'border-green-300 text-green-700 hover:bg-green-50': scheme.type === 'A',
                'border-blue-300 text-blue-700 hover:bg-blue-50': scheme.type === 'B',
                'border-purple-300 text-purple-700 hover:bg-purple-50': scheme.type === 'C'
              }"
            >
              应用此方案
            </button>
          </div>
        </div>
      </div>

      <!-- Disclaimer -->
      <div class="bg-gray-50 rounded-lg p-3 border border-gray-200">
        <p class="text-xs text-gray-500">所有方案均需您进一步编辑和核实</p>
      </div>
    </div>
  </div>
</template>
