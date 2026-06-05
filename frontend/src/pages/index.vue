<template>
    <div class="home-page">
    <main  class="p-4">
        <!-- Logo区域 -->
        <div class="text-center py-8">
        <div class="w-20 h-20 mx-auto rounded-2xl flex items-center justify-center shadow-lg mb-4 home-logo-card">
                <!-- <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
                </svg> -->
                <p class="text-white">
                    <img src="/Zhejiang_University_Logo.svg.png" alt="浙大Logo" class="w-12 h-12" />
                </p>
        </div>
        <h2 class="text-2xl font-bold home-title">浙大就业信息平台</h2>
        <p class="mt-2 home-subtitle">助力浙大学子，开启职业未来</p>
    </div>
    
    <!-- 搜索框 -->
    <div class="relative mb-8">
        <input 
          v-model="searchQuery"
          type="text" 
          placeholder="搜索职位、活动或企业..."
                    class="w-full px-4 py-3 pl-12 rounded-xl border-2 focus:outline-none shadow-sm home-search-input"
          @keyup.enter="handleSearch"
        />
        <svg class="w-5 h-5 text-sky-400 absolute left-4 top-1/2 -translate-y-1/2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
        <button 
        @click="handleSearch"
                    class="absolute right-2 top-1/2 -translate-y-1/2 px-4 py-1.5 rounded-lg text-sm font-medium transition home-search-btn"
          >
          搜索
        </button>
      </div>

      <!-- 功能按钮 -->
      <div class="space-y-4">
        <button 
          @click="$router.push('/info?tab=job')"
                    class="w-full text-white py-5 rounded-2xl font-bold text-lg shadow-lg hover:shadow-xl transition transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 home-feature-btn"
        >
          <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
          </svg>
          查看职位
        </button>
        
        <button 
          @click="$router.push('/info?tab=event')"
                    class="w-full text-white py-5 rounded-2xl font-bold text-lg shadow-lg hover:shadow-xl transition transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 home-feature-btn"
        >
          <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
                    查看活动
        </button>

        <button 
          @click="$router.push('/info/companies')"
                    class="w-full text-white py-5 rounded-2xl font-bold text-lg shadow-lg hover:shadow-xl transition transform hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 home-feature-btn"
        >
          <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
          </svg>
          查看公司
        </button>
      </div>

      <!-- 统计数据 -->
      <div class="mt-8 grid grid-cols-3 gap-3">
                <div class="rounded-xl p-4 text-center shadow-md border home-stat-card">
                    <div class="text-2xl font-bold home-stat-number">{{ stats.jobs }}</div>
                    <div class="text-xs mt-1 home-stat-label">在招职位</div>
        </div>
                <div class="rounded-xl p-4 text-center shadow-md border home-stat-card">
                    <div class="text-2xl font-bold home-stat-number">{{ stats.fairs }}</div>
                    <div class="text-xs mt-1 home-stat-label">招聘会</div>
        </div>
                <div class="rounded-xl p-4 text-center shadow-md border home-stat-card">
                    <div class="text-2xl font-bold home-stat-number">{{ stats.companies }}</div>
                    <div class="text-xs mt-1 home-stat-label">合作企业</div>
        </div>
    </div>
    </main>
    </div>
    
    <!-- <div class="page-root">
    <header class="top-hero" role="banner">
        <div class="hero-content">
                <h1 class="title">浙大就业信息平台</h1>
                <p class="subtitle">Zhejiang University Career Information Platform</p>
                
                <form class="search-form" @submit.prevent="onSearch">
                    <input
                        v-model="q"
                        class="search-input"
                        type="search"
                        aria-label="搜索宣讲会"
                        placeholder="输入宣讲会名字或时间"
                    />
                    <button type="submit" class="search-btn">搜索</button>
                </form>
            </div>
        </header>

 
    </div> -->
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getJob } from '@/services/job'
import { getActivities } from '@/services/activity'
import { getCompany } from '@/services/company'
import { isSuccessResponse } from '@/utils/request'

const searchQuery = ref('')
const router = useRouter()

function handleSearch() {
    const keyword = (searchQuery.value || '').trim()
    if (!keyword) return
    router.push({
      path: '/info/search',
      query: { keywords: keyword, scope: 'all' },
    })
}

// 平台统计数据
const stats = ref({ fairs: 0, jobs: 0, companies: 0 })

async function fetchStats() {
    try {
        const [jobRes, activityRes, companyRes] = await Promise.all([
            getJob({ page: 1, page_size: 1 }),
            getActivities({ page: 1, page_size: 1 }),
            getCompany({ page: 1, page_size: 1 }),
        ])

        stats.value = {
            jobs: isSuccessResponse(jobRes) ? Number(jobRes.data?.total || 0) : 0,
            fairs: isSuccessResponse(activityRes) ? Number(activityRes.data?.total || 0) : 0,
            companies: isSuccessResponse(companyRes) ? Number(companyRes.data?.total || 0) : 0,
        }
    } catch (e) {
        stats.value = { fairs: 0, jobs: 0, companies: 0 }
    }
}

onMounted(() => {
    fetchStats()
})
</script>

<style scoped>
@import "tailwindcss";
.home-page {
    background: var(--home-bg);
    min-height: calc(100vh + 140px);
    padding-bottom: 160px;
}

.home-logo-card {
    background: var(--home-surface);
}

.home-title {
    color: var(--home-title);
}

.home-subtitle {
    color: var(--home-subtitle);
}

