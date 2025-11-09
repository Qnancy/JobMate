<template>
    <div class="page-root">
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

        <!-- 页面下方的其它内容占位 -->
        <main class="page-body">
            <div class="page-actions">
                <!--info里面需要分开jobs和events（目前应该是以events为主）-->
                <button class="action-large" @click="goInfo('jobs')">查看职位</button>
                <button class="action-large outline" @click="goInfo('events')">查看活动</button>
            </div>
            <!-- 平台数据栏 -->
            <section class="stats-section" aria-label="平台数据">
                <h2 class="stats-title">平台数据</h2>
                <div class="stats-list">
                    <div class="stat-card">
                        <div class="stat-value">{{ stats.fairs ?? '--' }}</div>
                        <div class="stat-label">宣讲会</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">{{ stats.jobs ?? '--' }}</div>
                        <div class="stat-label">招聘职位</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">{{ stats.companies ?? '--' }}</div>
                        <div class="stat-label">合作企业</div>
                    </div>
                </div>
            </section>
        </main>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const q = ref('')
const router = useRouter()

function onSearch() {
    const keyword = (q.value || '').trim()
    if (!keyword) return
    router.push({ path: '/info/search', query: { q: keyword } })
}

function goInfo(tab = '') {
    const query = tab ? { tab } : {}
    router.push({ path: '/info', query })
}

// 平台统计数据（后端未接入时使用占位数据）
const stats = ref({ fairs: null, jobs: null, companies: null })

async function fetchStats() {
    try {
        const res = await fetch('/api/stats')
        if (!res.ok) throw new Error('network')
        const data = await res.json()
        // 期望返回字段：{ fairs: number, jobs: number, companies: number }
        stats.value = {
            fairs: data.fairs ?? 0,
            jobs: data.jobs ?? 0,
            companies: data.companies ?? 0,
        }
    } catch (e) {
        // 后端尚未就绪时使用占位数据，后续替换为真实接口即可
        stats.value = { fairs: 12, jobs: 238, companies: 64 }
    }
}

onMounted(() => {
    fetchStats()
})
</script>

<style scoped>
/* 顶部区域：白底、蓝色字体，简洁风格 */
.top-hero {
    height: 33vh; /* 大概占上三分之一视口高度 */
    min-height: 180px;
    background: #ffffff; /* 白底 */
    color: #2f80ed; /* 全局蓝色字体 */
    display: flex;
    align-items: center;
    justify-content: center;
}

.hero-content {
    width: 100%;
    max-width: 980px;
    padding: 20px;
    text-align: center;
}

.title {
    margin: 0 0 8px;
    font-weight: 700;
    font-size: clamp(20px, 4vw, 36px);
    line-height: 1.1;
    color: #2f80ed;
}

.subtitle {
    margin: 0 0 18px;
    opacity: 0.9;
    color: #2f80ed;
}

.search-form {
    display: flex;
    gap: 8px;
    margin: 0 auto;
    width: 100%;
    max-width: 720px;
}

.search-input {
    flex: 1;
    padding: 12px 14px;
    border-radius: 999px;
    border: 1px solid rgba(47,128,237,0.12);
    outline: none;
    background: #f7fbff;
    color: #2f80ed;
    font-size: 14px;
}
.search-input::placeholder { color: rgba(47,128,237,0.45); }

.search-btn {
    padding: 10px 16px;
    border-radius: 999px;
    border: 1px solid #2f80ed;
    background: #fff;
    color: #2f80ed;
    font-weight: 600;
    cursor: pointer;
}

.page-body {
    padding: 28px 20px;
    background: #ffffff;
    color: #2f80ed;
}

.stats-section {
    width: 100%;
    max-width: 980px;
    margin: 10px auto 60px;
    padding: 18px;
}

.stats-title {
    margin: 0 0 12px;
    font-size: 16px;
    color: #2f80ed;
    font-weight: 600;
}

.stats-list {
    display: flex;
    gap: 14px;
    justify-content: space-between;
}

.stat-card {
    flex: 1 1 0;
    border: 1px solid rgba(47,128,237,0.12);
    border-radius: 10px;
    padding: 18px;
    text-align: center;
    background: #fff;
}

.stat-value {
    font-size: 28px;
    font-weight: 800;
    color: #2f80ed;
}

.stat-label {
    margin-top: 8px;
    font-size: 13px;
    color: rgba(47,128,237,0.9);
}

@media (max-width: 720px) {
    .stats-list { flex-direction: column; }
}

.page-actions {
    display: flex;
    flex-direction: column;
    gap: 18px;
    width: 100%;
    max-width: 720px;
    margin: 10px auto 40px;
}

.action-large {
    padding: 18px 20px;
    border-radius: 14px;
    border: 2px solid #2f80ed;
    background: #ffffff;
    color: #2f80ed;
    font-size: 18px;
    font-weight: 700;
    cursor: pointer;
    width: 100%;
}
.action-large.outline {
    background: #fff;
}

/* 响应式：小屏幕上让 hero 更紧凑 */
@media (max-width: 480px) {
    .top-hero { height: 30vh; }
    .search-form { gap: 6px; }
    .search-input { padding: 10px; }
    .search-btn { padding: 9px 12px; }
}
</style>