<template>
  <div class="min-h-screen bg-gradient-to-b from-sky-50 to-white pb-8">
    <header
      class="sticky top-0 z-10 bg-white/85 backdrop-blur-md border-b border-sky-100 px-3 py-3 flex items-center gap-2 shadow-sm"
    >
      <button
        type="button"
        class="inline-flex items-center gap-1 text-sky-600 text-sm font-medium px-1 py-1 rounded-lg active:bg-sky-50"
        @click="goBack"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        返回
      </button>
      <h1 class="text-[17px] font-bold text-gray-800 flex-1 text-center pr-16">{{ infoPageTitle }}</h1>
    </header>

    <div class="px-3 pt-2">
      <van-search
        v-model="searchValue"
        shape="round"
        :placeholder="searchPlaceholder"
        @search="
          $router.push({
            path: '/info/search',
            query: {
              keywords: searchValue,
              scope: isEventList ? 'activity' : 'job',
            },
          })
        "
      />
    </div>

    <main v-if="!isEventList" class="px-3 pb-6 pt-4">
      <div class="space-y-3 pb-3 mb-4">
        <div>
          <p class="text-xs text-slate-500 font-medium mb-1.5 px-0.5">招聘类型</p>
          <div class="flex gap-2 overflow-x-auto scrollbar-hide items-center">
            <button
              v-for="tag in jobTags"
              :key="'r-' + tag"
              type="button"
              @click="onSelectJobTag(tag)"
              :class="[
                'px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition shrink-0',
                selectedJobTag === tag
                  ? 'bg-sky-500 text-white'
                  : 'bg-white text-gray-600 border border-gray-200',
              ]"
            >
              {{ tag }}
            </button>
          </div>
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium mb-1.5 px-0.5">学历要求</p>
          <div class="flex gap-2 overflow-x-auto scrollbar-hide items-center">
            <button
              v-for="opt in educationOptions"
              :key="'e-' + opt.tier"
              type="button"
              @click="onSelectEducationTier(opt.tier)"
              :class="[
                'px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition shrink-0',
                selectedEducationTier === opt.tier
                  ? 'bg-violet-500 text-white'
                  : 'bg-white text-gray-600 border border-gray-200',
              ]"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>
      </div>

      <div class="space-y-3">
        <JobItemCard
          v-for="job in filteredJobs"
          :key="job.id"
          :job="job"
          :favorited="isJobFavorited(job.id)"
          :animating="animatingJobs.has(job.id)"
          @view="viewJobDetail"
          @toggle-favorite="toggleFavorite('job', $event)"
        />
      </div>
    </main>

    <main v-else class="px-3 pb-6 pt-4">
      <div class="flex gap-2 overflow-x-auto scrollbar-hide items-center pb-2 mb-3">
        <button
          v-for="tag in fairTags"
          :key="tag"
          type="button"
          @click="onSelectFairTag(tag)"
          :class="[
            'px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition',
            selectedFairTag === tag
              ? 'bg-sky-500 text-white'
              : 'bg-white text-gray-600 border border-gray-200',
          ]"
        >
          {{ tag }}
        </button>
      </div>

      <div class="space-y-4">
        <FairItemCard
          v-for="fair in filteredFairs"
          :key="fair.id"
          :fair="fair"
          :favorited="isActivityFavorited(fair.id)"
          :animating="animatingFairs.has(fair.id)"
          @view="viewFairDetail"
          @toggle-favorite="toggleFavorite('fair', $event)"
        />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { withReturnFrom } from "@/utils/returnNavigation";
import { showToast } from "vant";
import {
  searchJob,
  jobMatchesEducationTier,
  type Job,
  type JobEducationFilterTier,
} from "@/services/job";
import { getActivities } from "@/services/activity";
import { isSuccessResponse } from "@/utils/request";
import { favoriteStore } from "@/utils/favoriteStore";
import JobItemCard from "./components/JobItemCard.vue";
import FairItemCard from "./components/FairItemCard.vue";
import {
  mapJobToCard,
  mapActivityToCard,
  type JobCard,
  type FairCard,
} from "./components/cards";

const router = useRouter();
const route = useRoute();

const searchValue = ref("");

const jobTags = ['全部', '实习', '校招'];
const selectedJobTag = ref("全部");

