import { defineStore } from 'pinia'
import { list, getById, upload as uploadApi, remove as removeApi } from '@/api/literature'

export const useLiteratureStore = defineStore('literature', {
  state: () => ({
    list: [],
    current: null,
    loading: false,
    error: null,
    selectedIds: []
  }),
  getters: {
    count(state) {
      return state.list.length
    },
    lastThree(state) {
      return state.list.slice(0, 3)
    }
  },
  actions: {
    async fetchList() {
      this.loading = true
      this.error = null
      try {
        const res = await list()
        this.list = res.data || []
      } catch (e) {
        this.error = e.response?.data?.message || '获取文献列表失败'
      } finally {
        this.loading = false
      }
    },
    async fetchById(id) {
      this.loading = true
      this.error = null
      this.current = null
      try {
        const res = await getById(id)
        this.current = res.data
        return res.data
      } catch (e) {
        this.error = e.response?.data?.message || '获取文献详情失败'
      } finally {
        this.loading = false
      }
    },
    async upload(file) {
      this.loading = true
      this.error = null
      try {
        const formData = new FormData()
        formData.append('file', file)
        const res = await uploadApi(formData)
        this.list.unshift(res.data)
        return res.data
      } catch (e) {
        this.error = e.response?.data?.message || '上传文献失败'
        throw e
      } finally {
        this.loading = false
      }
    },
    async remove(id) {
      this.error = null
      try {
        await removeApi(id)
        this.list = this.list.filter((item) => item.id !== id)
        if (this.current && this.current.id === id) {
          this.current = null
        }
      } catch (e) {
        this.error = e.response?.data?.message || '删除文献失败'
        throw e
      }
    },
    toggleSelected(id) {
      const idx = this.selectedIds.indexOf(id)
      if (idx === -1) {
        this.selectedIds.push(id)
      } else {
        this.selectedIds.splice(idx, 1)
      }
    },
    clearSelected() {
      this.selectedIds = []
    }
  }
})
