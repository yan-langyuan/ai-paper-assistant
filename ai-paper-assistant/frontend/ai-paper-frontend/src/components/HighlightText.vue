<script setup>
defineProps({
  sentences: {
    type: Array,
    default: () => []
  }
})

defineEmits(['viewRewrite'])

function getSimilarityLevel(similarity) {
  if (similarity > 80) return 'high'
  if (similarity >= 50) return 'medium'
  return 'low'
}
</script>

<template>
  <div class="space-y-2">
    <div
      v-for="(sentence, index) in sentences"
      :key="index"
      class="p-3 rounded-lg border transition-colors"
      :class="{
        'bg-red-50 border-red-200': getSimilarityLevel(sentence.similarity) === 'high',
        'bg-yellow-50 border-yellow-200': getSimilarityLevel(sentence.similarity) === 'medium',
        'bg-green-50 border-green-200': getSimilarityLevel(sentence.similarity) === 'low'
      }"
    >
      <div class="flex items-start justify-between gap-3">
        <p class="text-sm text-gray-800 leading-relaxed flex-1">{{ sentence.text }}</p>
        <div class="flex-shrink-0 flex flex-col items-center space-y-1">
          <span
            class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium"
            :class="{
              'bg-red-100 text-red-700': getSimilarityLevel(sentence.similarity) === 'high',
              'bg-yellow-100 text-yellow-700': getSimilarityLevel(sentence.similarity) === 'medium',
              'bg-green-100 text-green-700': getSimilarityLevel(sentence.similarity) === 'low'
            }"
          >
            {{ sentence.similarity }}%
          </span>
          <button
            v-if="sentence.similarity >= 50"
            @click="$emit('viewRewrite', index)"
            class="text-xs text-primary hover:text-primary-hover underline whitespace-nowrap"
          >
            查看改写建议
          </button>
        </div>
      </div>
      <p v-if="sentence.suggestion" class="mt-1 text-xs text-gray-500">{{ sentence.suggestion }}</p>
    </div>
  </div>
</template>
