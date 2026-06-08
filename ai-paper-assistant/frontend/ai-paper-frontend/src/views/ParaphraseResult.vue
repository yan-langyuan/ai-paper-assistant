<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useParaphraseStore } from '@/stores/paraphrase'
import HighlightText from '@/components/HighlightText.vue'
import RewriteDrawer from '@/components/RewriteDrawer.vue'
import EmptyState from '@/components/EmptyState.vue'
import Toast from '@/components/Toast.vue'

const router = useRouter()
const store = useParaphraseStore()

const sentences = ref([])
const drawerVisible = ref(false)
const currentSentenceIndex = ref(-1)
const currentRewriteResult = ref(null)
const rewriteLoading = ref(false)
const toastMessage = ref('')
const toastType = ref('info')
const toastVisible = ref(false)

// Track sentence states: { originalText, currentText, appliedScheme, status }
const sentenceStates = ref({})

function showToast(message, type = 'info') {
  toastMessage.value = message
  toastType.value = type
  toastVisible.value = true
}

const stats = computed(() => {
  const items = sentences.value
  return {
    high: items.filter((s) => s.similarity > 80).length,
    medium: items.filter((s) => s.similarity >= 50 && s.similarity <= 80).length,
    low: items.filter((s) => s.similarity < 50).length
  }
})

function getSentenceDisplay(index) {
  const state = sentenceStates.value[index]
  if (state && state.status === 'modified') {
    return state.currentText
  }
  return sentences.value[index]?.text || ''
}

async function handleViewRewrite(index) {
  currentSentenceIndex.value = index
  const sentence = sentences.value[index]

  if (!sentence || sentence.similarity < 50) return

  rewriteLoading.value = true
  try {
    const result = await store.rewriteText(sentence.text)
    currentRewriteResult.value = {
      schemes: [
        {
          type: 'A',
          label: '保守改写',
          text: result.schemeA?.text || '',
          keptTerms: result.schemeA?.keptTerms || [],
          changes: result.schemeA?.changes || [],
          warning: result.schemeA?.warning || ''
        },
        {
          type: 'B',
          label: '均衡改写',
          text: result.schemeB?.text || '',
          keptTerms: result.schemeB?.keptTerms || [],
          changes: result.schemeB?.changes || [],
          warning: result.schemeB?.warning || ''
        },
        {
          type: 'C',
          label: '强力改写',
          text: result.schemeC?.text || '',
          keptTerms: result.schemeC?.keptTerms || [],
          changes: result.schemeC?.changes || [],
          warning: result.schemeC?.warning || '强力改写可能改变原文含义，请仔细核对'
        }
      ]
    }
    drawerVisible.value = true
  } catch {
    showToast('获取改写建议失败', 'error')
  } finally {
    rewriteLoading.value = false
  }
}

function handleApplyScheme(scheme) {
  if (currentSentenceIndex.value < 0) return

  const index = currentSentenceIndex.value
  const original = sentences.value[index]?.text || ''

  sentenceStates.value = {
    ...sentenceStates.value,
    [index]: {
      originalText: original,
      currentText: scheme.text,
      appliedScheme: scheme.type,
      status: 'modified'
    }
  }

  drawerVisible.value = false
  showToast('已应用改写方案', 'success')
}

function handleCloseDrawer() {
  drawerVisible.value = false
  currentSentenceIndex.value = -1
  currentRewriteResult.value = null
}

function handleNextSimilar() {
  const currentIdx = currentSentenceIndex.value
  const items = sentences.value
  for (let i = currentIdx + 1; i < items.length; i++) {
    if (items[i].similarity >= 50) {
      handleViewRewrite(i)
      return
    }
  }
  showToast('已到达最后一处', 'info')
}

function handleSave() {
  showToast('修改已保存', 'success')
}

function goBack() {
  store.clearResult()
  router.push('/paraphrase')
}

onMounted(() => {
  if (!store.detectResult || !store.detectResult.sentences) {
    router.push('/paraphrase')
    return
  }
  sentences.value = store.detectResult.sentences
})
</script>

<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <!-- Back button -->
    <button
      @click="goBack"
      class="flex items-center text-sm text-gray-500 hover:text-gray-700 mb-4"
    >
      <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      返回输入
    </button>

    <div v-if="sentences.length === 0">
      <EmptyState
        title="暂无检测结果"
        description="请先输入文本进行检测"
        action-label="去检测"
        @action="goBack"
      />
      return
    </div>

    <template v-else>
      <!-- Summary stats bar -->
      <div class="sticky top-16 z-20 bg-white border border-gray-200 rounded-xl p-4 mb-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <span class="inline-flex items-center px-2.5 py-1 rounded text-sm font-medium bg-red-100 text-red-700">
              相似 {{ stats.high }} 处
            </span>
            <span class="inline-flex items-center px-2.5 py-1 rounded text-sm font-medium bg-yellow-100 text-yellow-700">
              疑似 {{ stats.medium }} 处
            </span>
            <span class="inline-flex items-center px-2.5 py-1 rounded text-sm font-medium bg-green-100 text-green-700">
              正常 {{ stats.low }} 处
            </span>
          </div>
          <button
            @click="handleSave"
            class="btn-primary text-sm"
          >
            保存修改
          </button>
        </div>
      </div>

      <!-- Full text with highlighting -->
      <div class="mb-8">
        <HighlightText
          :sentences="sentences.map((s, i) => ({
            ...s,
            text: getSentenceDisplay(i)
          }))"
          @viewRewrite="handleViewRewrite"
        />
      </div>

      <!-- Bottom info -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-4 text-center">
        <p class="text-xs text-gray-500">本页为AI语义比对结果，非正式查重报告</p>
      </div>
    </template>

    <!-- Rewrite Drawer -->
    <RewriteDrawer
      :visible="drawerVisible"
      :original-text="currentSentenceIndex >= 0 ? sentences[currentSentenceIndex]?.text : ''"
      :similarity="currentSentenceIndex >= 0 ? sentences[currentSentenceIndex]?.similarity : 0"
      :schemes="currentRewriteResult?.schemes || []"
      @close="handleCloseDrawer"
      @apply="handleApplyScheme"
    />

    <!-- Next button in drawer placeholder - show as floating when drawer is open -->
    <div
      v-if="drawerVisible"
      class="fixed bottom-4 left-4 z-50"
    >
      <button
        @click="handleNextSimilar"
        class="bg-white border border-gray-200 shadow-lg rounded-lg px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 flex items-center space-x-2"
      >
        <span>下一处</span>
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
        </svg>
      </button>
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
