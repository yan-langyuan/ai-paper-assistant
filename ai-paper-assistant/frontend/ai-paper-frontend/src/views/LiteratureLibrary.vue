<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useLiteratureStore } from '@/stores/literature'
import LoadingSkeleton from '@/components/LoadingSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import Toast from '@/components/Toast.vue'

const router = useRouter()
const store = useLiteratureStore()

const searchQuery = ref('')
const activeFilter = ref('all')
const deleteTarget = ref(null)
const toastMessage = ref('')
const toastType = ref('info')
const toastVisible = ref(false)

const filters = [
  { key: 'all', label: '全部' },
  { key: 'COMPLETED', label: '已完成' },
  { key: 'PARSING', label: '解析中' },
  { key: 'FAILED', label: '解析失败' }
]

const filteredList = computed(() => {
  let items = store.list
  if (activeFilter.value !== 'all') {
    items = items.filter((item) => item.status === activeFilter.value)
  }
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase()
    items = items.filter(
      (item) =>
        (item.title && item.title.toLowerCase().includes(q)) ||
        (item.authors && item.authors.toLowerCase().includes(q))
    )
  }
  return items
})

const statusBadge = (status) => {
  const map = {
    COMPLETED: { label: '已完成', class: 'bg-green-100 text-green-700' },
    PARSING: { label: '解析中', class: 'bg-yellow-100 text-yellow-700' },
    FAILED: { label: '解析失败', class: 'bg-red-100 text-red-700' }
  }
  return map[status] || { label: status || '未知', class: 'bg-gray-100 text-gray-600' }
}

function showToast(message, type = 'info') {
  toastMessage.value = message
  toastType.value = type
  toastVisible.value = true
}

function handleSelect(id) {
  store.toggleSelected(id)
}

function handleDelete(id) {
  deleteTarget.value = id
}

async function confirmDelete() {
  if (deleteTarget.value) {
    try {
      await store.remove(deleteTarget.value)
      showToast('删除成功', 'success')
    } catch {
      showToast('删除失败', 'error')
    }
  }
  deleteTarget.value = null
}

function goToImport() {
  router.push('/literature/import')
}

function goToDetail(id) {
  router.push(`/literature/${id}`)
}

function goToReference() {
  if (store.selectedIds.length === 0) return
  router.push({
    path: '/reference',
    query: { ids: store.selectedIds.join(',') }
  })
}

onMounted(() => {
  store.fetchList()
})
</script>

<template>
  <div class="max-w-5xl mx-auto px-4 py-8">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900">我的文献库 ({{ store.count }})</h1>
      <button @click="goToImport" class="btn-primary">导入文献</button>
    </div>

    <!-- Search & Filters -->
    <div class="mb-6 space-y-4">
      <div class="relative">
        <svg class="absolute left-3 top-2.5 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索文献标题或作者..."
          class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary focus:border-primary outline-none"
        />
      </div>
      <div class="flex space-x-2">
        <button
          v-for="f in filters"
          :key="f.key"
          @click="activeFilter = f.key"
          class="px-3 py-1.5 text-sm rounded-lg transition-colors"
          :class="activeFilter === f.key ? 'bg-primary text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
        >
          {{ f.label }}
        </button>
      </div>
    </div>

    <!-- Loading -->
    <LoadingSkeleton v-if="store.loading" type="list" :count="5" />

    <!-- Empty state -->
    <EmptyState
      v-else-if="filteredList.length === 0"
      title="暂无文献"
      :description="searchQuery ? '未找到匹配的文献' : '开始上传你的第一篇论文吧'"
      action-label="导入文献"
      @action="goToImport"
    />

    <!-- List -->
    <div v-else class="space-y-2">
      <div
        v-for="item in filteredList"
        :key="item.id"
        class="bg-white rounded-lg border border-gray-200 p-4 flex items-center space-x-4 hover:shadow-sm transition-shadow group"
      >
        <input
          type="checkbox"
          :checked="store.selectedIds.includes(item.id)"
          @change="handleSelect(item.id)"
          class="h-4 w-4 text-primary border-gray-300 rounded"
        />
        <div
          @click="goToDetail(item.id)"
          class="flex-1 min-w-0 cursor-pointer"
        >
          <h3 class="text-sm font-medium text-gray-900 truncate">{{ item.title || '无标题' }}</h3>
          <p class="text-xs text-gray-500 mt-0.5">
            {{ item.authors || '未知作者' }}
            <template v-if="item.journal"> | {{ item.journal }}</template>
            <template v-if="item.year"> | {{ item.year }}</template>
          </p>
        </div>
        <span
          class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium flex-shrink-0"
          :class="statusBadge(item.status).class"
        >
          {{ statusBadge(item.status).label }}
        </span>
        <svg class="w-5 h-5 text-gray-300 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </div>
    </div>

    <!-- Bottom Floating Bar -->
    <div
      v-if="store.selectedIds.length > 0"
      class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 shadow-lg px-4 py-3 z-30"
    >
      <div class="max-w-5xl mx-auto flex items-center justify-between">
        <span class="text-sm text-gray-700">已选 {{ store.selectedIds.length }} 篇</span>
        <div class="flex space-x-3">
          <button @click="store.clearSelected()" class="btn-secondary text-sm">取消选择</button>
          <button @click="goToReference" class="btn-primary text-sm">格式化引用</button>
        </div>
      </div>
    </div>

    <!-- Delete Confirm -->
    <ConfirmDialog
      :visible="!!deleteTarget"
      title="删除文献"
      message="确定要删除这篇文献吗？此操作不可恢复。"
      confirm-text="删除"
      :danger="true"
      @confirm="confirmDelete"
      @cancel="deleteTarget = null"
    />

    <!-- Toast -->
    <Toast
      :message="toastMessage"
      :type="toastType"
      :visible="toastVisible"
      @close="toastVisible = false"
    />
  </div>
</template>
