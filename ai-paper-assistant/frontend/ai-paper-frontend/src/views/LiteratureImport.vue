<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLiteratureStore } from '@/stores/literature'

const router = useRouter()
const store = useLiteratureStore()

const selectedFile = ref(null)
const uploading = ref(false)
const progress = ref(0)
const error = ref('')
const success = ref(false)

const MAX_FILE_SIZE = 50 * 1024 * 1024 // 50MB

function handleDrop(e) {
  e.preventDefault()
  const file = e.dataTransfer.files[0]
  validateAndSetFile(file)
}

function handleFileSelect(e) {
  const file = e.target.files[0]
  validateAndSetFile(file)
}

function validateAndSetFile(file) {
  error.value = ''
  if (!file) return

  if (!file.name.toLowerCase().endsWith('.pdf')) {
    error.value = '仅支持PDF格式文件'
    return
  }

  if (file.size > MAX_FILE_SIZE) {
    error.value = '文件大小超过50MB限制'
    return
  }

  selectedFile.value = file
}

function removeFile() {
  selectedFile.value = null
  error.value = ''
}

function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + 'B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + 'KB'
  return (bytes / (1024 * 1024)).toFixed(1) + 'MB'
}

async function handleUpload() {
  if (!selectedFile.value) return

  uploading.value = true
  error.value = ''
  progress.value = 0

  try {
    // Simulate progress
    const progressInterval = setInterval(() => {
      progress.value = Math.min(progress.value + 10, 90)
    }, 200)

    const result = await store.upload(selectedFile.value)
    clearInterval(progressInterval)
    progress.value = 100
    success.value = true

    setTimeout(() => {
      router.push(`/literature/${result.id}`)
    }, 1000)
  } catch (e) {
    error.value = e.response?.data?.message || '上传失败，请重试'
    progress.value = 0
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">导入文献</h1>

    <!-- Success state -->
    <div v-if="success" class="bg-green-50 border border-green-200 rounded-xl p-8 text-center">
      <svg class="w-12 h-12 text-green-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <h2 class="text-lg font-semibold text-green-700 mb-2">上传成功，正在解析...</h2>
      <p class="text-sm text-green-600">即将跳转到文献详情页</p>
    </div>

    <!-- Upload area -->
    <div v-else>
      <!-- Drag drop zone -->
      <div
        @dragover.prevent="(e) => e.preventDefault()"
        @drop="handleDrop"
        class="border-2 border-dashed border-gray-300 rounded-xl p-12 text-center hover:border-primary transition-colors cursor-pointer"
        :class="{ 'border-primary bg-blue-50': selectedFile }"
      >
        <input
          type="file"
          accept=".pdf"
          class="hidden"
          ref="fileInput"
          id="fileInput"
          @change="handleFileSelect"
        />
        <label for="fileInput" class="cursor-pointer">
          <svg class="w-12 h-12 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
          </svg>
          <p class="text-sm text-gray-600 mb-1">点击或拖拽上传PDF</p>
          <p class="text-xs text-gray-400">单次最多10篇，单篇最大50MB</p>
        </label>
      </div>

      <!-- Error message -->
      <div
        v-if="error"
        class="mt-4 bg-red-50 border border-red-200 text-red-600 text-sm rounded-lg p-3"
      >
        {{ error }}
      </div>

      <!-- File selected info -->
      <div v-if="selectedFile" class="mt-6 bg-white rounded-lg border border-gray-200 p-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <svg class="w-8 h-8 text-red-500" fill="currentColor" viewBox="0 0 24 24">
              <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" />
              <polyline points="14 2 14 8 20 8" />
            </svg>
            <div>
              <p class="text-sm font-medium text-gray-900">{{ selectedFile.name }}</p>
              <p class="text-xs text-gray-500">{{ formatFileSize(selectedFile.size) }}</p>
            </div>
          </div>
          <button @click="removeFile" class="text-gray-400 hover:text-red-500">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Progress bar -->
      <div v-if="uploading" class="mt-4">
        <div class="w-full bg-gray-200 rounded-full h-2">
          <div
            class="bg-primary h-2 rounded-full transition-all duration-300"
            :style="{ width: progress + '%' }"
          ></div>
        </div>
        <p class="text-xs text-gray-500 mt-1 text-right">{{ progress }}%</p>
      </div>

      <!-- Upload button -->
      <div v-if="selectedFile && !uploading" class="mt-6">
        <button
          @click="handleUpload"
          class="btn-primary w-full"
        >
          开始上传
        </button>
      </div>
    </div>
  </div>
</template>
