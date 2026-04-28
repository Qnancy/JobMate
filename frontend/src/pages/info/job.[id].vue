<template>
    <main class="pb-4">
      <div class="bg-gradient-to-r from-sky-500 to-blue-600 text-white px-4 py-6">
        <button
          type="button"
          class="mb-3 inline-flex items-center gap-1 text-white/90 hover:text-white active:opacity-80 transition"
          aria-label="返回"
          @click="goBack"
        >
          <svg viewBox="0 0 24 24" class="w-5 h-5" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
          </svg>
          <span class="text-sm">返回</span>
        </button>
        <h2 class="text-xl font-bold">{{ job.title }}</h2>
        <p class="text-sky-100 mt-1">{{ job.company }}</p>
        <div class="flex flex-wrap gap-2 mt-3">
          <span
            v-for="(city, i) in locationCities"
            :key="'loc-' + i"
            class="px-3 py-1 bg-white/20 rounded-full text-sm"
          >{{ city }}</span>
          <span class="px-3 py-1 bg-white/20 rounded-full text-sm">{{ job.type }}</span>
          <span class="px-3 py-1 bg-white/20 rounded-full text-sm">{{ job.educationLabel }}</span>
          <span v-if="job.deadlineText" class="px-3 py-1 bg-white/20 rounded-full text-sm">{{ job.deadlineText }}</span>
        </div>
        <p class="text-2xl font-bold mt-4 text-yellow-300">{{ job.salary }}</p>
        <p v-if="job.publishText" class="text-xs text-sky-100/80 mt-2">{{ job.publishText }}</p>
      </div>

      <div class="p-4 space-y-4">
        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            职位详情
          </h3>
          <p class="text-xs text-gray-400 mb-2">含岗位介绍、职责与任职要求等，由管理员在后台「职位描述」栏位维护。</p>
          <div class="text-gray-600 text-sm leading-relaxed" v-html="job.fullDescription"></div>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            学历要求
          </h3>
          <p class="text-gray-700 text-sm">{{ job.educationLabel }}</p>
        </div>

        <div class="bg-white rounded-xl p-4 shadow-md">
          <h3 class="font-bold text-gray-800 mb-3 flex items-center gap-2">
            <span class="w-1 h-5 bg-sky-500 rounded-full"></span>
            投递方式
          </h3>
          <template v-if="safeApplyUrl">
            <p class="text-gray-600 text-sm leading-relaxed">
              本岗位已提供<strong class="text-gray-800">官方网申 / 投递入口</strong>。请点击底部「立即投递」或下方链接，在新窗口中打开企业招聘页面完成简历投递。
            </p>
            <a
              :href="safeApplyUrl"
              target="_blank"
              rel="noopener noreferrer"
              class="mt-3 inline-flex items-center gap-1 text-sky-600 text-sm font-medium underline underline-offset-2"
            >
              打开投递链接
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
              </svg>
            </a>
          </template>
          <p v-else class="text-gray-600 text-sm leading-relaxed">
            当前职位<strong class="text-gray-800">未录入在线投递链接</strong>。请通过
            <strong class="text-gray-800">{{ job.company }}</strong>
            官方招聘网站、校招公众号、宣讲会 / 双选会现场等渠道，按企业要求完成简历投递。
          </p>
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
              <button
                v-if="job.companyId"
                type="button"
                class="mt-2 text-sm text-sky-600 font-medium"
                @click="goCompanyJobs"
              >
                查看该公司全部职位
              </button>
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
            class="w-5 h-5 transition-transform duration-150" 
            :class="[isFavorite ? 'text-yellow-500 fill-yellow-500' : 'text-gray-300', heartAnimating ? 'scale-125' : '']"
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
          </svg>
          {{ isFavorite ? '已收藏' : '收藏' }}
        </button>
        <button
          type="button"
          class="flex-[2] py-3 bg-gradient-to-r from-sky-500 to-blue-600 text-white rounded-xl font-bold"
          @click="onApply"
        >
          立即投递
        </button>
      </div>
    </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast } from 'vant';
import { getJobById, labelEducationRequirement, parseEducationRequirement } from '@/services/job';
import { isSuccessResponse } from '@/utils/request';
import { favoriteStore } from '@/utils/favoriteStore';
import { parseJobLocations } from '@/utils/jobLocation';
import {
  navigateBackPreferFrom,
  withReturnFromQuery,
} from '@/utils/returnNavigation';

// const props = defineProps({
//   params: {
//     type: Object,
//     required: true
//   }
// });
const route = useRoute();
const router = useRouter();

