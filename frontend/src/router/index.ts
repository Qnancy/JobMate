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

  if (token && isPublicPage && to.path !== '/404') {
    return { path: '/' }
  }

  return true
})

export default router