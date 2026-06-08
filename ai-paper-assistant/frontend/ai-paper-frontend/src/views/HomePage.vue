<script setup>
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useLiteratureStore } from '@/stores/literature'
import { useUserStore } from '@/stores/user'
import LoadingSkeleton from '@/components/LoadingSkeleton.vue'

const router = useRouter()
const literatureStore = useLiteratureStore()
const userStore = useUserStore()

const featureCards = [
  {
    title: '智能文献助手',
    description: '上传文献，自动生成结构化摘要',
    path: '/literature',
    comingSoon: false
  },
  {
    title: '格式排版助手',
    description: '参考文献一键格式化，支持 GB/T 7714 / APA / MLA',
    path: '/reference',
    comingSoon: false
  },
  {
    title: '降重改写助手',
    description: '粘贴段落，检测重复，逐句获得改写建议',
    path: '/paraphrase',
    comingSoon: false
  },
  {
    title: '答辩PPT助手',
    description: '论文转PPT大纲',
    path: '',
    comingSoon: true
  }
]

const recentItems = computed(() => literatureStore.lastThree)

function navigate(path) {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  router.push(path)
}

onMounted(() => {
  if (userStore.isLoggedIn && literatureStore.list.length === 0) {
    literatureStore.fetchList()
  }
})
</script>

<template>
  <div class="min-h-screen bg-paper-texture">
    <div class="max-w-5xl mx-auto px-5 sm:px-8 py-12 sm:py-20">

      <!-- Hero — 编辑风格大标题 -->
      <header class="mb-16 sm:mb-24">
        <p class="text-ink-400 text-sm tracking-wide mb-4 font-mono uppercase">
          AI PAPER ASSISTANT
        </p>
        <h1 class="font-display text-4xl sm:text-5xl text-ink-800 leading-tight text-balance">
          &#20889;&#35770;&#25991;&#30340;&#27599;&#19968;&#27493;&#65292;<br>
          <span class="text-amber">&#37117;&#26377;&#20154;&#38506;&#30528;&#20320;</span>
        </h1>
        <p class="mt-5 text-ink-500 text-lg max-w-lg leading-relaxed">
          &#35835;&#25991;&#29486;&#12289;&#25490;&#26684;&#24335;&#12289;&#25913;&#37325;&#22797;&#8212;&#8212;&#21482;&#20570;&#19977;&#20214;&#20107;&#65292;&#20570;&#21040;&#26080;&#21487;&#26367;&#20195;
        </p>
      </header>

      <!-- 功能卡片 — 非对称 2x2 -->
      <section class="mb-16 sm:mb-24">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 sm:gap-5">
          <div
            v-for="(card, index) in featureCards"
            :key="index"
            @click="!card.comingSoon && navigate(card.path)"
            class="group relative rounded-xl p-6 sm:p-8 transition-all duration-300"
            :class="card.comingSoon
              ? 'bg-surface-card/60 cursor-not-allowed'
              : 'card-hover cursor-pointer'"
          >
            <!-- 即将上线标签 -->
            <span
              v-if="card.comingSoon"
              class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-ink-100 text-ink-500 mb-4"
            >
              &#21363;&#23558;&#19978;&#32447;
            </span>

            <h3
              class="text-xl font-display text-ink-800 mb-2 transition-colors"
              :class="{ 'group-hover:text-amber': !card.comingSoon }"
            >
              {{ card.title }}
            </h3>
            <p
              class="text-sm leading-relaxed"
              :class="card.comingSoon ? 'text-ink-400' : 'text-ink-500'"
            >
              {{ card.description }}
            </p>

            <!-- hover 时的小箭头 -->
            <span
              v-if="!card.comingSoon"
              class="inline-block mt-4 text-ink-300 group-hover:text-amber group-hover:translate-x-1 transition-all duration-200 text-sm"
            >
              &#36827;&#20837; &rarr;
            </span>
          </div>
        </div>
      </section>

      <!-- 最近文献 -->
      <section>
        <div class="flex items-end justify-between mb-6">
          <div>
            <p class="text-ink-400 text-xs tracking-wide uppercase mb-1 font-mono">RECENT</p>
            <h2 class="font-display text-2xl text-ink-800">&#26368;&#36817;&#25991;&#29486;</h2>
          </div>
          <router-link
            v-if="userStore.isLoggedIn && literatureStore.list.length > 0"
            to="/literature"
            class="text-sm text-amber hover:text-amber-dark transition-colors"
          >
            &#26597;&#30475;&#20840;&#37096; &rarr;
          </router-link>
        </div>

        <!-- 加载态 -->
        <LoadingSkeleton v-if="literatureStore.loading" type="list" :count="3" />

        <!-- 未登录 -->
        <div
          v-else-if="!userStore.isLoggedIn"
          class="rounded-xl bg-surface-card p-10 text-center"
        >
          <p class="text-ink-500 mb-4">&#30331;&#24405;&#21518;&#21487;&#26597;&#30475;&#24050;&#19978;&#20256;&#25991;&#29486;</p>
          <router-link to="/login" class="btn-primary inline-block">&#30331;&#24405;</router-link>
        </div>

        <!-- 空状态 -->
        <div
          v-else-if="literatureStore.list.length === 0"
          class="rounded-xl bg-surface-card p-10 text-center"
        >
          <p class="text-ink-500 mb-4">&#26242;&#26080;&#25991;&#29486;&#65292;&#24320;&#22987;&#19978;&#20256;&#20320;&#30340;&#31532;&#19968;&#31687;&#35770;&#25991;&#21543;</p>
          <router-link to="/literature/import" class="btn-primary inline-block">&#23548;&#20837;&#25991;&#29486;</router-link>
        </div>

        <!-- 文献列表 -->
        <div v-else class="space-y-2">
          <div
            v-for="item in recentItems"
            :key="item.id"
            @click="navigate(`/literature/${item.id}`)"
            class="group card-hover px-5 py-4 flex items-center justify-between"
          >
            <div class="min-w-0">
              <h4 class="text-sm font-medium text-ink-800 truncate group-hover:text-amber transition-colors">
                {{ item.title }}
              </h4>
              <p class="text-xs text-ink-400 mt-1">
                {{ item.authors || '未知作者' }}
                <span v-if="item.year"> &middot; {{ item.year }}</span>
              </p>
            </div>
            <span class="ml-4 text-ink-300 group-hover:text-amber group-hover:translate-x-1 transition-all shrink-0">
              &rarr;
            </span>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>
