import { createRouter, createWebHistory } from 'vue-router'
import { routes } from 'vue-router/auto-routes'
import { TOKEN_KEY } from '@/utils/request'

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

  if (!token && !isPublicPage) {
    return {
      path: '/my/login',
      query: { redirect: to.fullPath },
    }
  }

  // 不再在「已有 token」时禁止访问登录页：过期/无效 token 仍能打开登录页重新登录，
  // 否则会被重定向到首页，表现为「怎么都登不进去」。

  return true
})

export default router