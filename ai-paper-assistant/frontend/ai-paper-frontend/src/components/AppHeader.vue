<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const mobileMenuOpen = ref(false)
const userMenuOpen = ref(false)

const navLinks = [
  { label: '首页', path: '/' },
  { label: '文献库', path: '/literature' },
  { label: '格式排版', path: '/reference' },
  { label: '降重改写', path: '/paraphrase' }
]

const isActive = (path) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

function handleLogout() {
  userStore.logout()
  userMenuOpen.value = false
  router.push('/')
}

function goToLogin() {
  router.push('/login')
}

function goToUserCenter() {
  userMenuOpen.value = false
  router.push('/user')
}

function closeMenus() {
  mobileMenuOpen.value = false
  userMenuOpen.value = false
}
</script>

<template>
  <header class="sticky top-0 z-50 bg-surface-paper/90 backdrop-blur-md border-b border-ink-100">
    <div class="max-w-6xl mx-auto px-5 sm:px-8">
      <div class="flex items-center justify-between h-16">

        <!-- Logo — 编辑风格：衬线字标 -->
        <router-link to="/" class="flex items-center gap-2 group">
          <span class="font-display text-xl text-ink-800 tracking-tight">
            AI&#35770;&#25991;&#21161;&#25163;
          </span>
          <span class="hidden sm:inline-block w-1 h-1 rounded-full bg-amber"></span>
        </router-link>

        <!-- 桌面导航 -->
        <nav class="hidden md:flex items-center gap-1">
          <router-link
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            class="relative px-3 py-2 text-sm font-medium rounded-md transition-colors duration-200"
            :class="isActive(link.path)
              ? 'text-amber'
              : 'text-ink-500 hover:text-ink-800 hover:bg-ink-50'"
          >
            {{ link.label }}
            <span
              v-if="isActive(link.path)"
              class="absolute bottom-0 left-3 right-3 h-0.5 bg-amber rounded-full"
            ></span>
          </router-link>
        </nav>

        <!-- 用户区域 -->
        <div class="flex items-center gap-3">
          <template v-if="userStore.isLoggedIn">
            <div class="relative">
              <button
                @click="userMenuOpen = !userMenuOpen"
                class="flex items-center gap-2 text-sm font-medium text-ink-700 hover:text-ink-900 transition-colors"
              >
                <span class="w-8 h-8 rounded-full bg-amber text-white flex items-center justify-center text-xs font-medium">
                  {{ userStore.username.charAt(0).toUpperCase() }}
                </span>
                <span class="hidden sm:inline">{{ userStore.username }}</span>
                <svg class="w-3.5 h-3.5 text-ink-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              <Transition name="fade">
                <div
                  v-if="userMenuOpen"
                  class="absolute right-0 mt-2 w-44 bg-white rounded-xl shadow-modal border border-ink-100 py-1.5 z-50"
                >
                  <button
                    @click="goToUserCenter"
                    class="w-full text-left px-4 py-2 text-sm text-ink-600 hover:bg-ink-50 hover:text-ink-800 transition-colors"
                  >
                    &#20010;&#20154;&#20013;&#24515;
                  </button>
                  <div class="border-t border-ink-100 my-1"></div>
                  <button
                    @click="handleLogout"
                    class="w-full text-left px-4 py-2 text-sm text-ink-500 hover:bg-ink-50 hover:text-risk-high transition-colors"
                  >
                    &#36864;&#20986;&#30331;&#24405;
                  </button>
                </div>
              </Transition>
            </div>
          </template>

          <button v-else @click="goToLogin" class="btn-primary text-sm">
            &#30331;&#24405;
          </button>

          <!-- 移动端菜单按钮 -->
          <button
            @click="mobileMenuOpen = !mobileMenuOpen"
            class="md:hidden p-2 rounded-lg text-ink-500 hover:text-ink-800 hover:bg-ink-100 transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                v-if="!mobileMenuOpen"
                stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M4 6h16M4 12h16M4 18h16"
              />
              <path
                v-else
                stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>
      </div>

      <!-- 移动端导航 -->
      <Transition name="fade">
        <div v-if="mobileMenuOpen" class="md:hidden pb-4 border-t border-ink-100">
          <nav class="mt-3 space-y-0.5">
            <router-link
              v-for="link in navLinks"
              :key="link.path"
              :to="link.path"
              @click="closeMenus"
              class="block px-3 py-2.5 rounded-lg text-sm font-medium transition-colors"
              :class="isActive(link.path)
                ? 'text-amber bg-amber/5'
                : 'text-ink-500 hover:text-ink-800 hover:bg-ink-50'"
            >
              {{ link.label }}
            </router-link>
          </nav>
        </div>
      </Transition>
    </div>
  </header>
</template>