function goBack() {
  navigateBackPreferFrom(router, route, { path: '/info', query: { tab: 'job' } });
}

type JobDetailView = {
  id: number;
  title: string;
  company: string;
  /** 有值时可跳转「该公司全部职位」 */
  companyId: number | null;
  location: string;
  type: string;
  salary: string;
  fullDescription: string;
  companyType: string;
  companySize: string;
  link: string;
  publishText: string;
  deadlineText: string | null;
  educationLabel: string;
};

const job = ref<JobDetailView>({
  id: 0,
  title: '',
  company: '',
  companyId: null,
  location: '地点待定',
  type: '',
  salary: '面议',
  fullDescription: '暂无职位描述',
  companyType: '未知',
  companySize: '规模未知',
  link: '',
  publishText: '',
  deadlineText: null,
  educationLabel: '不限',
});

const heartAnimating = ref(false);
const isFavorite = computed(() => favoriteStore.isFavorited('JOB', job.value.id));

const locationCities = computed(() => {
  const list = parseJobLocations(job.value.location);
  return list.length ? list : ['地点待定'];
});

/** 仅允许 http(s)，避免 javascript: 等异常 scheme */
const safeApplyUrl = computed(() => {
  const raw = (job.value.link || '').trim();
  if (!raw) return '';
  try {
    const u = new URL(raw);
    if (u.protocol === 'http:' || u.protocol === 'https:') return u.href;
  } catch {
    /* ignore */
  }
  return '';
});

function onApply() {
  const url = safeApplyUrl.value;
  if (url) {
    window.open(url, '_blank', 'noopener,noreferrer');
    return;
  }
  showToast('暂无在线投递链接，请通过企业官方渠道投递');
}

function mapRecruitType(type: string) {
  const map: Record<string, string> = {
    INTERN: '实习',
    CAMPUS: '校招',
    EXPERIENCED: '校招',
  };
  return map[type] || type;
}

function buildPublishText(createdAt?: string | null, updatedAt?: string | null): string {
  const created = createdAt ? createdAt.slice(0, 10) : '';
  if (!created) return '';
  if (updatedAt && updatedAt !== createdAt) {
    return `发布于 ${created} · 更新于 ${updatedAt.slice(0, 10)}`;
  }
  return `发布于 ${created}`;
}

function buildDeadlineText(deadline?: string | null): string | null {
  if (!deadline) return null;
  const date = new Date(deadline.replace(' ', 'T'));
  if (Number.isNaN(date.getTime())) return null;
  if (date.getTime() < Date.now()) return '已截止';
  return `截止 ${deadline.slice(0, 16)}`;
}

async function loadJob() {
  const id = Number(route.params.id);
  if (!id) return;

  try {
    const res = await getJobById(id);
    if (!isSuccessResponse(res) || !res.data) {
      showToast(res.message || '获取职位详情失败');
      return;
    }

    const item = res.data;
    job.value = {
      id: item.id,
      title: item.position,
      company: item.company?.name || '未知企业',
      companyId: item.company?.id ?? null,
      location: item.location || '地点待定',
      type: mapRecruitType(item.recruit_type),
      salary: '面议',
      fullDescription: item.extra || '暂无职位描述',
      companyType: item.company?.type || '未知',
      companySize: '规模未知',
      link: item.link || '',
      publishText: buildPublishText(item.created_at, item.updated_at),
      deadlineText: buildDeadlineText(item.deadline),
      educationLabel: labelEducationRequirement(
        parseEducationRequirement(item as Record<string, unknown>)
      ),
    };

    // 向后端确认这一条的收藏状态（登录时）；未登录则读本地缓存
    favoriteStore.syncForIds('JOB', [item.id]);
  } catch {
    showToast('获取职位详情失败');
  }
}

onMounted(async () => {
  await loadJob();
});

function goCompanyJobs() {
  const id = job.value.companyId;
  if (!id) return;
  const name = job.value.company || '';
  router.push(
    withReturnFromQuery(
      {
        path: '/info/search',
        query: {
          company_id: String(id),
          ...(name ? { company_name: name } : {}),
        },
      },
      route,
    ),
  );
}

function toggleFavorite(type: 'job', id: number) {
  if (type !== 'job' || !id) return;
  heartAnimating.value = true;
  setTimeout(() => (heartAnimating.value = false), 180);
  favoriteStore.toggle('JOB', Number(id)).catch(() => {
    showToast('收藏操作失败');
  });
}
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