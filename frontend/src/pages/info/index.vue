<template>
  <van-search
    v-model="searchValue"
    placeholder="搜索职位或活动"
    @search="$router.push({
      'path':'/info/search',
      'query':{
        'keywords': searchValue
      }
    })"
  />
  <div class="min-h-screen bg-gradient-to-b from-sky-50 to-white">
  <van-tabs v-model:active="activeName" class="glass-tabs" >
    <van-tab title="职位" name="job">
         <main class="p-4">
      <!-- 筛选标签 -->
      <div class="flex gap-2 overflow-x-auto pb-3 mb-4 scrollbar-hide">
        <button 
          v-for="tag in jobTags" 
          :key="tag"
          @click="selectedJobTag = tag"
          :class="[
            'px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition',
            selectedJobTag === tag 
              ? 'bg-sky-500 text-white' 
              : 'bg-white text-gray-600 border border-gray-200'
          ]"
        >
          {{ tag }}
        </button>
      </div>

      <!-- 职位卡片列表 -->
      <div class="space-y-3">
        <div 
          v-for="job in filteredJobs" 
          :key="job.id"
          class="bg-white rounded-xl p-4 shadow-md border border-sky-50 hover:shadow-lg transition cursor-pointer"
          @click="viewJobDetail(job)"
        >
          <div class="flex justify-between items-start">
            <div class="flex-1">
              <h3 class="font-bold text-gray-800 text-lg">{{ job.title }}</h3>
              <p class="text-sky-600 font-medium mt-1">{{ job.company }}</p>
              <div class="flex flex-wrap gap-2 mt-2">
                <span class="px-2 py-0.5 bg-sky-50 text-sky-600 text-xs rounded">{{ job.location }}</span>
                <span class="px-2 py-0.5 bg-blue-50 text-blue-600 text-xs rounded">{{ job.type }}</span>
                <span class="px-2 py-0.5 bg-green-50 text-green-600 text-xs rounded">{{ job.salary }}</span>
              </div>
              <p class="text-gray-500 text-sm mt-2 line-clamp-2">{{ job.description }}</p>
            </div>
            <button 
              @click.stop="toggleFavorite('job', job.id)"
              class="p-2 ml-2"
            >
              <svg 
                class="w-6 h-6 transition-transform duration-150" 
                :class="[
                  favorites.jobs.includes(job.id) ? 'text-yellow-500 fill-yellow-500' : 'text-gray-300',
                  animatingJobs.has(job.id) ? 'scale-125' : ''
                ]"
                fill="none" 
                stroke="currentColor" 
                viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
              </svg>
            </button>
          </div>
          <div class="flex justify-between items-center mt-3 pt-3 border-t border-gray-100">
            <span class="text-xs text-gray-400">{{ job.publishDate }}</span>
            <button 
              @click.stop="viewJobDetail(job)"
              class="text-sky-500 text-sm font-medium hover:text-sky-600"
            >
              查看详情 →
            </button>
          </div>
        </div>
      </div>
    </main>
    </van-tab>
    <van-tab title="活动" name="event">
          <main  class="p-4">
      <!-- 招聘会卡片列表 -->
      <div class="space-y-4">
        <div 
          v-for="fair in fairs" 
          :key="fair.id"
          class="bg-white rounded-xl overflow-hidden shadow-md border border-sky-50 hover:shadow-lg transition"
        >
          <div class="h-2 bg-white"></div>
          <div class="p-4">
            <div class="flex justify-between items-start">
              <div class="flex-1" @click="viewFairDetail(fair)">
                <div class="flex items-center gap-2">
                  <span 
                    :class="[
                      'px-2 py-0.5 text-xs font-medium rounded',
                      fair.status === '报名中' ? 'bg-green-100 text-green-600' : 
                      fair.status === '即将开始' ? 'bg-orange-100 text-orange-600' : 
                      'bg-gray-100 text-gray-600'
                    ]"
                  >
                    {{ fair.status }}
                  </span>
                  <span class="text-xs text-gray-400">{{ fair.type }}</span>
                </div>
                <h3 class="font-bold text-gray-800 text-lg mt-2">{{ fair.title }}</h3>
                <div class="mt-3 space-y-1.5 text-sm text-gray-600">
                  <div class="flex items-center gap-2">
                    <svg class="w-4 h-4 text-sky-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                    </svg>
                    {{ fair.date }}
                  </div>
                  <div class="flex items-center gap-2">
                    <svg class="w-4 h-4 text-sky-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                    </svg>
                    {{ fair.location }}
                  </div>
                  <div class="flex items-center gap-2">
                    <svg class="w-4 h-4 text-sky-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                    </svg>
                    参展企业 {{ fair.companies }} 家
                  </div>
                </div>
              </div>
              <button 
                @click.stop="toggleFavorite('fair', fair.id)"
                class="p-2"
              >
                <svg 
                  class="w-6 h-6 transition-transform duration-150" 
                  :class="[
                    favorites.fairs.includes(fair.id) ? 'text-yellow-500 fill-yellow-500' : 'text-gray-300',
                    animatingFairs.has(fair.id) ? 'scale-125' : ''
                  ]"
                  fill="none" 
                  stroke="currentColor" 
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                </svg>
              </button>
            </div>
            <div class="flex justify-between items-center mt-3 pt-3 border-t border-gray-100">
              <span></span>
            <button 
              @click="$router.push({ path: `/info/activity/${fair.id}` })"              
              class="text-sky-500 text-sm font-medium hover:text-sky-600"
            >
              查看详情 →
            </button>
            </div>
          </div>
        </div>
      </div>
    </main>
    </van-tab>
  </van-tabs>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { fetchFairs, fetchFavouriteFairIds, fetchFavouriteJobIds, fetchJobTags,fetchJobs } from "@/utils/mockData";
import { subscribe, unsubscribe } from "@/services/subscription";

const router = useRouter();

const searchValue = ref("");
const activeName = ref("job");


const jobTags = await fetchJobTags();
const selectedJobTag = ref("全部");

const favorites = ref({
  jobs: [],
  fairs: []
});

const animatingJobs = ref(new Set());
const animatingFairs = ref(new Set());

const jobs = await fetchJobs();
const fairs = await fetchFairs();

const filteredJobs = computed(() => {
  if (selectedJobTag.value === "全部") {
    return jobs;
  }
  return jobs.filter(job => job.category === selectedJobTag.value);
});


favorites.value.jobs = await fetchFavouriteJobIds()
favorites.value.fairs = await fetchFavouriteFairIds()

function viewJobDetail(job) {
  router.push({ path: `/info/job/${job.id}` });
}

function viewFairDetail(fair) {
  router.push({ path: `/info/activity/${fair.id}` });
}

function toggleFavorite(type, id) {
  const numId = Number(id);
  const set = type === 'job' ? favorites.value.jobs : favorites.value.fairs;
  const animSet = type === 'job' ? animatingJobs : animatingFairs;
  const isFav = set.includes(numId);

  // 触发动画
  animSet.value.add(numId);
  setTimeout(() => animSet.value.delete(numId), 180);

  // 本地切换
  if (isFav) {
    const idx = set.indexOf(numId);
    if (idx !== -1) set.splice(idx, 1);
    // 后端取消订阅（占位）
    unsubscribe({ type: type === 'job' ? 'job' : 'activity', id: numId }).catch(() => {});
  } else {
    set.push(numId);
    // 后端订阅（占位）
    subscribe({ type: type === 'job' ? 'job' : 'activity', id: numId }).catch(() => {});
  }
}
</script>

<style scoped>

</style>
