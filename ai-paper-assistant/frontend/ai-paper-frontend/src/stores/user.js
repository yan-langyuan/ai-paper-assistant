import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi } from '@/api/auth'

function parseToken(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.userId || payload.sub || null
  } catch {
    return null
  }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('auth_token') || '',
    username: localStorage.getItem('auth_username') || '',
    isLoggedIn: !!localStorage.getItem('auth_token')
  }),
  getters: {
    userId(state) {
      if (!state.token) return null
      return parseToken(state.token)
    }
  },
  actions: {
    async login(data) {
      const res = await loginApi(data)
      this.token = res.data.token
      this.username = res.data.username
      this.isLoggedIn = true
      localStorage.setItem('auth_token', this.token)
      localStorage.setItem('auth_username', this.username)
    },
    async register(data) {
      const res = await registerApi(data)
      this.token = res.data.token
      this.username = res.data.username
      this.isLoggedIn = true
      localStorage.setItem('auth_token', this.token)
      localStorage.setItem('auth_username', this.username)
    },
    logout() {
      this.token = ''
      this.username = ''
      this.isLoggedIn = false
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_username')
    },
    checkAuth() {
      const token = localStorage.getItem('auth_token')
      this.token = token || ''
      this.username = localStorage.getItem('auth_username') || ''
      this.isLoggedIn = !!token
    }
  }
})
