import { defineStore } from 'pinia'
import { detect as detectApi, rewrite as rewriteApi } from '@/api/paraphrase'

export const useParaphraseStore = defineStore('paraphrase', {
  state: () => ({
    originalText: '',
    detectResult: null,
    loading: false,
    error: null,
    todayCount: 0,
    weekCount: 0
  }),
  actions: {
    async detectText(text) {
      this.loading = true
      this.error = null
      this.originalText = text
      try {
        const res = await detectApi({ text })
        this.detectResult = res.data
        this.todayCount++
        this.weekCount++
        return res.data
      } catch (e) {
        this.error = e.response?.data?.message || '检测失败'
        throw e
      } finally {
        this.loading = false
      }
    },
    async rewriteText(text) {
      this.loading = true
      this.error = null
      try {
        const res = await rewriteApi({ originalText: text })
        return res.data
      } catch (e) {
        this.error = e.response?.data?.message || '改写失败'
        throw e
      } finally {
        this.loading = false
      }
    },
    clearResult() {
      this.originalText = ''
      this.detectResult = null
    }
  }
})
