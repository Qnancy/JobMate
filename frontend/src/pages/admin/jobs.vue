<template>
  <AdminManageLayout
    v-model:keyword="keyword"
    v-model:refreshing="refreshing"
    title="职位管理"
    search-placeholder="搜索职位名称、公司名、工作地点…"
    @back="goBack"
    @refresh="onListRefresh"
    @apply-search="applySearch"
  >
    <template #toolbar>
      <van-button size="small" type="primary" icon="plus" @click="openJobEdit(null)">发布职位</van-button>
    </template>

    <van-cell-group :border="false">
      <van-cell
        v-for="job in jobList"
        :key="job.id"
        :title="job.position"
        :label="`${recruitTypeMap[job.recruit_type] || job.recruit_type} · ${jobService.labelEducationRequirement(job.education_requirement)} · ${job.location || '暂无地点'} · ${job.company?.name ? job.company.name + ' · ' : ''}发布于 ${(job.created_at || '').slice(0, 10) || '—'}${job.deadline ? ' · 截止 ' + job.deadline.slice(0, 16) : ''}`"
      >
        <template #value>
          <div class="flex flex-col gap-1 items-end shrink-0" @click.stop>
            <van-button size="small" type="primary" plain @click="openJobEdit(job)">编辑</van-button>
            <van-button size="small" type="danger" plain @click="handleDeleteJob(job.id)">删除</van-button>
          </div>
        </template>
      </van-cell>
    </van-cell-group>
    <div v-if="!jobLoading && jobList.length === 0" class="text-center text-gray-400 py-12 text-sm">暂无职位数据</div>

    <template #after-card>
      <div ref="jobSentinel" class="h-6" />
      <div v-if="jobLoading" class="text-center text-gray-400 py-3 text-sm">加载中…</div>
      <div v-else-if="!jobHasMore && jobList.length" class="text-center text-gray-400 py-3 text-sm">没有更多了</div>
    </template>
  </AdminManageLayout>

  <van-dialog
      v-model:show="showJobEdit"
      class="admin-editor-wide-dialog"
      width="min(820px, 94vw)"
      :title="editingJob?.id ? '编辑职位' : '发布职位'"
      show-cancel-button
      :before-close="onJobDialogBeforeClose"
    >
      <van-form class="p-4 max-h-[70vh] overflow-y-auto">
        <van-field
          v-model="aiRawText"
          type="textarea"
          rows="3"
          autosize
          label="宣传原文"
          label-align="top"
          placeholder="可粘贴招聘 JD / 推文全文，点击下方按钮由 AI 解析并填入下方表单（不会自动保存）"
        />
        <van-button
          class="mb-3"
          block
          type="primary"
          plain
          size="small"
          :loading="aiParsing"
          :disabled="!aiRawText.trim()"
          @click="runAiParseJob"
        >
          AI 识别并填入表单
        </van-button>
        <van-field v-model="jobForm.position" label="职位名称" placeholder="请输入职位名称" />
        <van-field v-model.number="jobForm.company_id" type="digit" label="公司ID" placeholder="请输入公司ID" />
        <van-field
          v-model="recruitTypeMap[jobForm.recruit_type]"
          is-link
          readonly
          name="recruit_type"
          label="招聘类型"
          placeholder="点击选择招聘类型"
          @click="showRecruitTypePicker = true"
        />
        <van-popup v-model:show="showRecruitTypePicker" position="bottom">
          <van-picker :columns="recruitTypeColumns" @confirm="onConfirmRecruitType" @cancel="showRecruitTypePicker = false" />
        </van-popup>
        <van-field
          v-model="educationRequirementMap[jobForm.education_requirement]"
          is-link
          readonly
          name="education_requirement"
          label="学历要求"
          placeholder="点击选择学历要求"
          @click="showEducationPicker = true"
        />
        <van-popup v-model:show="showEducationPicker" position="bottom">
          <van-picker :columns="educationColumns" @confirm="onConfirmEducation" @cancel="showEducationPicker = false" />
        </van-popup>
        <van-field
          v-model="jobForm.location"
          label="工作地点"
          placeholder="单城直接填；多城请用顿号（、）分隔，例如：北京、上海、杭州"
        />
        <van-field v-model="jobForm.link" label="投递链接" placeholder="请输入投递链接" />
        <van-field
          v-model="jobForm.deadline"
          label="截止时间"
          placeholder="可选，例如 2026-05-31 23:59:59"
        >
          <template v-if="jobForm.deadline" #button>
            <van-button size="small" plain @click="jobForm.deadline = ''">清除</van-button>
          </template>
        </van-field>
        <van-field
          v-model="jobForm.extra"
          type="textarea"
          rows="4"
          autosize
          maxlength="8000"
          show-word-limit
          label="职位描述"
          label-align="top"
          placeholder="填写岗位介绍、职责与任职要求等（对应用户端职位详情中的说明正文）"
        />
        <div v-if="editingJob" class="px-3 pt-1 text-xs text-gray-400 leading-relaxed">
          发布于 {{ editingJob.created_at || '—' }}<br>
          最近更新 {{ editingJob.updated_at || '—' }}
        </div>
      </van-form>
    </van-dialog>

    <!-- AI 解析结果确认对话框 -->
    <van-dialog
      v-model:show="showAiPreview"
      class="admin-editor-wide-dialog"
      width="min(820px, 94vw)"
      title="AI 解析结果 · 请确认"
      show-cancel-button
      confirm-button-text="应用到表单"
      cancel-button-text="取消"
      :before-close="onAiPreviewBeforeClose"
    >
      <div v-if="aiPreview.job" class="p-4 max-h-[60vh] overflow-y-auto text-[13px] leading-relaxed">
        <div class="text-gray-500 mb-2">请核对 AI 识别的字段，确认后将覆盖当前表单：</div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">职位</div>
          <div class="flex-1 break-all">{{ aiPreview.job.position || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">招聘类型</div>
          <div class="flex-1 break-all">{{ recruitTypeMap[aiPreview.job.recruit_type] || aiPreview.job.recruit_type || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">学历要求</div>
          <div class="flex-1 break-all">{{ jobService.labelEducationRequirement(aiPreview.job.education_requirement) }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">公司ID</div>
          <div class="flex-1 break-all">{{ Number(aiPreview.job.company_id) > 0 ? aiPreview.job.company_id : '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">公司名称</div>
          <div class="flex-1 break-all">{{ aiPreview.job.company_name || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">工作地点</div>
          <div class="flex-1 break-all">{{ aiPreview.job.location || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">投递链接</div>
          <div class="flex-1 break-all">{{ aiPreview.job.link || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">截止时间</div>
          <div class="flex-1 break-all">{{ aiPreview.job.deadline || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">备注</div>
          <div class="flex-1 break-all">{{ aiPreview.job.extra || '（未识别）' }}</div>
        </div>

        <div
          v-if="aiPreview.warnings?.length"
          class="mt-3 p-2 rounded-md border border-amber-200 bg-amber-50 text-amber-800 text-[12px] leading-relaxed"
        >
          <div class="font-semibold mb-1">⚠ 解析提示</div>
          <div v-for="(w, i) in aiPreview.warnings" :key="i">· {{ w }}</div>
        </div>

        <div
          v-if="aiPreview.suggested_company_name"
          class="mt-3 p-2 rounded-md border border-blue-200 bg-blue-50 text-[12px] leading-relaxed"
        >
          <van-checkbox v-model="aiCreateCompany" shape="square" icon-size="16px">
            同时创建公司「{{ aiPreview.suggested_company_name }}」
          </van-checkbox>
          <div class="text-gray-500 mt-1 pl-6">
            勾选后将以「民营企业」类型新建，创建完成后会自动填入「公司ID」。之后可在企业管理里修改类型/补充信息。
          </div>
        </div>
      </div>
    </van-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive, nextTick } from "vue";
import { useRouter } from "vue-router";
import { showToast, showSuccessToast, showConfirmDialog } from "vant";
import AdminManageLayout from "@/components/admin/AdminManageLayout.vue";
import * as auth from "@/services/auth";
import * as jobService from "@/services/job";
import * as companyService from "@/services/company";
import * as promotionParse from "@/services/promotionParse";
import { isSuccessResponse } from "@/utils/request";

const router = useRouter();

const keyword = ref("");
const refreshing = ref(false);

const jobList = ref<jobService.Job[]>([]);
const jobPage = ref(1);
const jobPageSize = ref(10);
const jobLoading = ref(false);
const jobHasMore = ref(true);
const jobSentinel = ref<HTMLElement | null>(null);
let jobObserver: IntersectionObserver | null = null;

const showJobEdit = ref(false);
const editingJob = ref<jobService.Job | null>(null);
const aiRawText = ref("");
const aiParsing = ref(false);

// AI 解析预览对话框状态
const showAiPreview = ref(false);
const aiCreateCompany = ref(false);
const aiPreview = reactive<{
  job: promotionParse.ParsedJobPromotion | null;
  warnings: string[];
  suggested_company_name: string | null;
}>({
  job: null,
  warnings: [],
  suggested_company_name: null,
});

type JobForm = {
  company_id: number;
  recruit_type: jobService.JobType;
  education_requirement: jobService.EducationRequirement;
  position: string;
  link: string;
  location: string;
  extra: string;
  /** 字符串形式以便直接绑定到 van-field；保存时再判断空值。 */
  deadline: string;
};

const jobForm = reactive<JobForm>({
  company_id: 0,
  recruit_type: "INTERN",
  education_requirement: "UNSPECIFIED",
  position: "",
  link: "",
  location: "",
  extra: "",
  deadline: "",
});

const recruitTypeMap: Record<string, string> = {
  INTERN: "实习",
  CAMPUS: "校招",
  // 历史遗留的 EXPERIENCED(社招) 数据展示为校招，新数据已不再产生该值。
  EXPERIENCED: "校招",
};

const showRecruitTypePicker = ref(false);
const recruitTypeColumns = [
  { text: "实习", value: "INTERN" },
  { text: "校招", value: "CAMPUS" },
];

const showEducationPicker = ref(false);
const educationRequirementMap: Record<jobService.EducationRequirement, string> = {
  UNSPECIFIED: "不限",
  BACHELOR_AND_ABOVE: "本科及以上",
  MASTER_AND_ABOVE: "硕士及以上",
  PHD_AND_ABOVE: "博士及以上",
};
const educationColumns = [
  { text: "不限", value: "UNSPECIFIED" as jobService.EducationRequirement },
  { text: "本科及以上", value: "BACHELOR_AND_ABOVE" },
  { text: "硕士及以上", value: "MASTER_AND_ABOVE" },
  { text: "博士及以上", value: "PHD_AND_ABOVE" },
];

function onConfirmRecruitType({ selectedOptions }: { selectedOptions: { value: jobService.JobType }[] }) {
  const opt = selectedOptions?.[0];
  if (opt?.value) jobForm.recruit_type = opt.value;
  showRecruitTypePicker.value = false;
}

function onConfirmEducation({ selectedOptions }: { selectedOptions: { value: jobService.EducationRequirement }[] }) {
  const opt = selectedOptions?.[0];
  if (opt?.value) jobForm.education_requirement = opt.value;
  showEducationPicker.value = false;
}

function goBack() {
  router.push("/admin");
}

function assertAdmin() {
  const u = auth.currentUser();
  if (!u) {
    showToast("请先登录");
    router.replace({ path: "/my/login-admin" });
    return false;
  }
  if (u.role !== "ADMIN") {
    showToast("无权访问");
    router.replace({ path: "/" });
    return false;
  }
  return true;
}

function setupJobObserver() {
  if (jobObserver) jobObserver.disconnect();
  jobObserver = new IntersectionObserver((entries) => {
    const entry = entries.find((e) => e.isIntersecting);
    if (!entry) return;
    fetchMoreJobs();
  });
  if (jobSentinel.value) jobObserver.observe(jobSentinel.value);
}

function applySearch() {
  jobPage.value = 1;
  jobHasMore.value = true;
  jobList.value = [];
  nextTick(() => {
    setupJobObserver();
    fetchMoreJobs();
  });
}

function onListRefresh() {
  applySearch();
}

async function fetchMoreJobs() {
  if (jobLoading.value || !jobHasMore.value) return;
  jobLoading.value = true;
  try {
    const kw = keyword.value.trim();
    const res = await jobService.searchJob({
      page: jobPage.value,
      page_size: jobPageSize.value,
      ...(kw ? { keyword: kw } : {}),
    });
    if (isSuccessResponse(res)) {
      const list = res.data?.content || [];
      jobList.value.push(...list);
      if (list.length < jobPageSize.value) {
        jobHasMore.value = false;
      } else {
        jobPage.value += 1;
      }
    }
  } catch (e) {
    console.error(e);
  } finally {
    jobLoading.value = false;
    refreshing.value = false;
  }
}

async function runAiParseJob() {
  const text = aiRawText.value.trim();
  if (!text) {
    showToast("请先粘贴宣传文本");
    return;
  }
  aiParsing.value = true;
  try {
    const res = await promotionParse.parsePromotion("JOB", text);
    if (!isSuccessResponse(res) || !res.data?.job) {
      showToast(res.message || "解析失败");
      return;
    }
    aiPreview.job = res.data.job;
    aiPreview.warnings = res.data.warnings || [];
    aiPreview.suggested_company_name = res.data.suggested_company_name || null;
    aiCreateCompany.value = false;
    showAiPreview.value = true;
  } catch {
    /* request 已 toast */
  } finally {
    aiParsing.value = false;
  }
}

/**
 * 预览对话框点「应用到表单」前的钩子：
 * - action === 'confirm'：按勾选情况，先（可选）创建公司拿 id，再覆盖表单；
 * - action === 'cancel' 或关闭：什么都不做。
 * 返回 true 表示允许关闭。
 */
async function onAiPreviewBeforeClose(action: string): Promise<boolean> {
  if (action !== "confirm") {
    return true;
  }
  const j = aiPreview.job;
  if (!j) return true;

  let resolvedCompanyId = Number(j.company_id) || 0;

  // 勾选「一并创建公司」且后端提示了一个待创建的公司名
  if (aiCreateCompany.value && aiPreview.suggested_company_name) {
    const name = aiPreview.suggested_company_name.trim();
    try {
      const created = await companyService.createCompany({
        name,
        type: "PRIVATE", // 默认按民营企业创建，管理员可在企业管理里再改
      });
      if (!isSuccessResponse(created) || !created.data?.id) {
        showToast(created.message || "创建公司失败，已取消应用");
        return false; // 不关闭对话框，让管理员修正
      }
      resolvedCompanyId = created.data.id;
      showSuccessToast(`已创建公司「${name}」(ID=${resolvedCompanyId})`);
    } catch {
      // request 工具已 toast
      return false;
    }
  }

  jobForm.company_id = resolvedCompanyId;
  jobForm.recruit_type = j.recruit_type;
  jobForm.education_requirement = j.education_requirement || "UNSPECIFIED";
  jobForm.position = j.position || "";
  jobForm.link = j.link || "";
  jobForm.location = j.location || "";
  jobForm.extra = j.extra || "";
  jobForm.deadline = j.deadline || "";

  showSuccessToast("已填入表单，请核对后点确定保存");
  return true;
}

function openJobEdit(job: jobService.Job | null) {
  editingJob.value = job;
  aiRawText.value = "";
  if (job) {
    Object.assign(jobForm, {
      company_id: job.company.id,
      recruit_type: job.recruit_type,
      education_requirement: job.education_requirement || "UNSPECIFIED",
      position: job.position,
      link: job.link || "",
      location: job.location || "",
      extra: job.extra || "",
      deadline: job.deadline || "",
    });
  } else {
    Object.assign(jobForm, {
      company_id: 0,
      recruit_type: "INTERN",
      education_requirement: "UNSPECIFIED",
      position: "",
      link: "",
      location: "",
      extra: "",
      deadline: "",
    });
  }
  showJobEdit.value = true;
}

/** 返回 true 表示可关闭弹窗 */
async function saveJob(): Promise<boolean> {
  try {
    if (!jobForm.company_id || !jobForm.position.trim()) {
      showToast("公司ID和职位名称不能为空");
      return false;
    }
    const trimmedDeadline = (jobForm.deadline || "").trim();
    const payload: jobService.JobPayload = {
      company_id: Number(jobForm.company_id) || 0,
      recruit_type: jobForm.recruit_type,
      education_requirement: jobForm.education_requirement,
      position: jobForm.position,
      link: jobForm.link || null,
      location: jobForm.location || null,
      extra: jobForm.extra || null,
      deadline: trimmedDeadline || null,
    };
    if (editingJob.value) {
      // 编辑：若用户清空了截止时间且原本有值，显式告诉后端清掉。
      if (!trimmedDeadline && editingJob.value.deadline) {
        payload.clear_deadline = true;
      }
      const res = await jobService.updateJob(editingJob.value.id, payload);
      if (!isSuccessResponse(res)) {
        showToast(res.message || "更新失败");
        return false;
      }
      showSuccessToast("更新成功");
    } else {
      const res = await jobService.createJob(payload);
      if (!isSuccessResponse(res)) {
        showToast(res.message || "发布失败");
        return false;
      }
      showSuccessToast("发布成功");
    }
    applySearch();
    return true;
  } catch {
    showToast("操作失败");
    return false;
  }
}

function onJobDialogBeforeClose(action: string) {
  if (action === "cancel") return true;
  return saveJob();
}

function handleDeleteJob(id: number) {
  showConfirmDialog({ title: "确认删除", message: "确定要删除这个职位吗？" })
    .then(async () => {
      const res = await jobService.deleteJob(id);
      if (!isSuccessResponse(res)) {
        showToast(res.message || "删除失败");
        return;
      }
      showSuccessToast("删除成功");
      applySearch();
    })
    .catch(() => {});
}

onMounted(async () => {
  if (!assertAdmin()) return;
  applySearch();
});

onUnmounted(() => {
  jobObserver?.disconnect();
});
</script>

<!-- Teleport 到 body，需非 scoped 才能加宽默认约 320px 的弹窗 -->
<style>
.admin-editor-wide-dialog {
  width: min(820px, 94vw) !important;
  max-width: 96vw;
}
</style>
