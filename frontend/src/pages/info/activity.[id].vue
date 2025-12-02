<template>
    <main class="pb-24">
      <div class="bg-gradient-to-r from-blue-600 to-blue-800 text-white px-4 py-6">
        <span 
          :class="[
            'px-3 py-1 text-xs font-medium rounded-full',
            selectedFair.status === '报名中' ? 'bg-green-400 text-green-900' : 
            selectedFair.status === '即将开始' ? 'bg-orange-400 text-orange-900' : 
            'bg-gray-400 text-gray-900'
          ]"
        >
          {{ selectedFair.status }}
        </span>
        <h2 class="text-xl font-bold mt-3">{{ selectedFair.title }}</h2>
        <div class="mt-4 space-y-2 text-sm text-blue-100">
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

      <div class="p-4 space-y-4">
        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-blue-600 rounded-full"></span>
            招聘会简介
          </h3>
          <div class="text-gray-600 text-sm leading-relaxed" v-html="selectedFair.description"></div>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-blue-600 rounded-full"></span>
            参会须知
          </h3>
          <div class="text-gray-600 text-sm leading-relaxed" v-html="selectedFair.notice"></div>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-blue-600 rounded-full"></span>
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

      <!-- 底部操作栏 -->
      <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 p-4 flex gap-3">
        <button 
          @click="toggleFavorite('fair', selectedFair.id)"
          class="flex-1 py-3 border-2 border-blue-600 text-blue-600 rounded-xl font-medium flex items-center justify-center gap-2"
        >
          <svg 
            class="w-5 h-5" 
            :class="favorites.fairs.includes(selectedFair.id) ? 'fill-blue-600' : ''"
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
          </svg>
          {{ favorites.fairs.includes(selectedFair.id) ? '已收藏' : '收藏' }}
        </button>
        <button class="flex-[2] py-3 bg-gradient-to-r from-blue-600 to-blue-800 text-white rounded-xl font-bold">
          立即报名
        </button>
      </div>
    </main>
  </div>
</template>
<script setup>

</script>