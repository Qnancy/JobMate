<template>
  <div class="detail-page">
    <div class="header-bar">
      <button @click="$emit('navigate', 'FairList')" class="back-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/></svg>
      </button>
      <h2 v-if="fair">{{ fair.title }}</h2>
    </div>

    <div v-if="isLoading" class="loading-state">加载中...</div>
    <div v-else-if="fair" class="detail-container">
      <h1 class="main-title">{{ fair.title }}</h1>
      <div class="meta-info">
        <span class="date-tag">日期：{{ fair.date }}</span>
        <span class="venue-tag">地点：{{ fair.venue }}</span>
      </div>
      
      <h3>详情介绍</h3>
      <div class="detail-content" v-html="fair.detailHtml"></div>

      <button class="register-btn">报名参加招聘会</button>
    </div>
    <div v-else class="loading-state">招聘会信息未找到。</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { fetchFairDetail } from '@/utils/mockData';

const props = defineProps({
  params: {
    type: Object,
    required: true
  }
});

const fair = ref(null);
const isLoading = ref(true);

onMounted(async () => {
  fair.value = await fetchFairDetail(props.params.id);
  isLoading.value = false;
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