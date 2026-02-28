import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueJsx from '@vitejs/plugin-vue-jsx'
import { fileURLToPath, URL } from 'node:url'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import tailwindcss from '@tailwindcss/vite'

// Still kept in case we need other UI Lib
// import { ElementPlusResolver, NaiveUiResolver } from 'unplugin-vue-components/resolvers'
import {VantResolver} from '@vant/auto-import-resolver'
import VueRouter from 'unplugin-vue-router/vite'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const rawApiTarget = env.VITE_API_BASE_URL || 'http://127.0.0.1:8000'
  const apiTarget = rawApiTarget
    .replace(/\/api\/.+$/, '')
    .replace(/\/api\/?$/, '')

  return {
    plugins: [
      VueRouter(),
      tailwindcss(),
      vue(),
      VueJsx(),
      AutoImport({
        resolvers: [VantResolver(), ],
      }),
      Components({
        resolvers: [VantResolver(), ],
      }),
    ],
    base: './',
    server: {
      proxy: {
        '/api': {
          target: apiTarget,
          changeOrigin: true,
        },
      },
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
  }
})

