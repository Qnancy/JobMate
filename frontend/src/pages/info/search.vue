<template>
  <van-nav-bar
    title="搜索结果"
    left-text="返回"
    left-arrow
    @click-left="onClickLeft"
  />
  <div class="min-h-screen bg-gradient-to-b from-sky-50 to-white">
    <div class="px-4 pt-3">
      <van-search
        v-model="searchValue"
        placeholder="搜公司名、职位或活动"
        @search="runSearch"
      />
      <p class="result-hint">
        <template v-if="filterCompanyId">
          <template v-if="activeKeyword">
            在 <span class="result-keyword">{{ companyDisplayName }}</span> 内搜索
            <span class="result-keyword">「{{ activeKeyword }}」</span>
          </template>
          <template v-else>
            <span class="result-keyword">{{ companyDisplayName }}</span> 发布的职位
          </template>
        </template>
        <template v-else>
          搜索 <span class="result-keyword">{{ activeKeyword || '—' }}</span>
        </template>
        共 {{ jobs.length + fairs.length }} 个结果
      </p>
    </div>

    <main class="p-4 pt-0">
      <section v-if="matchedCompanies.length > 0" class="mb-4">
        <h4 class="section-title">匹配到的企业 · {{ matchedCompanies.length }}</h4>
        <p class="company-hint">点选下面一家企业，即可查看其发布的全部职位。</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="c in matchedCompanies"
            :key="`c-${c.id}`"
            type="button"
            class="company-chip"
            @click="openCompanyJobs(c)"
          >
            <span class="company-chip-name">{{ c.name }}</span>
            <span class="company-chip-meta">{{ companyTypeLabel(c.type) }}</span>
          </button>
        </div>
      </section>

      <section v-if="jobs.length > 0" class="mb-4">
        <h4 class="section-title">职位 · {{ jobs.length }}</h4>
        <div class="space-y-3">
          <JobItemCard
            v-for="job in jobs"
            :key="`j-${job.id}`"
            :job="job"
            :favorited="isJobFavorited(job.id)"
            :animating="animatingJobs.has(job.id)"
            @view="viewJobDetail"
            @toggle-favorite="toggleFavorite('job', $event)"
          />
        </div>
      </section>

      <section v-if="fairs.length > 0" class="mb-4">
        <h4 class="section-title">活动 · {{ fairs.length }}</h4>
        <div class="space-y-4">
          <FairItemCard
            v-for="fair in fairs"
            :key="`a-${fair.id}`"
            :fair="fair"
            :favorited="isActivityFavorited(fair.id)"
            :animating="animatingFairs.has(fair.id)"
            @view="viewFairDetail"
            @toggle-favorite="toggleFavorite('fair', $event)"
          />
        </div>
      </section>

      <van-empty
        v-if="
          !loading &&
          hasSearchScope &&
          jobs.length === 0 &&
          fairs.length === 0 &&
          matchedCompanies.length === 0
        "
        description="没有找到相关结果"
      />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast } from 'vant';
import { searchJobs, searchActivities, searchCompanies } from '@/services/search';
import type { Job } from '@/services/job';
import type { Activity } from '@/services/activity';
import type { Company, CompanyType } from '@/services/company';
import { isSuccessResponse } from '@/utils/request';
import { favoriteStore } from '@/utils/favoriteStore';
import { withReturnFrom } from '@/utils/returnNavigation';
import JobItemCard from './components/JobItemCard.vue';
import FairItemCard from './components/FairItemCard.vue';
import {
  mapJobToCard,
  mapActivityToCard,
  type JobCard,
  type FairCard,
} from './components/cards';

const route = useRoute();
const router = useRouter();

const searchValue = ref(String(route.query.keywords || route.query.q || ''));
const activeKeyword = ref(searchValue.value.trim());
/** 来自 URL，与职位接口 company_id 一致 */
const filterCompanyId = ref<number | null>(null);
const filterCompanyName = ref('');
const loading = ref(false);

const companyDisplayName = computed(() =>
  filterCompanyName.value.trim() || '该公司',
);
const hasSearchScope = computed(
  () => !!activeKeyword.value.trim() || filterCompanyId.value != null,
);

const jobs = ref<JobCard[]>([]);
const fairs = ref<FairCard[]>([]);
/** 关键词下匹配到的企业（未限定单一公司时展示，点选后带 company_id 看全部职位） */
const matchedCompanies = ref<Company[]>([]);

const COMPANY_TYPE_LABEL: Record<CompanyType, string> = {
  STATE: '国企',
  PRIVATE: '民营',
  FOREIGN: '外资',
};

function companyTypeLabel(t: CompanyType) {
  return COMPANY_TYPE_LABEL[t] ?? t;
}

const animatingJobs = ref<Set<number>>(new Set());
const animatingFairs = ref<Set<number>>(new Set());

function isJobFavorited(id: number) {
  return favoriteStore.isFavorited('JOB', id);
}
function isActivityFavorited(id: number) {
  return favoriteStore.isFavorited('ACTIVITY', id);
}

function parseCompanyIdFromRoute(): number | null {
  const raw = route.query.company_id;
  if (raw == null || raw === '') return null;
  const n = Number(raw);
  return Number.isFinite(n) && n > 0 ? n : null;
}

