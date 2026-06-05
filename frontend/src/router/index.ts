import { createRouter, createWebHistory } from 'vue-router'
import { routes } from 'vue-router/auto-routes'
import { TOKEN_KEY } from '@/utils/request'
import * as auth from '@/services/auth'

const router = createRouter({
  history: createWebHistory(),
  routes:[
    ...routes,
    { path: '/:pathMatch(.*)*', redirect: '/404' }
  ]
})

const PUBLIC_PATHS = new Set([
  '/my/login',
  '/my/register',
  '/my/login-admin',
  '/my/register-admin',
  '/404',
])

router.beforeEach((to) => {
  const token = localStorage.getItem(TOKEN_KEY)
  const isPublicPage = PUBLIC_PATHS.has(to.path)
  const isAdminSection = to.path === '/admin' || to.path.startsWith('/admin/')

  if (!token && !isPublicPage) {
    // 未登录访问后台：去管理员登录页并带上回跳，避免被误送到普通用户登录
    if (isAdminSection) {
      return { path: '/my/login-admin', query: { redirect: to.fullPath } }
    }
    return { path: '/my/login' }
  }

  if (isAdminSection && token) {
    const u = auth.currentUser()
    if (!u) {
      return { path: '/my/login-admin', query: { redirect: to.fullPath } }
    }
    if (u.role !== 'ADMIN') {
      return { path: '/' }
    }
  }

  // 不再在「已有 token」时禁止访问登录页：过期/无效 token 仍能打开登录页重新登录，
  // 否则会被重定向到首页，表现为「怎么都登不进去」。

  return true
})

export default router