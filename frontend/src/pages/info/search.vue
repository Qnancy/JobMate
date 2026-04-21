<template>
<van-nav-bar
  title="搜索结果"
  left-text="Back"
  left-arrow
  @click-left="onClickLeft"
/>
<p class="result-hint">搜索 <span style="font-weight: bold;">{{ searchValue }}</span> 共 {{ searchResults.length }} 个结果</p>
<common-list :list="searchResults" />
</template>
<script setup lang="ts">
import commonList from './components/list.vue';

import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { showToast } from 'vant';
import { searchJobs, searchActivities } from '@/services/search';
import { isSuccessResponse } from '@/utils/request';
const route = useRoute();

type SearchCard = {
  id: number;
  title: string;
  company?: string;
  companySize?: string;
  salary?: string;
  tags?: string[];
  hr?: { name: string; role: string };
  location?: { district: string; area: string };
  subTitle?: string;
  tag?: string;
};

const searchValue = ref(String(route.query.keywords || route.query.q || ''));
const searchResults = ref<SearchCard[]>([]);

function mapJobResult(item: any): SearchCard {
  const recruitTypeMap: Record<string, string> = {
    INTERN: '实习',
    CAMPUS: '校招',
    EXPERIENCED: '社招',
  };

  return {
    id: Number(item.id),
    title: item.position || '未命名职位',
    company: item.company?.name || '未知企业',
    companySize: item.company?.type || '企业',
    salary: '面议',
    tags: [recruitTypeMap[item.recruit_type] || item.recruit_type || '招聘'],
    hr: { name: '企业招聘', role: 'HR' },
    location: {
      district: item.location || '地点待定',
      area: item.extra || '',
    },
  };
}

function mapActivityResult(item: any): SearchCard {
  return {
    id: Number(item.id),
    title: item.title || '未命名活动',
    subTitle: `${item.company?.name || '未知企业'} · ${item.time || '时间待定'}`,
    tag: '宣讲会',
  };
}

async function loadSearchResults() {
  const keyword = searchValue.value.trim();
  if (!keyword) {
    searchResults.value = [];
    return;
  }

  try {
    const [jobRes, activityRes] = await Promise.all([
      searchJobs<any>({ page: 1, page_size: 50, keyword }),
      searchActivities<any>({ page: 1, page_size: 50, keyword }),
    ]);

    const jobList = isSuccessResponse(jobRes)
      ? (jobRes.data?.content || []).map(mapJobResult)
      : [];
    const activityList = isSuccessResponse(activityRes)
      ? (activityRes.data?.content || []).map(mapActivityResult)
      : [];

    searchResults.value = [...jobList, ...activityList];
  } catch {
    showToast('搜索失败，请稍后重试');
  }
}

watch(
  () => [route.query.keywords, route.query.q],
  ([keywords, q]) => {
    searchValue.value = String(keywords || q || '');
    loadSearchResults();
  },
  { immediate: true },
);


const onClickLeft = () => {
  history.back()
}
</script>
<style scoped>

.result-hint {
  padding: 1em;
  font-size: 0.8em;
}
</style>