async function loadSearchResults() {
  const kw = String(route.query.keywords || route.query.q || '').trim();
  const companyId = parseCompanyIdFromRoute();
  const companyName = String(route.query.company_name || '').trim();

  searchValue.value = kw;
  activeKeyword.value = kw;
  filterCompanyId.value = companyId;
  filterCompanyName.value = companyName;

  if (!kw && companyId == null) {
    jobs.value = [];
    fairs.value = [];
    matchedCompanies.value = [];
    return;
  }

  loading.value = true;
  try {
    const jobParams: {
      page: number;
      page_size: number;
      keyword?: string;
      company_id?: number;
    } = { page: 1, page_size: 50 };
    if (kw) jobParams.keyword = kw;
    if (companyId != null) jobParams.company_id = companyId;

    const wantCompanyHints = kw.length > 0 && companyId == null;

    if (wantCompanyHints) {
      const [jobRes, activityRes, companyRes] = await Promise.all([
        searchJobs<{ content: Job[] }>(jobParams),
        searchActivities<{ content: Activity[] }>({
          page: 1,
          page_size: 50,
          keyword: kw,
        }),
        searchCompanies<{ content: Company[] }>({
          page: 1,
          page_size: 15,
          keyword: kw,
        }),
      ]);

      jobs.value = isSuccessResponse(jobRes)
        ? (jobRes.data?.content || []).map(mapJobToCard)
        : [];
      fairs.value = isSuccessResponse(activityRes)
        ? (activityRes.data?.content || []).map(mapActivityToCard)
        : [];
      matchedCompanies.value = isSuccessResponse(companyRes)
        ? companyRes.data?.content || []
        : [];
    } else {
      const jobRes = await searchJobs<{ content: Job[] }>(jobParams);
      const activityRes = kw
        ? await searchActivities<{ content: Activity[] }>({
            page: 1,
            page_size: 50,
            keyword: kw,
          })
        : { code: 200, message: '', data: { content: [] as Activity[] } };

      jobs.value = isSuccessResponse(jobRes)
        ? (jobRes.data?.content || []).map(mapJobToCard)
        : [];
      fairs.value = isSuccessResponse(activityRes)
        ? (activityRes.data?.content || []).map(mapActivityToCard)
        : [];
      matchedCompanies.value = [];
    }

    if (jobs.value.length > 0) {
      favoriteStore.syncForIds('JOB', jobs.value.map((j) => j.id));
    }
    if (fairs.value.length > 0) {
      favoriteStore.syncForIds('ACTIVITY', fairs.value.map((f) => f.id));
    }
  } catch {
    showToast('搜索失败，请稍后重试');
  } finally {
    loading.value = false;
  }
}

function openCompanyJobs(c: Company) {
  router.replace({
    path: '/info/search',
    query: {
      company_id: String(c.id),
      company_name: c.name,
    },
  });
}

function runSearch() {
  const kw = searchValue.value.trim();
  const cid = filterCompanyId.value;
  if (!kw && cid == null) return;
  const query: Record<string, string> = {};
  if (kw) query.keywords = kw;
  if (cid != null) {
    query.company_id = String(cid);
    if (filterCompanyName.value.trim()) {
      query.company_name = filterCompanyName.value.trim();
    }
  }
  router.replace({ path: '/info/search', query });
}

watch(
  () =>
    [
      route.query.keywords,
      route.query.q,
      route.query.company_id,
      route.query.company_name,
    ] as const,
  () => {
    loadSearchResults();
  },
  { immediate: true },
);

function viewJobDetail(job: JobCard) {
  router.push(withReturnFrom(`/info/job/${job.id}`, route));
}

function viewFairDetail(fair: FairCard) {
  router.push(withReturnFrom(`/info/activity/${fair.id}`, route));
}

function toggleFavorite(type: 'job' | 'fair', id: number) {
  const numId = Number(id);
  const animSet = type === 'job' ? animatingJobs : animatingFairs;
  const targetType = type === 'job' ? 'JOB' : 'ACTIVITY';

  animSet.value.add(numId);
  setTimeout(() => animSet.value.delete(numId), 180);

  favoriteStore.toggle(targetType, numId).catch(() => {
    showToast('收藏操作失败');
  });
}

function onClickLeft() {
  history.back();
}
</script>

<style scoped>
.result-hint {
  padding: 0.5em 0.25em 0.75em;
  font-size: 0.85em;
  color: #666;
}
.result-keyword {
  font-weight: 600;
  color: #0284c7;
}
.section-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #475569;
  margin: 0 0 0.5rem 0.25rem;
}
.company-hint {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0 0 0.75rem 0.25rem;
  line-height: 1.45;
}
.company-chip {
  display: inline-flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.15rem;
  padding: 0.5rem 0.75rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  background: #fff;
  text-align: left;
  box-shadow: 0 1px 2px rgb(15 23 42 / 6%);
}
.company-chip:active {
  opacity: 0.88;
  border-color: #7dd3fc;
  background: #f0f9ff;
}
.company-chip-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: #0f172a;
}
.company-chip-meta {
  font-size: 0.7rem;
  color: #64748b;
}
</style>
