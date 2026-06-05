<template>
  <div class="min-h-screen bg-gradient-to-b from-sky-50 to-white pb-8">
    <header
      class="sticky top-0 z-10 bg-white/85 backdrop-blur-md border-b border-sky-100 px-3 py-3 flex items-center gap-2 shadow-sm"
    >
      <button
        type="button"
        class="inline-flex items-center gap-1 text-sky-600 text-sm font-medium px-1 py-1 rounded-lg active:bg-sky-50"
        @click="goBack"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        返回
      </button>
      <h1 class="text-[17px] font-bold text-gray-800 flex-1 text-center pr-16">合作企业</h1>
    </header>

    <div class="px-3 pt-2">
      <van-search
        v-model="keyword"
        shape="round"
        placeholder="搜索企业名称"
        @search="load"
        @clear="load"
      />
    </div>

    <main class="px-3 space-y-3">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <div v-if="loading" class="py-12 text-center text-sm text-gray-400">加载中…</div>
        <template v-else>
          <router-link
            v-for="c in list"
            :key="c.id"
            :to="`/info/company/${c.id}`"
            class="group block bg-white rounded-xl p-4 shadow-md border border-sky-50 hover:shadow-lg transition active:bg-sky-50/50"
          >
            <div class="min-w-0">
              <p class="font-semibold text-gray-900 text-[15px] leading-snug">{{ c.name }}</p>
              <p class="text-xs text-gray-500 mt-1">{{ labelCompanyType(c.type) }}</p>
              <p
                v-if="snippet(c.description)"
                class="text-xs text-gray-400 mt-2 line-clamp-2"
              >{{ snippet(c.description) }}</p>
            </div>
            <div class="flex justify-between items-center mt-3 pt-3 border-t border-gray-100">
              <span />
              <span :class="DETAIL_VIEW_CTA_GROUP_CLASS">查看详情 →</span>
            </div>
          </router-link>
          <van-empty v-if="list.length === 0" description="暂无企业" />
        </template>
      </van-pull-refresh>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { showFailToast } from "vant";
import { searchCompany, labelCompanyType, type Company } from "@/services/company";
import { isSuccessResponse } from "@/utils/request";
import { DETAIL_VIEW_CTA_GROUP_CLASS } from "@/constants/detailViewCta";

const router = useRouter();

const keyword = ref("");
const list = ref<Company[]>([]);
const loading = ref(true);
const refreshing = ref(false);

function snippet(s: string | null | undefined) {
  if (s == null) return "";
  const t = String(s).trim();
  return t || "";
}

function goBack() {
  if (typeof window !== "undefined" && window.history.length > 1) {
    router.back();
    return;
  }
  router.replace("/");
}

async function load() {
  loading.value = true;
  try {
    const res = await searchCompany({
      page: 1,
      page_size: 100,
      ...(keyword.value.trim() ? { keyword: keyword.value.trim() } : {}),
    });
    if (!isSuccessResponse(res)) {
      showFailToast(res.message || "加载失败");
      list.value = [];
      return;
    }
    list.value = res.data?.content ?? [];
  } catch {
    showFailToast("加载失败");
    list.value = [];
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
}

function onRefresh() {
  refreshing.value = true;
  load();
}

onMounted(() => {
  load();
});
</script>
