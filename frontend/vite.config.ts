import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueJsx from '@vitejs/plugin-vue-jsx'
import { fileURLToPath, URL } from 'node:url'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'

// Still kept in case we need other UI Lib
// import { ElementPlusResolver, NaiveUiResolver } from 'unplugin-vue-components/resolvers'
import {VantResolver} from '@vant/auto-import-resolver'
import VueRouter from 'unplugin-vue-router/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [    
    VueRouter(),
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
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})

