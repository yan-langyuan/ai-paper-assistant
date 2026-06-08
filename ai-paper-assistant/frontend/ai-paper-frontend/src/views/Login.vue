<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const error = ref('')
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  email: '',
  grade: '',
  major: ''
})

const gradeOptions = ['本科一年级', '本科二年级', '本科三年级', '本科四年级', '硕士一年级', '硕士二年级', '硕士三年级', '博士']

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    error.value = '请填写用户名和密码'
    return
  }
  error.value = ''
  loading.value = true
  try {
    await userStore.login(loginForm)
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password || !registerForm.email) {
    error.value = '请填写完整信息'
    return
  }
  error.value = ''
  loading.value = true
  try {
    await userStore.register(registerForm)
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] flex items-center justify-center px-4 py-12">
    <div class="w-full max-w-md">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
        <h2 class="text-2xl font-bold text-center text-gray-900 mb-6">AI论文助手</h2>

        <!-- Tabs -->
        <div class="flex border-b border-gray-200 mb-6">
          <button
            @click="activeTab = 'login'; error = ''"
            class="flex-1 pb-3 text-sm font-medium text-center transition-colors"
            :class="activeTab === 'login' ? 'text-primary border-b-2 border-primary' : 'text-gray-500 hover:text-gray-700'"
          >
            登录
          </button>
          <button
            @click="activeTab = 'register'; error = ''"
            class="flex-1 pb-3 text-sm font-medium text-center transition-colors"
            :class="activeTab === 'register' ? 'text-primary border-b-2 border-primary' : 'text-gray-500 hover:text-gray-700'"
          >
            注册
          </button>
        </div>

        <!-- Error message -->
        <div
          v-if="error"
          class="bg-red-50 border border-red-200 text-red-600 text-sm rounded-lg p-3 mb-4"
        >
          {{ error }}
        </div>

        <!-- Login form -->
        <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
            <input
              v-model="loginForm.username"
              type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary focus:border-primary outline-none"
              placeholder="请输入用户名"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
            <input
              v-model="loginForm.password"
              type="password"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary focus:border-primary outline-none"
              placeholder="请输入密码"
            />
          </div>
          <button
            type="submit"
            class="btn-primary w-full"
            :disabled="loading"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>

        <!-- Register form -->
        <form v-if="activeTab === 'register'" @submit.prevent="handleRegister" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
            <input
              v-model="registerForm.username"
              type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary focus:border-primary outline-none"
              placeholder="请输入用户名"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
            <input
              v-model="registerForm.password"
              type="password"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary focus:border-primary outline-none"
              placeholder="请输入密码"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
            <input
              v-model="registerForm.email"
              type="email"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary focus:border-primary outline-none"
              placeholder="请输入邮箱"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">年级</label>
            <select
              v-model="registerForm.grade"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary focus:border-primary outline-none"
            >
              <option value="">请选择年级</option>
              <option v-for="g in gradeOptions" :key="g" :value="g">{{ g }}</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">专业</label>
            <input
              v-model="registerForm.major"
              type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary focus:border-primary outline-none"
              placeholder="请输入专业"
            />
          </div>
          <button
            type="submit"
            class="btn-primary w-full"
            :disabled="loading"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>
