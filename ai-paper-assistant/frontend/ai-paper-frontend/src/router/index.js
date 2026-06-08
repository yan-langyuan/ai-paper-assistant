import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomePage.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/literature',
    name: 'LiteratureLibrary',
    component: () => import('@/views/LiteratureLibrary.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/literature/import',
    name: 'LiteratureImport',
    component: () => import('@/views/LiteratureImport.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/literature/:id',
    name: 'LiteratureDetail',
    component: () => import('@/views/LiteratureDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/reference',
    name: 'ReferenceFormat',
    component: () => import('@/views/ReferenceFormat.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/paraphrase',
    name: 'ParaphraseInput',
    component: () => import('@/views/ParaphraseInput.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/paraphrase/result',
    name: 'ParaphraseResult',
    component: () => import('@/views/ParaphraseResult.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/UserCenter.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('auth_token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
