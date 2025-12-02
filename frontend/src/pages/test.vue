<template>
  <div class="home-page">
    <header class="header">
      <h1 class="title">浙大就业信息平台</h1>
      <p class="subtitle">ZJU Career Services</p>
    </header>

    <div class="search-bar">
      <input type="text" placeholder="搜索职位、公司或招聘会..." v-model="searchText" @keyup.enter="handleSearch">
      <button @click="handleSearch" class="search-btn">
        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>
      </button>
    </div>

    <div class="quick-access">
      <button class="access-btn job-btn" @click="$emit('navigate', 'JobList')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.53 23.53 0 0112 15c-3.185 0-6.257-.942-8.755-2.745M10 20l4-8V4"/></svg>
        <span>查看职位</span>
      </button>
      <button class="access-btn fair-btn" @click="$emit('navigate', 'FairList')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
        <span>查看招聘会</span>
      </button>
    </div>
    
    <div class="info-card">
        <h3>🎉 热门推荐</h3>
        <p>今日新增职位 <strong>50+</strong>，名企招聘会即将开始！</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const searchText = ref('');

const handleSearch = () => {
  if (searchText.value.trim()) {
    alert(`搜索: ${searchText.value} (实际应用中会跳转到搜索结果页)`);
  }
};
</script>

<style scoped>
.home-page {
  padding: 20px;
  min-height: 100vh;
  background-color: var(--color-bg-light);
}

.header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px 0;
  color: var(--color-primary-dark);
}

.title {
  font-size: 2.2rem;
  font-weight: 700;
  margin: 0;
}

.subtitle {
  font-size: 1rem;
  color: var(--color-primary-light);
  margin-top: 5px;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  margin-bottom: 40px;
  border-radius: 25px;
  background-color: var(--color-text-light);
  box-shadow: 0 4px 12px var(--color-shadow);
  overflow: hidden;
}

.search-bar input {
  flex-grow: 1;
  border: none;
  padding: 12px 20px;
  font-size: 16px;
  outline: none;
}

.search-btn {
  background-color: var(--color-primary-light);
  border: none;
  color: var(--color-text-light);
  padding: 10px 15px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-btn:hover {
  background-color: var(--color-primary-dark);
}
.search-btn svg {
  width: 24px;
  height: 24px;
}

/* 快速访问按钮 */
.quick-access {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  margin-bottom: 30px;
}

.access-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 120px;
  border: none;
  border-radius: 15px;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--color-text-light);
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, opacity 0.2s;
}

.access-btn:active {
    transform: scale(0.98);
    opacity: 0.9;
}

.access-btn svg {
  width: 40px;
  height: 40px;
  margin-bottom: 8px;
  stroke: var(--color-text-light);
}

.job-btn {
  background: linear-gradient(135deg, var(--color-primary-light), #3b82f6);
}

.fair-btn {
  background: linear-gradient(135deg, var(--color-primary-dark), #1d4ed8);
}

/* 信息卡片 */
.info-card {
    background-color: var(--color-text-light);
    padding: 15px;
    border-radius: 10px;
    box-shadow: 0 2px 8px var(--color-shadow);
    border-left: 5px solid var(--color-primary-light);
}

.info-card h3 {
    margin-top: 0;
    color: var(--color-primary-dark);
}
</style>