/** 学历筛选：用 tier 存状态，避免用中文串做 === 比较导致筛选失效 */
const educationOptions: { tier: JobEducationFilterTier; label: string }[] = [
  { tier: "ALL", label: "不限" },
  { tier: "BACHELOR", label: "本科及以上" },
  { tier: "MASTER", label: "硕士及以上" },
  { tier: "PHD", label: "博士及以上" },
];
const selectedEducationTier = ref<JobEducationFilterTier>("ALL");

const fairTags = ['全部', '宣讲会', '双选会', '名企探访'];
const selectedFairTag = ref("全部");

/** 从路由回填筛选时跳过，避免与「写回 URL」互相触发 */
let syncingFromRoute = false;

/** 招聘类型 chips → URL 参数 */
function jobTagToQueryParam(tag: string): "all" | "intern" | "campus" {
  if (tag === "实习") return "intern";
  if (tag === "校招") return "campus";
  return "all";
}

function queryParamToJobTag(raw: string | undefined): string {
  if (raw === "intern") return "实习";
  if (raw === "campus") return "校招";
  return "全部";
}

function educationTierToQuery(t: JobEducationFilterTier): "all" | "bachelor" | "master" | "phd" {
  switch (t) {
    case "BACHELOR":
      return "bachelor";
    case "MASTER":
      return "master";
    case "PHD":
      return "phd";
    default:
      return "all";
  }
}

function queryParamToEducationTier(raw: string | undefined): JobEducationFilterTier {
  switch ((raw || "all").toLowerCase()) {
    case "bachelor":
      return "BACHELOR";
    case "master":
      return "MASTER";
    case "phd":
      return "PHD";
    default:
      return "ALL";
  }
}

function fairTagToQueryParam(tag: string): "all" | "lecture" | "double" | "visit" {
  if (tag === "宣讲会") return "lecture";
  if (tag === "双选会") return "double";
  if (tag === "名企探访") return "visit";
  return "all";
}

function queryParamToFairTag(raw: string | undefined): string {
  switch ((raw || "all").toLowerCase()) {
    case "lecture":
      return "宣讲会";
    case "double":
      return "双选会";
    case "visit":
      return "名企探访";
    default:
      return "全部";
  }
}

/** Vue Router 下单个 query 可能是 string | string[]，统一取首个有效字符串 */
function firstQueryString(
  v: string | string[] | null | undefined
): string | undefined {
  if (v == null) return undefined;
  if (Array.isArray(v)) {
    const x = v.find((s) => typeof s === "string" && s.length > 0);
    return x;
  }
  return typeof v === "string" ? v : undefined;
}

/** 与首页入口一致：`tab=job` 为职位列表；`tab=event` / `tab=fair` 为活动列表 */
const isEventList = computed(() => {
  const tabRaw = firstQueryString(route.query.tab) || "job";
  return tabRaw === "fair" || tabRaw === "event";
});

const infoPageTitle = computed(() =>
  isEventList.value ? "招聘活动" : "所有职位",
);

const searchPlaceholder = computed(() =>
  isEventList.value ? "搜索活动" : "搜索职位",
);

function goBack() {
  if (typeof window !== "undefined" && window.history.length > 1) {
    router.back();
    return;
  }
  router.replace("/");
}

function isInfoListRoute(): boolean {
  return route.path === "/info" || route.path === "/info/";
}

/** 把当前列表类型与筛选写入 URL，便于详情页 `from` 带回后恢复 */
function syncFiltersToUrl() {
  if (!isInfoListRoute() || syncingFromRoute) return;
  const q: Record<string, string> = {};
  if (isEventList.value) {
    q.tab = "event";
    const fc = fairTagToQueryParam(selectedFairTag.value);
    if (fc !== "all") q.fair_cat = fc;
  } else {
    q.tab = "job";
    const jc = jobTagToQueryParam(selectedJobTag.value);
    if (jc !== "all") q.job_cat = jc;
    const ed = educationTierToQuery(selectedEducationTier.value);
    if (ed !== "all") q.edu = ed;
  }
  router.replace({ path: "/info", query: q });
}

function applyRouteToFilters() {
  if (!isInfoListRoute()) return;
  syncingFromRoute = true;
  try {
    selectedJobTag.value = queryParamToJobTag(firstQueryString(route.query.job_cat));
    selectedEducationTier.value = queryParamToEducationTier(firstQueryString(route.query.edu));
    selectedFairTag.value = queryParamToFairTag(firstQueryString(route.query.fair_cat));
  } finally {
    nextTick(() => {
      syncingFromRoute = false;
    });
  }
}

