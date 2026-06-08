<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useParaphraseStore } from '@/stores/paraphrase'

const router = useRouter()
const userStore = useUserStore()
const paraphraseStore = useParaphraseStore()

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">个人中心</h1>

    <!-- User info card -->
    <div class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
      <div class="flex items-center space-x-4 mb-6">
        <div class="w-16 h-16 rounded-full bg-primary text-white flex items-center justify-center text-2xl font-bold">
          {{ userStore.username.charAt(0).toUpperCase() }}
        </div>
        <div>
          <h2 class="text-xl font-semibold text-gray-900">{{ userStore.username }}</h2>
          <p class="text-sm text-gray-500">AI论文助手用户</p>
        </div>
      </div>

      <div class="border-t border-gray-100 pt-4 space-y-3">
        <div class="flex items-center justify-between">
          <span class="text-sm text-gray-500">用户名</span>
          <span class="text-sm text-gray-900 font-medium">{{ userStore.username }}</span>
        </div>
        <!-- Additional user info fields (from registration) -->
        <div class="flex items-center justify-between">
          <span class="text-sm text-gray-500">邮箱</span>
          <span class="text-sm text-gray-900">--</span>
        </div>
        <div class="flex items-center justify-between">
          <span class="text-sm text-gray-500">年级</span>
          <span class="text-sm text-gray-900">--</span>
        </div>
        <div class="flex items-center justify-between">
          <span class="text-sm text-gray-500">专业</span>
          <span class="text-sm text-gray-900">--</span>
        </div>
      </div>
    </div>

    <!-- Usage stats card -->
    <div class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
      <h2 class="text-sm font-medium text-gray-700 mb-4">AI使用统计</h2>
      <div class="grid grid-cols-2 gap-4">
        <div class="bg-blue-50 rounded-lg p-4 text-center">
          <p class="text-2xl font-bold text-primary">{{ paraphraseStore.todayCount }}</p>
          <p class="text-xs text-gray-500 mt-1">今日检测次数</p>
        </div>
        <div class="bg-blue-50 rounded-lg p-4 text-center">
          <p class="text-2xl font-bold text-primary">{{ paraphraseStore.weekCount }}</p>
          <p class="text-xs text-gray-500 mt-1">本周检测次数</p>
        </div>
      </div>
    </div>

    <!-- Logout button -->
    <button
      @click="handleLogout"
      class="w-full py-3 text-sm text-red-600 border border-red-200 rounded-lg hover:bg-red-50 transition-colors"
    >
      退出登录
    </button>
  </div>
</template>
