<template>
    <main class="pb-4">
      <div class="bg-gradient-to-r from-sky-500 to-blue-600 text-white px-4 py-6">
        <h2 class="text-xl font-bold">{{ job.title }}</h2>
        <p class="text-sky-100 mt-1">{{ job.company }}</p>
        <div class="flex flex-wrap gap-2 mt-3">
          <span class="px-3 py-1 bg-white/20 rounded-full text-sm">{{ job.location }}</span>
          <span class="px-3 py-1 bg-white/20 rounded-full text-sm">{{ job.type }}</span>
        </div>
        <p class="text-2xl font-bold mt-4 text-yellow-300">{{ job.salary }}</p>
      </div>

      <div class="p-4 space-y-4">
        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            职位描述
          </h3>
          <div class="text-gray-600 text-sm leading-relaxed" v-html="job.fullDescription"></div>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            任职要求
          </h3>
          <div class="text-gray-600 text-sm leading-relaxed" v-html="job.requirements"></div>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            公司信息
          </h3>
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 bg-sky-100 rounded-lg flex items-center justify-center">
              <span class="text-sky-600 font-bold text-lg">{{ job.company.charAt(0) }}</span>
            </div>
            <div>
              <p class="font-medium text-gray-800">{{ job.company }}</p>
              <p class="text-sm text-gray-500">{{ job.companyType }} · {{ job.companySize }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作栏 -->
      <div class="bg-white border-t border-gray-100 p-4 flex gap-3 mt-4">
        <button 
          @click="toggleFavorite('job', job.id)"
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
          立即投递
        </button>
      </div>
    </main>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { fetchJobDetail,fetchIsJobFavorite } from '@/utils/mockData';
import { useRoute } from 'vue-router';

// const props = defineProps({
//   params: {
//     type: Object,
//     required: true
//   }
// });
const route = useRoute();


const fair = ref(null);
const isLoading = ref(true);

console.log(route.params.id);


const job = await fetchJobDetail(route.params.id);
console.log(job);
const isFavorite = ref(await fetchIsJobFavorite(route.params.id));


onMounted(async () => {

});
</script>

<style scoped>
/* 继承 JobDetail 的部分样式 */
.detail-page {
  padding: 0 15px 15px 15px;
  background-color: var(--color-text-light);
  min-height: 100vh;
}
.header-bar {
    display: flex;
    align-items: center;
    padding: 15px 0;
    margin-bottom: 10px;
    border-bottom: 1px solid #eee;
}
.header-bar h2 {
    margin: 0;
    font-size: 1.2rem;
    color: var(--color-primary-dark);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.back-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 5px;
    margin-right: 10px;
}
.back-btn svg {
    width: 24px;
    height: 24px;
    stroke: var(--color-primary-dark);
}
.loading-state {
    text-align: center;
    padding: 50px;
    color: #999;
}

.detail-container {
    padding-top: 10px;
}

.main-title {
    font-size: 1.8rem;
    color: var(--color-primary-dark);
    margin-bottom: 10px;
}

.meta-info {
    margin-bottom: 20px;
}
.date-tag, .venue-tag {
    display: inline-block;
    padding: 5px 10px;
    border-radius: 5px;
    margin-right: 10px;
    font-size: 0.9rem;
    font-weight: 500;
}
.date-tag {
    background-color: #e0f2fe;
    color: var(--color-primary-dark);
}
.venue-tag {
    background-color: #fae8ff; /* 浅紫色背景 */
    color: #9333ea;
}

.detail-content {
    line-height: 1.6;
    color: var(--color-text-dark);
}

.detail-content :deep(p) {
    margin-bottom: 10px;
}
.detail-content :deep(strong) {
    color: var(--color-primary-dark);
}

.register-btn {
    width: 100%;
    padding: 15px;
    background-color: var(--color-primary-dark);
    color: var(--color-text-light);
    border: none;
    border-radius: 8px;
    font-size: 1.1rem;
    font-weight: 600;
    margin-top: 30px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.register-btn:hover {
    background-color: var(--color-primary-light);
}
</style>