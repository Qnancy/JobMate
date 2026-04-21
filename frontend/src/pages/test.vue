<template>
  <div class="min-h-screen bg-gradient-to-b from-sky-50 to-white">
    <!-- 顶部导航栏 -->
    <header class="bg-gradient-to-r from-sky-500 to-blue-600 text-white px-4 py-3 sticky top-0 z-50 shadow-lg">
      <div class="flex items-center gap-3">
        <button 
          v-if="currentPage !== 'home'" 
          @click="goBack"
          class="p-1 hover:bg-white/20 rounded-full transition"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
          </svg>
        </button>
        <h1 class="text-lg font-bold">{{ pageTitle }}</h1>
      </div>
    </header>

    <!-- 主页 -->
    <main v-if="currentPage === 'home'" class="p-4">
      <!-- Logo区域 -->
      <div class="text-center py-8">
        <div class="w-20 h-20 mx-auto bg-gradient-to-br from-sky-400 to-blue-600 rounded-2xl flex items-center justify-center shadow-lg mb-4">
          <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-blue-800">浙大就业信息平台</h2>
        <p class="text-sky-600 mt-2">助力浙大学子，开启职业未来</p>
      </div>

      <!-- 搜索框 -->
      <div class="relative mb-8">
        <input 
          v-model="searchQuery"
          type="text" 
          placeholder="搜索职位、公司、招聘会..."
          class="w-full px-4 py-3 pl-12 rounded-xl border-2 border-sky-200 focus:border-sky-400 focus:outline-none shadow-sm text-gray-700"
          @keyup.enter="handleSearch"
        />
        <svg class="w-5 h-5 text-sky-400 absolute left-4 top-1/2 -translate-y-1/2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
        <button 
          @click="handleSearch"
          class="absolute right-2 top-1/2 -translate-y-1/2 bg-sky-500 text-white px-4 py-1.5 rounded-lg text-sm font-medium hover:bg-sky-600 transition"
        >
          搜索
        </button>
      </div>

      <!-- 功能按钮 -->
      <div class="space-y-4">
        <button 
          @click="currentPage = 'jobs'"
          class="w-full bg-gradient-to-r from-sky-500 to-blue-500 text-white py-5 rounded-2xl font-bold text-lg shadow-lg hover:shadow-xl transition transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3"
        >
          <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
          </svg>
          查看职位
        </button>
        
        <button 
          @click="currentPage = 'fairs'"
          class="w-full bg-gradient-to-r from-blue-600 to-blue-800 text-white py-5 rounded-2xl font-bold text-lg shadow-lg hover:shadow-xl transition transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3"
        >
          <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
          查看招聘会
        </button>
      </div>

      <!-- 统计数据 -->
      <div class="mt-8 grid grid-cols-3 gap-3">
        <div class="bg-white rounded-xl p-4 text-center shadow-md border border-sky-100">
          <div class="text-2xl font-bold text-sky-600">{{ jobs.length }}</div>
          <div class="text-xs text-gray-500 mt-1">在招职位</div>
        </div>
        <div class="bg-white rounded-xl p-4 text-center shadow-md border border-sky-100">
          <div class="text-2xl font-bold text-blue-600">{{ fairs.length }}</div>
          <div class="text-xs text-gray-500 mt-1">招聘会</div>
        </div>
        <div class="bg-white rounded-xl p-4 text-center shadow-md border border-sky-100">
          <div class="text-2xl font-bold text-blue-800">500+</div>
          <div class="text-xs text-gray-500 mt-1">合作企业</div>
        </div>
      </div>
    </main>

    <!-- 职位列表页 -->
    <main v-else-if="currentPage === 'jobs'" class="p-4">
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
          class="bg-white rounded-xl p-4 shadow-md border border-sky-50 hover:shadow-lg transition"
        >
          <div class="flex justify-between items-start">
            <div class="flex-1" @click="viewJobDetail(job)">
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
                class="w-6 h-6 transition" 
                :class="favorites.jobs.includes(job.id) ? 'text-red-500 fill-red-500' : 'text-gray-300'"
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
              @click="viewJobDetail(job)"
              class="text-sky-500 text-sm font-medium hover:text-sky-600"
            >
              查看详情 →
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 招聘会列表页 -->
    <main v-else-if="currentPage === 'fairs'" class="p-4">
      <!-- 招聘会卡片列表 -->
      <div class="space-y-4">
        <div 
          v-for="fair in fairs" 
          :key="fair.id"
          class="bg-white rounded-xl overflow-hidden shadow-md border border-sky-50 hover:shadow-lg transition"
        >
          <div class="h-2 bg-gradient-to-r from-sky-400 to-blue-600"></div>
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
                  class="w-6 h-6 transition" 
                  :class="favorites.fairs.includes(fair.id) ? 'text-red-500 fill-red-500' : 'text-gray-300'"
                  fill="none" 
                  stroke="currentColor" 
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                </svg>
              </button>
            </div>
            <button 
              @click="viewFairDetail(fair)"
              class="w-full mt-4 py-2.5 bg-gradient-to-r from-sky-500 to-blue-500 text-white rounded-lg font-medium hover:from-sky-600 hover:to-blue-600 transition"
            >
              查看详情
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 职位详情页 -->
    <main v-else-if="currentPage === 'jobDetail' && selectedJob" class="pb-24">
      <div class="bg-gradient-to-r from-sky-500 to-blue-600 text-white px-4 py-6">
        <h2 class="text-xl font-bold">{{ selectedJob.title }}</h2>
        <p class="text-sky-100 mt-1">{{ selectedJob.company }}</p>
        <div class="flex flex-wrap gap-2 mt-3">
          <span class="px-3 py-1 bg-white/20 rounded-full text-sm">{{ selectedJob.location }}</span>
          <span class="px-3 py-1 bg-white/20 rounded-full text-sm">{{ selectedJob.type }}</span>
        </div>
        <p class="text-2xl font-bold mt-4 text-yellow-300">{{ selectedJob.salary }}</p>
      </div>

      <div class="p-4 space-y-4">
        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            职位描述
          </h3>
          <div class="text-gray-600 text-sm leading-relaxed" v-html="selectedJob.fullDescription"></div>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            任职要求
          </h3>
          <div class="text-gray-600 text-sm leading-relaxed" v-html="selectedJob.requirements"></div>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            公司信息
          </h3>
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 bg-sky-100 rounded-lg flex items-center justify-center">
              <span class="text-sky-600 font-bold text-lg">{{ selectedJob.company.charAt(0) }}</span>
            </div>
            <div>
              <p class="font-medium text-gray-800">{{ selectedJob.company }}</p>
              <p class="text-sm text-gray-500">{{ selectedJob.companyType }} · {{ selectedJob.companySize }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作栏 -->
      <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 p-4 flex gap-3">
        <button 
          @click="toggleFavorite('job', selectedJob.id)"
          class="flex-1 py-3 border-2 border-sky-500 text-sky-500 rounded-xl font-medium flex items-center justify-center gap-2"
        >
          <svg 
            class="w-5 h-5" 
            :class="favorites.jobs.includes(selectedJob.id) ? 'fill-sky-500' : ''"
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
          </svg>
          {{ favorites.jobs.includes(selectedJob.id) ? '已收藏' : '收藏' }}
        </button>
        <button class="flex-[2] py-3 bg-gradient-to-r from-sky-500 to-blue-600 text-white rounded-xl font-bold">
          立即投递
        </button>
      </div>
    </main>

    <!-- 招聘会详情页 -->
    <main v-else-if="currentPage === 'fairDetail' && selectedFair" class="pb-24">
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
import { ref, computed } from 'vue'

// 页面状态
const currentPage = ref('home')
const searchQuery = ref('')
const selectedJobTag = ref('全部')
const selectedJob = ref(null)
const selectedFair = ref(null)

// 收藏状态
const favorites = ref({
  jobs: [],
  fairs: []
})

// 职位标签
const jobTags = ['全部', '技术', '产品', '设计', '运营', '市场']

// Mock 职位数据
const jobs = ref([
  {
    id: 1,
    title: '前端开发工程师',
    company: '阿里巴巴',
    location: '杭州',
    type: '全职',
    salary: '25-40K',
    description: '负责集团核心业务前端开发，参与技术架构设计与优化',
    category: '技术',
    publishDate: '2024-01-15',
    companyType: '互联网',
    companySize: '10000人以上',
    fullDescription: `
      <p><strong>岗位职责：</strong></p>
      <ul class="list-disc pl-4 space-y-1 mt-2">
        <li>负责阿里巴巴核心业务的前端开发工作</li>
        <li>参与前端技术架构设计与性能优化</li>
        <li>与产品、设计、后端紧密配合，确保产品高质量交付</li>
        <li>参与前端工程化建设，提升团队研发效率</li>
      </ul>
    `,
    requirements: `
      <ul class="list-disc pl-4 space-y-1">
        <li>本科及以上学历，计算机相关专业优先</li>
        <li>精通 HTML、CSS、JavaScript，熟悉 ES6+ 语法</li>
        <li>熟练使用 Vue 或 React 框架</li>
        <li>良好的沟通能力和团队协作精神</li>
        <li>有大型项目经验者优先</li>
      </ul>
    `
  },
  {
    id: 2,
    title: '产品经理',
    company: '字节跳动',
    location: '北京',
    type: '全职',
    salary: '30-50K',
    description: '负责抖音相关产品的规划与设计，推动产品迭代优化',
    category: '产品',
    publishDate: '2024-01-14',
    companyType: '互联网',
    companySize: '10000人以上',
    fullDescription: `
      <p><strong>岗位职责：</strong></p>
      <ul class="list-disc pl-4 space-y-1 mt-2">
        <li>负责抖音产品功能的规划与设计</li>
        <li>深入分析用户需求，输出高质量的产品方案</li>
        <li>跟进产品开发进度，确保按时高质量上线</li>
        <li>持续关注数据指标，推动产品迭代优化</li>
      </ul>
    `,
    requirements: `
      <ul class="list-disc pl-4 space-y-1">
        <li>本科及以上学历，3年以上产品经验</li>
        <li>优秀的逻辑思维和数据分析能力</li>
        <li>出色的沟通协调能力</li>
        <li>对短视频/社交产品有深刻理解者优先</li>
      </ul>
    `
  },
  {
    id: 3,
    title: 'UI/UX设计师',
    company: '网易',
    location: '杭州',
    type: '全职',
    salary: '20-35K',
    description: '负责网易游戏相关产品的UI设计与用户体验优化',
    category: '设计',
    publishDate: '2024-01-13',
    companyType: '互联网',
    companySize: '10000人以上',
    fullDescription: `
      <p><strong>岗位职责：</strong></p>
      <ul class="list-disc pl-4 space-y-1 mt-2">
        <li>负责网易游戏产品的界面设计</li>
        <li>参与用户研究，优化产品用户体验</li>
        <li>制定设计规范，维护设计系统</li>
        <li>与开发团队紧密配合，确保设计落地</li>
      </ul>
    `,
    requirements: `
      <ul class="list-disc pl-4 space-y-1">
        <li>本科及以上学历，设计相关专业</li>
        <li>熟练使用 Figma、Sketch 等设计工具</li>
        <li>具备良好的审美和设计思维</li>
        <li>有游戏设计经验者优先</li>
      </ul>
    `
  },
  {
    id: 4,
    title: '后端开发工程师',
    company: '腾讯',
    location: '深圳',
    type: '全职',
    salary: '28-45K',
    description: '负责微信支付核心系统的研发与维护',
    category: '技术',
    publishDate: '2024-01-12',
    companyType: '互联网',
    companySize: '10000人以上',
    fullDescription: `
      <p><strong>岗位职责：</strong></p>
      <ul class="list-disc pl-4 space-y-1 mt-2">
        <li>负责微信支付核心系统的设计与开发</li>
        <li>保障系统的高可用性和高性能</li>
        <li>参与技术方案评审和代码审核</li>
        <li>持续优化系统架构，提升研发效率</li>
      </ul>
    `,
    requirements: `
      <ul class="list-disc pl-4 space-y-1">
        <li>本科及以上学历，计算机相关专业</li>
        <li>精通 Java/Go/C++ 其中一门语言</li>
        <li>熟悉分布式系统设计</li>
        <li>有高并发系统经验者优先</li>
      </ul>
    `
  }
])

// Mock 招聘会数据
const fairs = ref([
  {
    id: 1,
    title: '2024春季大型综合招聘会',
    date: '2024年3月15日 9:00-17:00',
    location: '浙江大学紫金港校区体育馆',
    type: '综合招聘会',
    status: '报名中',
    companies: 156,
    companyList: ['阿里巴巴', '腾讯', '字节跳动', '华为', '网易', '美团', '京东', '小米'],
    description: `
      <p>本次招聘会是浙江大学2024年规模最大的综合性招聘会，汇集<strong>156家知名企业</strong>，涵盖互联网、金融、制造业、咨询等多个行业。</p>
      <p class="mt-2">预计提供岗位<strong>2000+</strong>个，涉及技术、产品、运营、市场等多个方向。</p>
      <p class="mt-2 text-sky-600">欢迎2024届及往届毕业生积极参与！</p>
    `,
    notice: `
      <ul class="list-disc pl-4 space-y-2">
        <li>请携带<strong>学生证</strong>或<strong>校园卡</strong>入场</li>
        <li>建议提前准备好<strong>简历</strong>（纸质版和电子版）</li>
        <li>着装得体，展现专业形象</li>
        <li>可提前在就业信息平台查看参展企业及岗位信息</li>
        <li>招聘会当天提供<strong>免费午餐</strong></li>
      </ul>
    `
  },
  {
    id: 2,
    title: '互联网专场招聘会',
    date: '2024年3月20日 14:00-18:00',
    location: '浙江大学玉泉校区永谦活动中心',
    type: '专场招聘会',
    status: '即将开始',
    companies: 45,
    companyList: ['阿里巴巴', '字节跳动', '美团', '滴滴', '快手', '哔哩哔哩'],
    description: `
      <p>互联网行业专场招聘会，汇集<strong>45家头部互联网企业</strong>。</p>
      <p class="mt-2">主要招聘方向：</p>
      <ul class="list-disc pl-4 mt-1">
        <li>软件开发工程师</li>
        <li>算法工程师</li>
        <li>产品经理</li>
        <li>数据分析师</li>
      </ul>
    `,
    notice: `
      <ul class="list-disc pl-4 space-y-2">
        <li>仅面向<strong>计算机、软件、电子信息</strong>等相关专业学生</li>
        <li>需提前在线报名，现场凭<strong>报名二维码</strong>入场</li>
        <li>建议提前了解目标公司及岗位</li>
      </ul>
    `
  },
  {
    id: 3,
    title: '金融行业精英招聘会',
    date: '2024年3月25日 9:00-12:00',
    location: '浙江大学紫金港校区蒙民伟楼',
    type: '专场招聘会',
    status: '报名中',
    companies: 32,
    companyList: ['中金公司', '高盛', '摩根士丹利', '招商银行', '蚂蚁集团', '中信证券'],
    description: `
      <p>金融行业专场招聘，<strong>32家顶级金融机构</strong>参展。</p>
      <p class="mt-2">岗位类型包括：投资银行、资产管理、风险控制、金融科技等。</p>
    `,
    notice: `
      <ul class="list-disc pl-4 space-y-2">
        <li>主要面向<strong>金融、经济、数学、统计</strong>等专业学生</li>
        <li>请携带中英文简历各5份</li>
        <li>部分企业有现场笔试，请做好准备</li>
      </ul>
    `
  }
])

// 计算页面标题
const pageTitle = computed(() => {
  switch (currentPage.value) {
    case 'home': return '浙大就业信息平台'
    case 'jobs': return '职位列表'
    case 'fairs': return '招聘会列表'
    case 'jobDetail': return '职位详情'
    case 'fairDetail': return '招聘会详情'
    default: return '浙大就业信息平台'
  }
})

// 筛选职位
const filteredJobs = computed(() => {
  if (selectedJobTag.value === '全部') {
    return jobs.value
  }
  return jobs.value.filter(job => job.category === selectedJobTag.value)
})

// 搜索处理
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    currentPage.value = 'jobs'
  }
}

// 返回上一页
const goBack = () => {
  if (currentPage.value === 'jobDetail' || currentPage.value === 'fairDetail') {
    currentPage.value = currentPage.value === 'jobDetail' ? 'jobs' : 'fairs'
  } else {
    currentPage.value = 'home'
  }
}

// 查看职位详情
const viewJobDetail = (job) => {
  selectedJob.value = job
  currentPage.value = 'jobDetail'
}

// 查看招聘会详情
const viewFairDetail = (fair) => {
  selectedFair.value = fair
  currentPage.value = 'fairDetail'
}

// 切换收藏状态
const toggleFavorite = (type, id) => {
  const list = type === 'job' ? favorites.value.jobs : favorites.value.fairs
  const index = list.indexOf(id)
  if (index > -1) {
    list.splice(index, 1)
  } else {
    list.push(id)
  }
}
</script>