function onSelectJobTag(tag: string) {
  selectedJobTag.value = tag;
  syncFiltersToUrl();
}

function onSelectEducationTier(tier: JobEducationFilterTier) {
  selectedEducationTier.value = tier;
  syncFiltersToUrl();
}

function onSelectFairTag(tag: string) {
  selectedFairTag.value = tag;
  syncFiltersToUrl();
}

function isJobFavorited(id: number) {
  return favoriteStore.isFavorited('JOB', id);
}
function isActivityFavorited(id: number) {
  return favoriteStore.isFavorited('ACTIVITY', id);
}

const animatingJobs = ref<Set<number>>(new Set());
const animatingFairs = ref<Set<number>>(new Set());

const jobs = ref<JobCard[]>([]);
const fairs = ref<FairCard[]>([]);

const filteredJobs = computed(() => {
  let list = jobs.value;
  if (selectedJobTag.value !== "全部") {
    list = list.filter((job) => job.category === selectedJobTag.value);
  }
  if (selectedEducationTier.value !== "ALL") {
    const tier = selectedEducationTier.value;
    list = list.filter((job) => jobMatchesEducationTier(job.educationKey, tier));
  }
  return list;
});

const filteredFairs = computed(() => {
  if (selectedFairTag.value === "全部") return fairs.value;
  return fairs.value.filter((fair) => fair.type === selectedFairTag.value);
});

/**
 * 职位列表必须用 /jobs/search：服务端按类型筛选并排序。
 * 原先 GET /jobs 只取库表前 N 条，若前 N 条全是校招，选「实习」后客户端再筛会得到 0 条。
 */
async function fetchJobList() {
  try {
    const tag = selectedJobTag.value;
    let rawJobs: Job[] = [];

    if (tag === "实习") {
      const res = await searchJob({ page: 1, page_size: 50, recruit_type: "INTERN" });
      if (isSuccessResponse(res)) {
        rawJobs = res.data?.content || [];
      }
    } else if (tag === "校招") {
      const [rCampus, rExp] = await Promise.all([
        searchJob({ page: 1, page_size: 50, recruit_type: "CAMPUS" }),
        searchJob({ page: 1, page_size: 50, recruit_type: "EXPERIENCED" }),
      ]);
      const byId = new Map<number, Job>();
      if (isSuccessResponse(rCampus)) {
        for (const j of rCampus.data?.content || []) byId.set(j.id, j);
      }
      if (isSuccessResponse(rExp)) {
        for (const j of rExp.data?.content || []) byId.set(j.id, j);
      }
      rawJobs = [...byId.values()].sort((a, b) => {
        const ta = new Date((a.created_at || "").replace(" ", "T")).getTime();
        const tb = new Date((b.created_at || "").replace(" ", "T")).getTime();
        return (Number.isNaN(tb) ? 0 : tb) - (Number.isNaN(ta) ? 0 : ta);
      });
    } else {
      const res = await searchJob({ page: 1, page_size: 50 });
      if (isSuccessResponse(res)) {
        rawJobs = res.data?.content || [];
      }
    }

    jobs.value = rawJobs.map(mapJobToCard);
    if (jobs.value.length > 0) {
      favoriteStore.syncForIds("JOB", jobs.value.map((j) => j.id));
    }
  } catch {
    showToast("加载职位失败");
  }
}

async function fetchActivitiesList() {
  try {
    const activityRes = await getActivities({ page: 1, page_size: 50 });
    if (isSuccessResponse(activityRes)) {
      fairs.value = (activityRes.data?.content || []).map(mapActivityToCard);
    }
    if (fairs.value.length > 0) {
      favoriteStore.syncForIds("ACTIVITY", fairs.value.map((f) => f.id));
    }
  } catch {
    showToast("加载活动失败");
  }
}

watch(
  () => route.fullPath,
  () => {
    if (!isInfoListRoute()) return;
    applyRouteToFilters();
  },
  { immediate: true },
);

watch(
  selectedJobTag,
  async () => {
    if (!isInfoListRoute() || isEventList.value) return;
    await fetchJobList();
  },
  { immediate: true },
);

watch(
  isEventList,
  async (event) => {
    if (!isInfoListRoute() || !event) return;
    await fetchActivitiesList();
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
</script>

<style scoped></style>
