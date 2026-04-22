<script setup lang="ts">
// import HelloWorld from './components/HelloWorld.vue'
import { RouterView } from "vue-router";
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const hideTabbar = computed(() => {
  const authPages = new Set([
    "/my/login",
    "/my/register",
    "/my/login-admin",
    "/my/register-admin",
  ]);
  return authPages.has(route.path);
});
</script>

<template>
  <Suspense>
    <RouterView />

  <template #fallback>
    Loading...
  </template>
</Suspense>

  <van-tabbar
    v-if="!hideTabbar"
    route
    class="glass-tabbar"
    :fixed="false"
    :border="false"
    safe-area-inset-bottom
  >
    <van-tabbar-item to="/" icon="home-o">主页</van-tabbar-item>
    <van-tabbar-item to="/my" icon="friends-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>
.glass-tabbar {
  position: fixed;
  left: 50%;
  bottom: 20px;
  transform: translateX(-50%);
  width: min(420px, calc(100% - 32px));
  padding: 8px;
  border-radius: 999px;
  background: var(--tabbar-bg);
  border: 1px solid var(--tabbar-border);
  box-shadow: var(--tabbar-shadow);
  backdrop-filter: blur(28px);
  -webkit-backdrop-filter: blur(28px);
  z-index: 100;
}

.glass-tabbar :deep(.van-tabbar-item) {
  border-radius: 999px;
  background: transparent;
  color: var(--tabbar-text);
  font-weight: 600;
}

.glass-tabbar :deep(.van-tabbar-item--active) {
  background: var(--tabbar-active-bg);
  box-shadow: var(--tabbar-active-shadow);
  color: var(--tabbar-text);
}

.glass-tabbar :deep(.van-icon) {
  font-size: 20px;
}
</style>
