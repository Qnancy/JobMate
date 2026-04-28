<script setup lang="ts">
// import HelloWorld from './components/HelloWorld.vue'
import { RouterView } from "vue-router";
import { computed, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import * as auth from "@/services/auth";

const route = useRoute();
const router = useRouter();

/** 登录/注册相关页隐藏底栏 */
const hideTabbar = computed(() => {
  const authPages = new Set([
    "/my/login",
    "/my/register",
    "/my/login-admin",
    "/my/register-admin",
  ]);
  return authPages.has(route.path);
});

/** 当前用户为管理员：底栏仅「主页 + 管理员后台」，不展示「我的」 */
const showAdminTab = computed(() => {
  route.fullPath;
  return auth.currentUser()?.role === "ADMIN";
});

type TabName = "home" | "admin" | "my";
const activeTab = ref<TabName>("home");

function syncTabFromRoute() {
  if (hideTabbar.value) return;
  const p = route.path;
  if (p.startsWith("/admin")) {
    activeTab.value = "admin";
  } else if (p.startsWith("/my")) {
    // 管理员账号没有「我的」底栏项；若仍访问 /my/*，高亮落在「主页」
    activeTab.value = showAdminTab.value ? "home" : "my";
  } else {
    activeTab.value = "home";
  }
}

watch(
  () => route.fullPath,
  () => {
    syncTabFromRoute();
  },
  { immediate: true },
);

watch(hideTabbar, (hidden: boolean) => {
  if (!hidden) syncTabFromRoute();
});

watch(showAdminTab, (show: boolean) => {
  if (show && activeTab.value === "my") {
    activeTab.value = "home";
  }
  if (!show && activeTab.value === "admin") {
    activeTab.value = "home";
  }
  if (!hideTabbar.value) syncTabFromRoute();
});

function onTabChange(name: string | number) {
  const n = String(name) as TabName;
  if (n === "home") router.push("/");
  else if (n === "admin") router.push("/admin");
  else if (n === "my") router.push("/my");
}
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
    v-model="activeTab"
    class="glass-tabbar glass-tabbar--two"
    :fixed="false"
    :border="false"
    safe-area-inset-bottom
    @change="onTabChange"
  >
    <van-tabbar-item name="home" icon="home-o">主页</van-tabbar-item>
    <van-tabbar-item v-if="showAdminTab" name="admin" icon="apps-o">管理员后台</van-tabbar-item>
    <van-tabbar-item v-else name="my" icon="friends-o">我的</van-tabbar-item>
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

.glass-tabbar--two {
  width: min(420px, calc(100% - 32px));
  padding: 8px;
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