.home-search-input {
    border-color: var(--home-input-border);
    color: var(--home-input-text);
    background: var(--home-input-bg);
}

.home-search-input:focus {
    border-color: var(--home-input-focus);
}

.home-search-btn {
    background: var(--home-search-btn-bg);
    color: var(--home-search-btn-text);
}

.home-search-btn:hover {
    background: var(--home-search-btn-hover-bg);
    color: var(--home-search-btn-hover-text);
}

.home-feature-btn {
    background: var(--home-feature-bg);
    color: var(--home-feature-text);
}

.home-stat-card {
    background: var(--home-surface);
    border-color: var(--home-stat-border);
}

.home-stat-number {
    color: var(--home-stat-number);
}

.home-stat-label {
    color: var(--home-stat-label);
}
/* .page-root {
    min-height: 100vh;
    background: linear-gradient(180deg, #f4f7ff 0%, #ffffff 35%);
    padding-bottom: 48px;
    color: #0f1f49;
}

.top-hero {
    min-height: 260px;
    background: linear-gradient(135deg, #1f4ec7 0%, #1d7ddc 55%, #39b3ff 100%);
    color: #fff;
    border-bottom-left-radius: 50px;
    border-bottom-right-radius: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 18px 45px rgba(35, 81, 181, 0.35);
    padding: 40px 20px 68px;
}

.hero-content {
    width: 100%;
    max-width: 960px;
    text-align: left;
}

.title {
    margin: 0 0 6px;
    font-weight: 700;
    font-size: clamp(24px, 4vw, 38px);
    line-height: 1.2;
}

.subtitle {
    margin: 0 0 24px;
    opacity: 0.85;
    font-size: clamp(14px, 2.5vw, 18px);
}

.search-form {
    display: flex;
    gap: 10px;
    width: 100%;
    max-width: 700px;
    background: rgba(255, 255, 255, 0.15);
    padding: 8px;
    border-radius: 999px;
    backdrop-filter: blur(6px);
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.25);
}

.search-input {
    flex: 1;
    padding: 12px 18px;
    border-radius: 999px;
    border: none;
    background: rgba(255, 255, 255, 0.9);
    color: #13408d;
    font-size: 15px;
    transition: box-shadow 0.2s ease, background 0.2s ease;
}
.search-input::placeholder {
    color: rgba(19, 64, 141, 0.55);
}
.search-input:focus {
    outline: none;
    background: #fff;
    box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.45);
}

.search-btn {
    padding: 12px 24px;
    border-radius: 999px;
    border: none;
    background: linear-gradient(135deg, #ffd976, #ffb347);
    color: #1d2b50;
    font-weight: 700;
    cursor: pointer;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    box-shadow: 0 10px 18px rgba(0, 0, 0, 0.15);
}
.search-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 14px 24px rgba(0, 0, 0, 0.18);
}

.page-body {
    padding: 0 20px;
    margin-top: -48px;
}

.page-actions {
    display: flex;
    flex-direction: column;
    gap: 18px;
    width: 100%;
    max-width: 720px;
    margin: 0 auto 40px;
}

.action-large {
    padding: 26px 26px;
    border-radius: 24px;
    border: none;
    text-align: left;
    background: linear-gradient(135deg, #68c0f8, #3d8bff);
    color: #fff;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    width: 100%;
    min-height: 120px;
    box-shadow: 0 20px 30px rgba(61, 139, 255, 0.3);
    transition: transform 0.25s ease, box-shadow 0.25s ease, filter 0.25s ease;
}
.action-large.outline {
    background: linear-gradient(135deg, #7dd3fc, #60a5fa);
}
.action-large:hover {
    transform: translateY(-4px);
    box-shadow: 0 26px 38px rgba(61, 139, 255, 0.35);
    filter: brightness(1.02);
}

.stats-section {
    width: 100%;
    max-width: 960px;
    margin: 0 auto;
    padding: 28px 24px 36px;
    background: #fff;
    border-radius: 30px;
    box-shadow: 0 25px 35px rgba(15, 23, 42, 0.08);
}

.stats-title {
    margin: 0 0 20px;
    font-size: 18px;
    color: #0f1f49;
    font-weight: 700;
}

.stats-list {
    display: flex;
    gap: 16px;
    justify-content: space-between;
}

.stat-card {
    flex: 1;
    background: linear-gradient(135deg, #f8fbff 0%, #eef5ff 100%);
    border-radius: 22px;
    padding: 20px;
    text-align: center;
    box-shadow: inset 0 0 0 1px rgba(60, 109, 255, 0.08);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.stat-card:hover {
    transform: translateY(-3px);
    box-shadow: inset 0 0 0 1px rgba(60, 109, 255, 0.15), 0 18px 26px rgba(15, 23, 42, 0.12);
}

.stat-value {
    font-size: 30px;
    font-weight: 800;
    color: #1f4ec7;
}

.stat-label {
    margin-top: 6px;
    font-size: 14px;
    color: rgba(15, 31, 73, 0.7);
}

@media (max-width: 720px) {
    .page-actions {
        margin-top: 10px;
    }
    .stats-list {
        flex-direction: column;
    }
}

@media (max-width: 480px) {
    .top-hero {
        padding: 32px 16px 56px;
        border-bottom-left-radius: 32px;
        border-bottom-right-radius: 32px;
    }
    .search-form {
        flex-direction: column;
        border-radius: 24px;
    }
    .search-btn {
        width: 100%;
    }
} */
</style>