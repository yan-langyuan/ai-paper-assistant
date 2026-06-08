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

    <!-- 摘要内容 — 左侧竖线的编辑排版风格 -->
    <div v-else class="space-y-5">
      <div
        v-for="section in mergedSections"
        :key="section.key"
        class="relative pl-5"
      >
        <!-- 每个 section 左侧的竖线 -->
        <span class="absolute left-0 top-0 bottom-0 w-0.5 rounded-full"
              :class="section.key === 'findings' ? 'bg-amber' : 'bg-ink-200'">
        </span>

        <h4 class="text-xs font-medium text-ink-400 tracking-wide mb-1.5 uppercase font-mono">
          {{ section.label }}
        </h4>
        <template v-if="section.key === 'findings'">
          <ul class="space-y-1.5">
            <li
              v-for="(finding, fi) in (Array.isArray(section.content) ? section.content : [section.content])"
              :key="fi"
              class="text-sm text-ink-700 leading-relaxed flex items-start gap-2"
            >
              <span class="text-amber mt-1.5 shrink-0">&mdash;</span>
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
