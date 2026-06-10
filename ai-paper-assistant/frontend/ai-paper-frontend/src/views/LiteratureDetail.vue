<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLiteratureStore } from '@/stores/literature'
import { generateSummary } from '@/api/literature'
import SummaryCard from '@/components/SummaryCard.vue'
import LoadingSkeleton from '@/components/LoadingSkeleton.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import Toast from '@/components/Toast.vue'

const route = useRoute()
const router = useRouter()
const store = useLiteratureStore()

const summaryLoading = ref(false)
const summaryError = ref('')
const summarySections = ref([])
const showMenu = ref(false)
const deleteTarget = ref(false)
const toastMessage = ref('')
const toastType = ref('info')
const toastVisible = ref(false)

const literature = computed(() => store.current)
const loading = computed(() => store.loading)

function showToast(message, type = 'info') {
  toastMessage.value = message
  toastType.value = type
  toastVisible.value = true
}

function goBack() {
  router.push('/literature')
}

function copyDOI() {
  if (literature.value?.doi) {
    navigator.clipboard.writeText(literature.value.doi).then(() => {
      showToast('DOI已复制', 'success')
    }).catch(() => {
      showToast('复制失败', 'error')
    })
  }
}

async function loadSummary() {
  if (!literature.value || literature.value.status !== 'COMPLETED') return
  if (literature.value.aiSummary) {
    summarySections.value = parseSummary(literature.value.aiSummary)
    return
  }

  summaryLoading.value = true
  summaryError.value = ''
  try {
    const res = await generateSummary(route.params.id)
    if (res.data && res.data.summary) {
      summarySections.value = parseSummary(res.data.summary)
    }
  } catch (e) {
    summaryError.value = e.response?.data?.message || '获取摘要失败'
  } finally {
    summaryLoading.value = false
  }
}

function parseSummary(summaryText) {
  if (!summaryText) return []

  // 处理字符串类型：先尝试JSON解析，失败则当作纯文本
  if (typeof summaryText === 'string') {
    try {
      const parsed = JSON.parse(summaryText)
      if (typeof parsed === 'object' && !Array.isArray(parsed)) {
        return mapJsonToSections(parsed)
      }
    } catch {
      // 不是合法JSON，当作纯文本摘要
    }
    return [{ key: 'content', label: '摘要内容', content: summaryText }]
  }

  // 已经是对象（极少情况）
  if (typeof summaryText === 'object' && !Array.isArray(summaryText)) {
    return mapJsonToSections(summaryText)
  }

  return []
}

/**
 * 将AI返回的JSON字段映射为SummaryCard需要的sections格式
 * JSON格式: { background, question, method, findings, limitations }
 */
function mapJsonToSections(json) {
  const fieldMap = [
    { key: 'background',  label: '研究背景' },
    { key: 'question',    label: '研究问题' },
    { key: 'method',      label: '研究方法' },
    { key: 'findings',    label: '核心发现' },
    { key: 'limitations', label: '研究局限' },
  ]

  const sections = []
  for (const fm of fieldMap) {
    const value = json[fm.key]
    if (value) {
      sections.push({
        key: fm.key,
        label: fm.label,
        content: value
      })
    }
  }
  return sections.length > 0 ? sections : [{ key: 'content', label: '摘要内容', content: JSON.stringify(json) }]
}

function addToReference() {
  if (literature.value) {
    store.toggleSelected(literature.value.id)
    showToast('已添加到参考文献列表', 'success')
  }
}

async function handleDelete() {
  try {
    await store.remove(route.params.id)
    showToast('删除成功', 'success')
    setTimeout(() => {
      router.push('/literature')
    }, 1000)
  } catch {
    showToast('删除失败', 'error')
  }
  deleteTarget.value = false
}

onMounted(async () => {
  await store.fetchById(route.params.id)
  loadSummary()
})
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <!-- Loading -->
    <LoadingSkeleton v-if="loading" type="card" :count="1" />

    <template v-else-if="literature">
      <!-- Back button -->
      <button
        @click="goBack"
        class="flex items-center text-sm text-gray-500 hover:text-gray-700 mb-4"
      >
        <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        返回文献库
      </button>

      <!-- Metadata section -->
      <div class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
        <div class="flex items-start justify-between">
          <div class="flex-1 min-w-0">
            <h1 class="text-2xl font-bold text-gray-900 mb-2">{{ literature.title || '无标题' }}</h1>
            <div class="space-y-1 text-sm text-gray-600">
              <p v-if="literature.authors"><span class="text-gray-400">作者:</span> {{ literature.authors }}</p>
              <p v-if="literature.journal"><span class="text-gray-400">期刊:</span> {{ literature.journal }}</p>
              <p v-if="literature.volume || literature.issue || literature.pages">
                <span class="text-gray-400">卷期页码:</span>
                {{ literature.volume || '' }}{{ literature.issue ? '(' + literature.issue + ')' : '' }}{{ literature.pages ? ', ' + literature.pages : '' }}
              </p>
              <p v-if="literature.year"><span class="text-gray-400">年份:</span> {{ literature.year }}</p>
              <p v-if="literature.doi" class="flex items-center space-x-1">
                <span class="text-gray-400">DOI:</span>
                <span class="text-primary">{{ literature.doi }}</span>
                <button
                  @click="copyDOI"
                  class="text-gray-400 hover:text-gray-600"
                  title="复制DOI"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
                  </svg>
                </button>
              </p>
            </div>
          </div>

          <!-- More menu -->
          <div class="relative flex-shrink-0 ml-4">
            <button
              @click="showMenu = !showMenu"
              class="p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z" />
              </svg>
            </button>
            <div
              v-if="showMenu"
              class="absolute right-0 mt-2 w-40 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-10"
            >
              <button
                @click="showMenu = false; deleteTarget = true"
                class="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100"
              >
                删除文献
              </button>
            </div>
          </div>
        </div>

        <!-- Abstract -->
        <div v-if="literature.abstractText" class="mt-4 pt-4 border-t border-gray-100">
          <h3 class="text-sm font-medium text-gray-700 mb-2">摘要</h3>
          <p class="text-sm text-gray-600 leading-relaxed">{{ literature.abstractText }}</p>
        </div>

        <!-- Actions -->
        <div class="mt-4 pt-4 border-t border-gray-100 flex space-x-3">
          <button
            @click="addToReference"
            class="btn-secondary text-sm"
          >
            加入参考文献格式化列表
          </button>
        </div>
      </div>

      <!-- Summary Card -->
      <SummaryCard
        :sections="summarySections"
        :loading="summaryLoading"
        :error="summaryError"
        @retry="loadSummary"
      />
    </template>

    <!-- Not found -->
    <div v-else class="text-center py-16">
      <p class="text-gray-500">文献不存在或已被删除</p>
      <button @click="goBack" class="btn-primary mt-4">返回文献库</button>
    </div>

    <!-- Delete Confirm -->
    <ConfirmDialog
      :visible="deleteTarget"
      title="删除文献"
      message="确定要删除这篇文献吗？此操作不可恢复。"
      confirm-text="删除"
      :danger="true"
      @confirm="handleDelete"
      @cancel="deleteTarget = false"
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
