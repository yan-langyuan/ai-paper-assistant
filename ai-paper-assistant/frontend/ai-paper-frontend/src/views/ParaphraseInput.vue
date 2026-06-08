<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useParaphraseStore } from '@/stores/paraphrase'
import RiskWarning from '@/components/RiskWarning.vue'

const router = useRouter()
const store = useParaphraseStore()

const text = ref('')
const showRiskModal = ref(false)
const riskLevel = ref('medium')
const riskPercentage = ref(0)

const charCount = computed(() => text.value.length)
const isWithinRange = computed(() => charCount.value >= 500 && charCount.value <= 3000)
const isTooLong = computed(() => charCount.value > 3000)
const isTooShort = computed(() => charCount.value > 0 && charCount.value < 500)

function handleDetect() {
  if (charCount.value < 500 || charCount.value > 3000) return

  // Start detection
  detectAndNavigate()
}

async function detectAndNavigate() {
  try {
    await store.detectText(text.value)
    router.push('/paraphrase/result')
  } catch (e) {
    // Error is stored in store.error - could show via toast
  }
}
</script>

<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900">降重改写助手</h1>
      <div class="text-xs text-gray-500 space-y-0.5 text-right">
        <p>今日 {{ store.todayCount }} 次</p>
        <p>本周 {{ store.weekCount }} 次</p>
      </div>
    </div>

    <!-- Textarea -->
    <div class="bg-white rounded-xl border border-gray-200 p-6 mb-4">
      <textarea
        v-model="text"
        placeholder="在此粘贴您的论文段落（500 - 3000字）"
        class="w-full min-h-[8rem] resize-y border-0 focus:ring-0 outline-none text-sm text-gray-700 placeholder-gray-400 leading-relaxed"
        style="min-height: 10rem;"
      ></textarea>

      <!-- Char count -->
      <div class="flex items-center justify-between mt-2 pt-2 border-t border-gray-100">
        <div>
          <p v-if="isTooShort" class="text-xs text-yellow-500">字数不足500，无法检测</p>
          <p v-if="isTooLong" class="text-xs text-red-500">字数超过3000限制</p>
        </div>
        <span
          class="text-xs"
          :class="isTooLong ? 'text-red-500 font-medium' : 'text-gray-400'"
        >
          {{ charCount }}/3000
        </span>
      </div>
    </div>

    <!-- Privacy notice -->
    <div class="bg-blue-50 border border-blue-100 rounded-lg p-4 mb-3">
      <div class="flex items-start space-x-2">
        <svg class="w-4 h-4 text-blue-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
        </svg>
        <p class="text-xs text-blue-700">检测在您的设备本地完成，文本不会上传至服务器</p>
      </div>
    </div>

    <!-- Warning notice -->
    <div class="bg-yellow-50 border border-yellow-100 rounded-lg p-4 mb-6">
      <div class="flex items-start space-x-2">
        <svg class="w-4 h-4 text-yellow-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z" />
        </svg>
        <p class="text-xs text-yellow-700">本检测为AI语义比对，非正式查重系统结果，不替代知网/维普等官方查重</p>
      </div>
    </div>

    <!-- Detect button -->
    <button
      @click="handleDetect"
      class="btn-primary w-full py-3 text-base"
      :disabled="charCount < 500 || charCount > 3000 || store.loading"
    >
      {{ store.loading ? '检测中...' : '开始检测' }}
    </button>

    <!-- Risk warning modal -->
    <RiskWarning
      :visible="showRiskModal"
      :level="riskLevel"
      :percentage="riskPercentage"
      @close="showRiskModal = false"
      @confirm="showRiskModal = false; detectAndNavigate()"
    />
  </div>
</template>
