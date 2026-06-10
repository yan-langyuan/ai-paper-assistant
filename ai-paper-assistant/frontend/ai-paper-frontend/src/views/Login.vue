<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const tab = ref('login')
const method = ref('phone')
const showPassword = ref(false)
const agreedToTerms = ref(false)
const error = ref('')
const loading = ref(false)

const form = reactive({
  account: '',
  password: '',
  email: '',
  grade: '',
  major: ''
})

const gradeOptions = ['本科一年级','本科二年级','本科三年级','本科四年级','硕士一年级','硕士二年级','博士']

async function handleSubmit() {
  if (!form.account) { error.value = '请填写账号'; return }
  if (!form.password) { error.value = '请填写密码'; return }
  if (!agreedToTerms.value) { error.value = '请先阅读并同意隐私政策'; return }

  error.value = ''
  loading.value = true

  try {
    if (tab.value === 'login') {
      await userStore.login({ username: form.account, password: form.password })
    } else {
      await userStore.register({
        username: form.account,
        password: form.password,
        email: form.email || form.account + '@qq.com',
        grade: form.grade || '本科三年级',
        major: form.major || '计算机'
      })
    }
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.error || (tab.value === 'login' ? '登录失败' : '注册失败')
  } finally {
    loading.value = false
  }
}

function demoLogin() {
  form.account = 'xiaolin'
  form.password = 'demo123'
  agreedToTerms.value = true
  tab.value = 'login'
  setTimeout(handleSubmit, 100)
}
</script>

<template>
  <div class="min-h-screen flex flex-col items-center justify-center px-4 py-8 bg-surface-paper">
    <!-- Logo -->
    <div class="flex flex-col items-center mb-8">
      <div class="w-14 h-14 rounded-2xl flex items-center justify-center mb-3" style="background-color:#2563EB">
        <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
        </svg>
      </div>
      <h1 class="text-xl font-bold text-ink-800 mt-1">AI论文助手</h1>
      <p class="text-sm text-ink-400 mt-1">读完文献、排好格式、改掉重复</p>
    </div>

    <!-- Main Card -->
    <div class="bg-white rounded-2xl shadow-card border border-ink-100 w-full max-w-sm overflow-hidden">

      <!-- Tabs -->
      <div class="flex border-b border-ink-100">
        <button
          v-for="t in ['login','register']"
          :key="t"
          @click="tab = t; error = ''"
          class="flex-1 py-3.5 text-sm font-medium transition-colors"
          :style="{
            color: tab === t ? '#2563EB' : '#9CA3AF',
            borderBottom: tab === t ? '2px solid #2563EB' : '2px solid transparent'
          }"
        >
          {{ t === 'login' ? '登录' : '注册' }}
        </button>
      </div>

      <div class="p-5">
        <!-- Method Switch -->
        <div class="flex gap-2 mb-4">
          <button
            v-for="m in ['phone','email']"
            :key="m"
            @click="method = m"
            class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition-all border"
            :style="{
              borderColor: method === m ? '#2563EB' : '#E5E7EB',
              backgroundColor: method === m ? '#EFF6FF' : 'white',
              color: method === m ? '#2563EB' : '#6B7280',
            }"
          >
            <svg v-if="m === 'phone'" class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
            </svg>
            <svg v-else class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
            {{ m === 'phone' ? '手机号' : '邮箱' }}
          </button>
        </div>

        <!-- Error -->
        <div v-if="error" class="bg-red-50 border border-red-200 text-red-600 text-xs rounded-lg p-3 mb-3">
          {{ error }}
        </div>

        <!-- Form -->
        <div class="space-y-3">
          <div>
            <label class="block text-xs font-medium text-ink-400 mb-1">
              {{ method === 'phone' ? '手机号' : '邮箱' }}
            </label>
            <input
              v-model="form.account"
              :type="method === 'phone' ? 'tel' : 'email'"
              :placeholder="method === 'phone' ? '请输入手机号' : '请输入邮箱地址'"
              class="w-full px-3 py-2.5 rounded-xl border border-ink-200 text-sm text-ink-800 placeholder-ink-300 focus:outline-none focus:border-blue-400 transition-colors"
            />
          </div>

          <div>
            <label class="block text-xs font-medium text-ink-400 mb-1">密码</label>
            <div class="relative">
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                class="w-full px-3 py-2.5 pr-10 rounded-xl border border-ink-200 text-sm text-ink-800 placeholder-ink-300 focus:outline-none focus:border-blue-400 transition-colors"
              />
              <button type="button" @click="showPassword = !showPassword" class="absolute right-3 top-1/2 -translate-y-1/2 text-ink-400">
                <svg v-if="showPassword" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243a9.7 9.7 0 01-4.243-4.243zM15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              </button>
            </div>
          </div>

          <!-- 注册额外字段 -->
          <template v-if="tab === 'register'">
            <div>
              <label class="block text-xs font-medium text-ink-400 mb-1">年级</label>
              <select v-model="form.grade" class="w-full px-3 py-2.5 rounded-xl border border-ink-200 text-sm text-ink-800 focus:outline-none focus:border-blue-400 transition-colors">
                <option value="">请选择年级</option>
                <option v-for="g in gradeOptions" :key="g" :value="g">{{ g }}</option>
              </select>
            </div>
            <div>
              <label class="block text-xs font-medium text-ink-400 mb-1">专业</label>
              <input v-model="form.major" type="text" placeholder="请输入专业" class="w-full px-3 py-2.5 rounded-xl border border-ink-200 text-sm text-ink-800 placeholder-ink-300 focus:outline-none focus:border-blue-400 transition-colors" />
            </div>
          </template>
        </div>

        <!-- 隐私条款 -->
        <div class="flex items-start gap-2 mt-4 mb-5">
          <button
            @click="agreedToTerms = !agreedToTerms"
            class="mt-0.5 w-4 h-4 rounded border-2 flex-shrink-0 flex items-center justify-center transition-all"
            :style="{
              borderColor: agreedToTerms ? '#2563EB' : '#D1D5DB',
              backgroundColor: agreedToTerms ? '#2563EB' : 'white',
            }"
          >
            <span v-if="agreedToTerms" class="text-white text-xs">✓</span>
          </button>
          <p class="text-xs text-ink-400 leading-relaxed">
            我已阅读并同意《隐私政策》和《用户协议》。
            我们承诺不将您的论文内容上传或分享给第三方。
          </p>
        </div>

        <!-- 提交按钮 -->
        <button
          @click="handleSubmit"
          :disabled="loading"
          class="w-full py-3 rounded-xl text-white font-medium transition-all disabled:opacity-50"
          style="background-color: #2563EB"
        >
          {{ loading ? '处理中...' : (tab === 'login' ? '登录' : '注册并登录') }}
        </button>

        <!-- 演示一键登录 -->
        <button
          @click="demoLogin"
          class="w-full mt-3 py-2.5 rounded-xl border border-ink-200 text-sm text-ink-500 hover:border-blue-200 hover:text-blue-600 transition-colors"
        >
          一键体验（演示账号）
        </button>
      </div>
    </div>

    <!-- 底部声明 -->
    <p class="text-xs text-ink-400 mt-4 text-center max-w-xs">
      本产品为AI辅助工具，所有功能均以"建议"形式呈现，最终论文质量由用户负责。
    </p>
  </div>
</template>
