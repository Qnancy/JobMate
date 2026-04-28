<template>
    <main class="pb-4">
      <div class="bg-gradient-to-r from-sky-500 to-blue-600 text-white px-4 py-6">
        <div class="mb-3">
          <button
            type="button"
            class="inline-flex items-center gap-1 text-white/90 hover:text-white active:opacity-80 transition"
            aria-label="返回"
            @click="goBack"
          >
            <svg viewBox="0 0 24 24" class="w-5 h-5" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
            </svg>
            <span class="text-sm">返回</span>
          </button>
        </div>
        <div class="flex flex-col items-start gap-3">
          <span
            :class="[
              'shrink-0 px-3 py-1 text-xs font-medium rounded-full leading-none',
              selectedFair.status === '报名中'
                ? 'bg-green-400 text-green-900'
                : selectedFair.status === '即将开始'
                  ? 'bg-orange-400 text-orange-900'
                  : 'bg-gray-400 text-gray-900',
            ]"
          >
            {{ selectedFair.status }}
          </span>
          <h2 class="text-xl font-bold w-full">{{ selectedFair.title }}</h2>
          <div class="w-full space-y-2 text-sm text-sky-100">
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
            </svg>
            {{ selectedFair.date }}
          </div>
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
            </svg>
            {{ selectedFair.location }}
          </div>
          </div>
        </div>
      </div>

      <div class="p-4 space-y-4">
        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            招聘会简介
          </h3>
          <div class="text-gray-600 text-sm leading-relaxed" v-html="selectedFair.description"></div>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            参会须知
          </h3>
          <div class="text-gray-600 text-sm leading-relaxed" v-html="selectedFair.notice"></div>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            参展企业 ({{ selectedFair.companies }}家)
          </h3>
          <div class="flex flex-wrap gap-2">
            <span 
              v-for="company in selectedFair.companyList" 
              :key="company"
              class="px-3 py-1.5 bg-sky-50 text-sky-700 text-sm rounded-lg"
            >
              {{ company }}
            </span>
          </div>
        </div>
      </div>

      <!-- 操作栏 -->
      <div class="bg-white border-t border-gray-100 p-4 flex gap-3 mt-4">
        <button 
          @click="toggleFavorite('fair', selectedFair.id)"
          class="flex-1 py-3 border-2 border-sky-500 text-sky-500 rounded-xl font-medium flex items-center justify-center gap-2"
        >
          <svg 
            class="w-5 h-5" 
            :class="isFavorite ? 'fill-sky-500' : ''"
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
          </svg>
          {{ isFavorite ? '已收藏' : '收藏' }}
        </button>
        <button class="flex-[2] py-3 bg-gradient-to-r from-sky-500 to-blue-600 text-white rounded-xl font-bold">
          立即报名
        </button>
      </div>
    </main>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast } from 'vant';
import { getActivityById } from '@/services/activity';
import { isSuccessResponse } from '@/utils/request';
import { favoriteStore } from '@/utils/favoriteStore';
import { navigateBackPreferFrom } from '@/utils/returnNavigation';

const route = useRoute();
const router = useRouter();

function goBack() {
  navigateBackPreferFrom(router, route, { path: '/info', query: { tab: 'event' } });
}

type FairDetailView = {
  id: number;
  title: string;
  status: string;
  date: string;
  location: string;
  description: string;
  notice: string;
  companies: number;
  companyList: string[];
};

const selectedFair = ref<FairDetailView>({
  id: 0,
  title: '',
  status: '',
  date: '',
  location: '',
  description: '',
  notice: '',
  companies: 0,
  companyList: []
});

const isLoading = ref(true);
const isFavorite = computed(() => favoriteStore.isFavorited('ACTIVITY', selectedFair.value.id));

function getActivityStatus(time?: string | null) {
  if (!time) return '状态未知';
  const date = new Date(time.replace(' ', 'T'));
  if (Number.isNaN(date.getTime())) return '状态未知';
  return date.getTime() > Date.now() ? '即将开始' : '已结束';
}

async function load() {
  isLoading.value = true;
  const id = Number(route.params.id);
  if (!id) {
    isLoading.value = false;
    return;
  }

  try {
    const res = await getActivityById(id);
    if (!isSuccessResponse(res) || !res.data) {
      showToast(res.message || '获取活动详情失败');
      isLoading.value = false;
      return;
    }

    const item = res.data;
    selectedFair.value = {
      id: item.id,
      title: item.title,
      status: getActivityStatus(item.time),
      date: item.time || '时间待定',
      location: item.location || '地点待定',
      description: item.extra || '暂无活动简介',
      notice: item.link ? `报名链接：${item.link}` : '暂无报名链接',
      companies: item.company?.name ? 1 : 0,
      companyList: item.company?.name ? [item.company.name] : [],
    };
  } catch {
    showToast('获取活动详情失败');
  }

  // 确认后端侧的收藏状态（登录时）
  if (selectedFair.value.id) {
    favoriteStore.syncForIds('ACTIVITY', [selectedFair.value.id]);
  }
  isLoading.value = false;
}

function toggleFavorite(type: 'fair', id: number) {
  if (!id || type !== 'fair') return;
  favoriteStore.toggle('ACTIVITY', Number(id)).catch(() => {
    showToast('收藏操作失败');
  });
}

onMounted(load);

</script>