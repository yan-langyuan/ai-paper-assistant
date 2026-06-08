<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useLiteratureStore } from '@/stores/literature'
import { format, getStandards } from '@/api/reference'
import Toast from '@/components/Toast.vue'
import LoadingSkeleton from '@/components/LoadingSkeleton.vue'

const route = useRoute()
const store = useLiteratureStore()

const standards = ref([])
const selectedStandard = ref('')
const sortBy = ref('citation')
const loading = ref(false)
const previewResult = ref(null)
const error = ref('')
const toastMessage = ref('')
const toastType = ref('info')
const toastVisible = ref(false)

const sortOptions = [
  { value: 'pinyin', label: '拼音顺序' },
  { value: 'alphabetical', label: '字母顺序' },
  { value: 'citation', label: '引用顺序' }
]

// Parse IDs from query on mount
const initialIds = ref([])

function showToast(message, type = 'info') {
  toastMessage.value = message
  toastType.value = type
  toastVisible.value = true
}

async function loadStandards() {
  try {
    const res = await getStandards()
    standards.value = res.data || []
    if (standards.value.length > 0) {
      selectedStandard.value = standards.value[0]
    }
  } catch {
    standards.value = ['GB/T 7714', 'APA 7th', 'MLA 9th']
    selectedStandard.value = standards.value[0]
  }
}

async function generatePreview() {
  if (initialIds.value.length === 0 && store.selectedIds.length === 0) {
    showToast('请先选择文献', 'warning')
    return
  }

  if (!selectedStandard.value) {
    showToast('请选择格式标准', 'warning')
    return
  }

  loading.value = true
  error.value = ''
  previewResult.value = null

  try {
    const ids = initialIds.value.length > 0 ? initialIds.value : store.selectedIds
    const res = await format({
      literatureIds: ids,
      standard: selectedStandard.value,
      sortBy: sortBy.value
    })
    previewResult.value = res.data
  } catch (e) {
    error.value = e.response?.data?.message || '生成预览失败'
    showToast('生成失败', 'error')
  } finally {
    loading.value = false
  }
}

function copyResult() {
  if (!previewResult.value?.formattedList) return
  const text = previewResult.value.formattedList.join('\n\n')
  navigator.clipboard.writeText(text).then(() => {
    showToast('已复制到剪贴板', 'success')
  }).catch(() => {
    showToast('复制失败', 'error')
  })
}

onMounted(() => {
  // Get IDs from query params
  const idsParam = route.query.ids
  if (idsParam) {
    initialIds.value = idsParam.split(',').map(Number).filter(Boolean)
  }

  loadStandards()
  if (store.list.length === 0) {
    store.fetchList()
  }
})
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">参考文献格式排版</h1>

    <!-- Literature selector -->
    <div v-if="store.list.length > 0" class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
      <h2 class="text-sm font-medium text-gray-700 mb-3">选择文献</h2>
      <div v-if="initialIds.length > 0" class="text-sm text-gray-500 mb-3">
        已从文献库选择 {{ initialIds.length }} 篇文献
      </div>
      <div class="max-h-48 overflow-y-auto space-y-2">
        <label
          v-for="item in store.list"
          :key="item.id"
          class="flex items-center space-x-3 p-2 rounded hover:bg-gray-50 cursor-pointer"
        >
          <input
            type="checkbox"
            :checked="store.selectedIds.includes(item.id) || initialIds.includes(item.id)"
            @change="store.toggleSelected(item.id)"
            class="h-4 w-4 text-primary border-gray-300 rounded"
          />
          <span class="text-sm text-gray-700 truncate">{{ item.title || '无标题' }}</span>
        </label>
      </div>
      <div class="mt-2 text-sm text-gray-500">
        已选 {{ initialIds.length > 0 ? initialIds.length : store.selectedIds.length }} 篇
      </div>
    </div>

    <!-- Format standard selector -->
    <div class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
      <h2 class="text-sm font-medium text-gray-700 mb-4">格式标准</h2>
      <div class="space-y-3">
        <label
          v-for="std in standards"
          :key="std"
          class="flex items-center space-x-3 cursor-pointer"
        >
          <input
            type="radio"
            :value="std"
            v-model="selectedStandard"
            class="h-4 w-4 text-primary border-gray-300"
          />
          <span class="text-sm text-gray-700">{{ std }}</span>
        </label>
      </div>

      <!-- Sort selector -->
      <div class="mt-6">
        <h2 class="text-sm font-medium text-gray-700 mb-3">排序方式</h2>
        <div class="flex space-x-4">
          <label
            v-for="opt in sortOptions"
            :key="opt.value"
            class="flex items-center space-x-2 cursor-pointer"
          >
            <input
              type="radio"
              :value="opt.value"
              v-model="sortBy"
              class="h-4 w-4 text-primary border-gray-300"
            />
            <span class="text-sm text-gray-700">{{ opt.label }}</span>
          </label>
        </div>
      </div>

      <!-- Generate button -->
      <div class="mt-6">
        <button
          @click="generatePreview"
          class="btn-primary"
          :disabled="loading"
        >
          {{ loading ? '生成中...' : '生成预览' }}
        </button>
      </div>
    </div>

    <!-- Loading -->
    <LoadingSkeleton v-if="loading" type="text" :count="4" />

    <!-- Error -->
    <div
      v-if="error"
      class="bg-red-50 border border-red-200 text-red-600 text-sm rounded-lg p-4 mb-6"
    >
      {{ error }}
    </div>

    <!-- Preview result -->
    <div v-if="previewResult" class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-sm font-medium text-gray-700">
          预览 ({{ previewResult.standard || selectedStandard }})
        </h2>
        <button
          @click="copyResult"
          class="text-sm text-primary hover:text-primary-hover flex items-center space-x-1"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
          </svg>
          <span>复制全部</span>
        </button>
      </div>
      <div class="space-y-3">
        <div
          v-for="(ref, index) in previewResult.formattedList"
          :key="index"
          class="p-3 bg-gray-50 rounded-lg text-sm text-gray-700 leading-relaxed"
        >
          {{ ref }}
        </div>
      </div>
    </div>

    <!-- If no literature in store -->
    <div
      v-if="!loading && store.list.length === 0"
      class="bg-white rounded-xl border border-gray-200 p-8 text-center"
    >
      <p class="text-gray-500 mb-4">还没有文献，请先导入文献</p>
      <router-link to="/literature/import" class="btn-primary inline-block">导入文献</router-link>
    </div>

    <!-- Toast -->
    <Toast
      :message="toastMessage"
      :type="toastType"
      :visible="toastVisible"
      @close="toastVisible = false"
    />
  </div>
</template>
