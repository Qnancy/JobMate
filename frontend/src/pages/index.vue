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
.page-root {
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
}
</style>