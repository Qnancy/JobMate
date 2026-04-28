<template>
  <van-search
    v-model="searchValue"
    placeholder="搜索职位或活动"
    @search="$router.push({
      'path':'/info/search',
      'query':{
        'keywords': searchValue
      }
    })"
  />
  <div class="min-h-screen bg-gradient-to-b from-sky-50 to-white">
  <van-tabs v-model:active="activeName" class="glass-tabs">
    <van-tab title="职位" name="job">
      <main class="p-4">
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
    </van-tab>

    <van-tab title="活动" name="event">
      <main class="p-4">
        <div class="flex gap-2 overflow-x-auto pb-3 mb-4 scrollbar-hide">
          <button
            v-for="tag in fairTags"
            :key="tag"
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
    </van-tab>
  </van-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { withReturnFrom } from "@/utils/returnNavigation";
import { showToast } from "vant";
import {
  getJob,
  jobMatchesEducationTier,
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
const activeName = ref("job");

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

function isInfoListRoute(): boolean {
  return route.path === "/info" || route.path === "/info/";
}

/** 把当前 Tab 与筛选写入 URL，便于详情页 `from` 带回后恢复 */
function syncFiltersToUrl() {
  if (!isInfoListRoute() || syncingFromRoute) return;
  const q: Record<string, string> = {};
  if (activeName.value === "event") {
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
  const tabRaw = (route.query.tab as string) || "job";
  syncingFromRoute = true;
  try {
    if (tabRaw === "fair" || tabRaw === "event") {
      activeName.value = "event";
    } else {
      activeName.value = "job";
    }
    selectedJobTag.value = queryParamToJobTag(route.query.job_cat as string | undefined);
    selectedEducationTier.value = queryParamToEducationTier(route.query.edu as string | undefined);
    selectedFairTag.value = queryParamToFairTag(route.query.fair_cat as string | undefined);
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

async function fetchJobsAndActivities() {
  try {
    const [jobRes, activityRes] = await Promise.all([
      getJob({ page: 1, page_size: 50 }),
      getActivities({ page: 1, page_size: 50 }),
    ]);

    if (isSuccessResponse(jobRes)) {
      jobs.value = (jobRes.data?.content || []).map(mapJobToCard);
    }
    if (isSuccessResponse(activityRes)) {
      fairs.value = (activityRes.data?.content || []).map(mapActivityToCard);
    }

    if (jobs.value.length > 0) {
      favoriteStore.syncForIds('JOB', jobs.value.map((j) => j.id));
    }
    if (fairs.value.length > 0) {
      favoriteStore.syncForIds('ACTIVITY', fairs.value.map((f) => f.id));
    }
  } catch (error) {
    showToast('加载列表失败');
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

watch(activeName, () => {
  syncFiltersToUrl();
});

onMounted(() => {
  fetchJobsAndActivities();
});

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
