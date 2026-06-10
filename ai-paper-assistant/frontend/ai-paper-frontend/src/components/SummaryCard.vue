<script setup>
import { computed } from 'vue'

const props = defineProps({
  sections: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: String, default: '' }
})

const emit = defineEmits(['retry'])

const icons = {
  background:  '',
  question:    '',
  method:      '',
  findings:    '',
  limitations: ''
}

const defaultSections = [
  { key: 'background',  label: '研究背景', icon: icons.background },
  { key: 'problem',     label: '研究问题', icon: icons.question },
  { key: 'method',      label: '研究方法', icon: icons.method },
  { key: 'findings',    label: '核心发现', icon: icons.findings },
  { key: 'limitations', label: '研究局限', icon: icons.limitations }
]

const mergedSections = computed(() =>
  props.sections.length > 0 ? props.sections : defaultSections
)

const hasContent = computed(() =>
  props.sections.some((s) => s.content)
)

// Figma 设计五色方案
const SECTION_COLORS = {
  background:  { bg: 'bg-blue-50',   text: 'text-blue-600',  dot: 'bg-blue-500' },
  question:    { bg: 'bg-purple-50', text: 'text-purple-600', dot: 'bg-purple-500' },
  method:      { bg: 'bg-cyan-50',   text: 'text-cyan-600',  dot: 'bg-cyan-500' },
  findings:    { bg: 'bg-green-50',  text: 'text-green-600', dot: 'bg-green-500' },
  limitations: { bg: 'bg-amber-50',  text: 'text-amber-600', dot: 'bg-amber-500' },
}

function sectionColor(key) {
  return SECTION_COLORS[key] || { bg: 'bg-ink-50', text: 'text-ink-600', dot: 'bg-ink-500' }
}

function handleCopy() {
  const text = mergedSections.value
    .filter((s) => s.content)
    .map((s) => `${s.label}\n${s.content}`)
    .join('\n\n')
  navigator.clipboard.writeText(text).catch(() => {})
}
</script>

<template>
  <div class="ai-block card-reveal">

    <!-- 标题行 -->
    <div class="flex items-center justify-between mb-5">
      <div class="flex items-center gap-2">
        <span class="w-1 h-4 bg-amber rounded-full"></span>
        <h3 class="font-display text-base text-ink-800">结构化摘要</h3>
      </div>
      <button
        v-if="!loading && hasContent"
        @click="handleCopy"
        class="btn-ghost text-xs"
      >
        复制摘要
      </button>
    </div>

    <!-- 加载态 -->
    <div v-if="loading" class="space-y-5">
      <div v-for="i in 5" :key="i">
        <div class="skeleton h-3 w-20 mb-2 shimmer"></div>
        <div class="skeleton h-3 w-full mb-1.5 shimmer"></div>
        <div class="skeleton h-3 w-3/4 shimmer"></div>
      </div>
    </div>

    <!-- 错误态 -->
    <div v-else-if="error" class="text-center py-6">
      <p class="text-ink-500 text-sm mb-3">{{ error }}</p>
      <button @click="emit('retry')" class="btn-secondary text-sm">重试</button>
    </div>

    <!-- 摘要内容 — Figma 五色模块 -->
    <div v-else class="space-y-3">
      <div
        v-for="section in mergedSections"
        :key="section.key"
        class="rounded-xl p-3"
        :class="sectionColor(section.key).bg"
      >
        <h4 class="text-xs font-medium mb-1.5" :class="sectionColor(section.key).text">
          {{ section.label }}
        </h4>
        <template v-if="section.key === 'findings'">
          <ul class="space-y-1">
            <li
              v-for="(finding, fi) in (Array.isArray(section.content) ? section.content : [section.content])"
              :key="fi"
              class="text-sm text-ink-700 leading-relaxed flex items-start gap-2"
            >
              <span class="mt-1.5 w-1 h-1 rounded-full flex-shrink-0" :class="sectionColor(section.key).dot"></span>
              <span>{{ finding }}</span>
            </li>
          </ul>
        </template>
        <p v-else class="text-sm text-ink-700 leading-relaxed">
          {{ section.content || '暂无内容' }}
        </p>
      </div>
    </div>

    <!-- AI 免责声明 -->
    <div
      v-if="!loading && hasContent"
      class="mt-5 pt-4 border-t border-ink-100"
    >
      <p class="ai-disclaimer">
        AI生成摘要，仅供参考，请核实原文
      </p>
    </div>
  </div>
</template>
