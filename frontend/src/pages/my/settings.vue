<template>
  <van-nav-bar title="设置" left-arrow @click-left="router.back()" />

  <div class="page">
    <van-cell-group inset>
      <van-cell title="深色模式" label="切换光明 / 黑夜模式">
        <template #right-icon>
          <van-switch v-model="isDark" size="22px" @change="onThemeChange" />
        </template>
      </van-cell>
    </van-cell-group>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { applyThemeMode, getStoredThemeMode, type ThemeMode } from '@/utils/theme'

const router = useRouter()
const theme = ref<ThemeMode>(getStoredThemeMode())

const isDark = computed({
  get: () => theme.value === 'dark',
  set: (value: boolean) => {
    theme.value = value ? 'dark' : 'light'
  },
})

function onThemeChange(value: boolean) {
  const mode: ThemeMode = value ? 'dark' : 'light'
  theme.value = mode
  applyThemeMode(mode)
}
</script>

<style scoped>
.page {
  min-height: calc(100vh - 46px);
  padding: 16px 12px 120px;
}
</style>
