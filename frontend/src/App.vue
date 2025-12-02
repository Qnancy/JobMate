<script setup lang="ts">
// import HelloWorld from './components/HelloWorld.vue'
import { RouterView } from "vue-router";
</script>

<template>
  <Suspense>
    <RouterView />

  <template #fallback>
    Loading...
  </template>
</Suspense>

  <van-tabbar
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
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 15px 45px rgba(15, 23, 42, 0.25), inset 0 1px 0 rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(28px);
  -webkit-backdrop-filter: blur(28px);
  z-index: 100;
}

.glass-tabbar :deep(.van-tabbar-item) {
  border-radius: 999px;
  background: transparent;
  color: #0f172a;
  font-weight: 600;
}

.glass-tabbar :deep(.van-tabbar-item--active) {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.25);
  color: #0f172a;
}

.glass-tabbar :deep(.van-icon) {
  font-size: 20px;
}
</style>
