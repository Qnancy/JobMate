<template>
  <main class="min-h-screen bg-gradient-to-b from-sky-50 to-white pb-6">
    <div class="bg-gradient-to-r from-sky-500 to-blue-600 text-white px-4 py-6">
      <button
        type="button"
        class="mb-3 inline-flex items-center gap-1 text-white/90 hover:text-white active:opacity-80 transition"
        aria-label="返回"
        @click="goBack"
      >
        <svg viewBox="0 0 24 24" class="w-5 h-5" fill="none" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        <span class="text-sm">返回</span>
      </button>
      <div v-if="loading" class="text-sky-100 text-sm">加载中…</div>
      <template v-else>
        <h1 class="text-xl font-bold leading-snug">{{ company.name || "企业" }}</h1>
        <p class="text-sky-100 mt-2 text-sm">{{ typeLabel }}</p>
      </template>
    </div>

    <div class="p-4 space-y-4">
      <div class="bg-white rounded-xl p-4 shadow-md border border-sky-50">
        <h2 class="font-bold text-gray-800 mb-2 flex items-center gap-2">
          <span class="w-1 h-4 bg-sky-500 rounded-full" />
          企业简介
        </h2>
        <p v-if="!loading && !hasDescription" class="text-gray-400 text-sm leading-relaxed">
          暂无简介，管理员可在后台「企业管理」中维护。
        </p>
        <div
          v-else
          class="text-gray-700 text-sm leading-relaxed whitespace-pre-wrap"
        >{{ company.description || "" }}</div>
      </div>

      <div>
        <h2 class="font-bold text-gray-800 mb-3 px-0.5 flex items-center gap-2">
          <span class="w-1 h-4 bg-violet-500 rounded-full" />
          在招职位
          <span v-if="!jobsLoading && jobTotal >= 0" class="text-xs font-normal text-gray-400"
          >（{{ jobTotal }}）</span>
        </h2>
        <div v-if="jobsLoading" class="text-sm text-gray-400 py-6 text-center">职位加载中…</div>
        <template v-else-if="jobs.length > 0">
          <div class="space-y-3">
            <JobItemCard
              v-for="job in jobs"
              :key="job.id"
              :job="job"
              :favorited="isJobFavorited(job.id)"
              :animating="animatingJobs.has(job.id)"
              @view="viewJobDetail"
              @toggle-favorite="toggleFavorite('job', $event)"
            />
          </div>
        </template>
        <p v-else class="text-gray-400 text-sm py-4 text-center bg-white/80 rounded-xl border border-sky-50">
          该企业暂无在招职位
        </p>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { showToast } from "vant";
import { getCompanyById, labelCompanyType, type Company } from "@/services/company";
import { searchJob } from "@/services/job";
import { isSuccessResponse } from "@/utils/request";
import { navigateBackPreferFrom, withReturnFrom } from "@/utils/returnNavigation";
import { favoriteStore } from "@/utils/favoriteStore";
import JobItemCard from "./components/JobItemCard.vue";
import { mapJobToCard, type JobCard } from "./components/cards";

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const jobsLoading = ref(true);
const company = ref<Company>({
  id: 0,
  name: "",
  type: "PRIVATE",
  description: null,
});
const jobs = ref<JobCard[]>([]);
const jobTotal = ref(-1);

const animatingJobs = ref<Set<number>>(new Set());

const typeLabel = computed(() => labelCompanyType(company.value.type));
const hasDescription = computed(() => {
  const d = company.value.description;
  return typeof d === "string" && d.trim().length > 0;
});

function goBack() {
  navigateBackPreferFrom(router, route, { path: "/info", query: { tab: "job" } });
}

function isJobFavorited(id: number) {
  return favoriteStore.isFavorited("JOB", id);
}

function viewJobDetail(job: JobCard) {
  router.push(withReturnFrom(`/info/job/${job.id}`, route));
}

function toggleFavorite(_type: "job" | "fair", id: number) {
  const numId = Number(id);
  animatingJobs.value.add(numId);
  setTimeout(() => animatingJobs.value.delete(numId), 180);
  favoriteStore.toggle("JOB", numId).catch(() => {
    showToast("收藏操作失败");
  });
}

async function load() {
  const id = Number(route.params.id);
  if (!id) {
    showToast("无效的企业");
    loading.value = false;
    jobsLoading.value = false;
    return;
  }

  loading.value = true;
  jobsLoading.value = true;
  try {
    const [companyRes, jobRes] = await Promise.all([
      getCompanyById(id),
      searchJob({ page: 1, page_size: 50, company_id: id }),
    ]);

    if (!isSuccessResponse(companyRes) || !companyRes.data) {
      showToast(companyRes.message || "企业加载失败");
      return;
    }
    company.value = companyRes.data as Company;

    if (isSuccessResponse(jobRes) && jobRes.data) {
      jobs.value = (jobRes.data.content || []).map(mapJobToCard);
      jobTotal.value = Number(jobRes.data.total ?? jobs.value.length);
      if (jobs.value.length > 0) {
        favoriteStore.syncForIds(
          "JOB",
          jobs.value.map((j) => j.id),
        );
      }
    } else {
      jobs.value = [];
      jobTotal.value = 0;
    }
  } catch {
    showToast("加载失败");
  } finally {
    loading.value = false;
    jobsLoading.value = false;
  }
}

onMounted(() => {
  load();
});
</script>
