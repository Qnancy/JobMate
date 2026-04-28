<template>
  <van-nav-bar title="活动管理" left-arrow @click-left="goBack" />

  <div class="min-h-screen bg-w pt-2 px-3 pb-28">
    <div class="mx-auto max-w-[min(820px,96vw)] flex justify-end mb-3">
      <van-button size="small" type="primary" icon="plus" @click="openActivityEdit(null)">发布活动</van-button>
    </div>

    <div class="mx-auto max-w-[min(820px,96vw)] bg-white rounded-2xl shadow-sm">
      <van-cell-group :border="false">
        <van-cell
          v-for="act in activityList"
          :key="act.id"
          :title="act.title"
          :label="`${ACTIVITY_TYPE_LABELS[act.type] || '宣讲会'} · ${act.time} · ${act.location || '暂无地点'}`"
        >
          <template #value>
            <div class="flex flex-col gap-1 items-end shrink-0" @click.stop>
              <van-button size="small" type="primary" plain @click="openActivityEdit(act)">编辑</van-button>
              <van-button size="small" type="danger" plain @click="handleDeleteActivity(act.id)">删除</van-button>
            </div>
          </template>
        </van-cell>
      </van-cell-group>
      <div ref="activitySentinel" class="h-6" />
      <div v-if="activityLoading" class="text-center text-gray-400 py-3">加载中...</div>
          <div v-else-if="!activityHasMore && activityList.length" class="text-center text-gray-400 py-3">没有更多</div>
      <div v-if="!activityLoading && activityList.length === 0" class="text-center text-gray-400 py-12">暂无活动数据</div>
    </div>

    <van-dialog
      v-model:show="showActivityEdit"
      class="admin-editor-wide-dialog"
      width="min(820px, 94vw)"
      :title="editingActivity?.id ? '编辑活动' : '发布活动'"
      show-cancel-button
      :before-close="onActivityDialogBeforeClose"
    >
      <van-form class="p-4 max-h-[70vh] overflow-y-auto">
        <van-field
          v-model="aiRawText"
          type="textarea"
          rows="3"
          autosize
          label="宣传原文"
          label-align="top"
          placeholder="可粘贴活动通知全文（宣讲会/双选会/名企探访），点击下方按钮由 AI 解析并填入下方表单（不会自动保存）"
        />
        <van-button
          class="mb-3"
          block
          type="primary"
          plain
          size="small"
          :loading="aiParsing"
          :disabled="!aiRawText.trim()"
          @click="runAiParseActivity"
        >
          AI 识别并填入表单
        </van-button>
        <van-field v-model.number="activityForm.company_id" type="digit" label="公司ID" placeholder="请输入公司ID" />
        <van-field v-model="activityForm.title" label="标题" placeholder="请输入活动标题" />
        <van-field v-model="activityForm.time" label="时间" placeholder="例如: 2024-05-20 14:00" />
        <van-field
          :model-value="ACTIVITY_TYPE_LABELS[activityForm.type]"
          is-link
          readonly
          name="activity_type"
          label="活动类型"
          placeholder="点击选择活动类型"
          @click="showActivityTypePicker = true"
        />
        <van-popup v-model:show="showActivityTypePicker" position="bottom">
          <van-picker :columns="activityTypeColumns" @confirm="onConfirmActivityType" @cancel="showActivityTypePicker = false" />
        </van-popup>
        <van-field v-model="activityForm.location" label="地点" placeholder="请输入地点" />
        <van-field v-model="activityForm.link" label="链接" placeholder="请输入报名链接" />
        <van-field v-model="activityForm.extra" label="备注" placeholder="其他信息" />
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
      <div v-if="aiPreview.activity" class="p-4 max-h-[60vh] overflow-y-auto text-[13px] leading-relaxed">
        <div class="text-gray-500 mb-2">请核对 AI 识别的字段，确认后将覆盖当前表单：</div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">标题</div>
          <div class="flex-1 break-all">{{ aiPreview.activity.title || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">类型</div>
          <div class="flex-1 break-all">{{ ACTIVITY_TYPE_LABELS[aiPreview.activity.type] || aiPreview.activity.type || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">时间</div>
          <div class="flex-1 break-all">{{ aiPreview.activity.time || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">公司ID</div>
          <div class="flex-1 break-all">{{ Number(aiPreview.activity.company_id) > 0 ? aiPreview.activity.company_id : '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">公司名称</div>
          <div class="flex-1 break-all">{{ aiPreview.activity.company_name || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">地点</div>
          <div class="flex-1 break-all">{{ aiPreview.activity.location || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">链接</div>
          <div class="flex-1 break-all">{{ aiPreview.activity.link || '（未识别）' }}</div>
        </div>
        <div class="flex py-1 border-b border-dashed border-gray-200">
          <div class="w-[72px] text-gray-500 shrink-0">备注</div>
          <div class="flex-1 break-all">{{ aiPreview.activity.extra || '（未识别）' }}</div>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive, nextTick } from "vue";
import { useRouter } from "vue-router";
import { showToast, showSuccessToast } from "vant";
import * as auth from "@/services/auth";
import * as activityService from "@/services/activity";
import { ACTIVITY_TYPE_LABELS } from "@/services/activity";
import * as companyService from "@/services/company";
import * as promotionParse from "@/services/promotionParse";
import { isSuccessResponse } from "@/utils/request";

const showActivityTypePicker = ref(false);
const activityTypeColumns: { text: string; value: activityService.ActivityType }[] = [
  { text: "宣讲会", value: "LECTURE" },
  { text: "双选会", value: "JOB_FAIR" },
  { text: "名企探访", value: "COMPANY_VISIT" },
];

function onConfirmActivityType({ selectedOptions }: { selectedOptions: { value: activityService.ActivityType }[] }) {
  const opt = selectedOptions?.[0];
  if (opt?.value) activityForm.type = opt.value;
  showActivityTypePicker.value = false;
}

const router = useRouter();

const activityList = ref<activityService.Activity[]>([]);
const activityPage = ref(1);
const activityPageSize = ref(10);
const activityLoading = ref(false);
const activityHasMore = ref(true);
const activitySentinel = ref<HTMLElement | null>(null);
let activityObserver: IntersectionObserver | null = null;

const showActivityEdit = ref(false);
const editingActivity = ref<activityService.Activity | null>(null);
const aiRawText = ref("");
const aiParsing = ref(false);

// AI 解析预览对话框状态
const showAiPreview = ref(false);
const aiCreateCompany = ref(false);
const aiPreview = reactive<{
  activity: promotionParse.ParsedActivityPromotion | null;
  warnings: string[];
  suggested_company_name: string | null;
}>({
  activity: null,
  warnings: [],
  suggested_company_name: null,
});

type ActivityForm = {
  company_id: number;
  title: string;
  time: string;
  type: activityService.ActivityType;
  link: string;
  location: string;
  extra: string;
};

const activityForm = reactive<ActivityForm>({
  company_id: 0,
  title: "",
  time: "",
  type: "LECTURE",
  link: "",
  location: "",
  extra: "",
});

function goBack() {
  router.push("/admin");
}

function assertAdmin() {
  const u = auth.currentUser();
  if (!u) {
    showToast("请先登录");
    router.replace({ path: "/my/login-admin", query: { redirect: router.currentRoute.value.fullPath } });
    return false;
  }
  if (u.role !== "ADMIN") {
    showToast("无权访问");
    router.replace({ path: "/" });
    return false;
  }
  return true;
}

function setupActivityObserver() {
  if (activityObserver) activityObserver.disconnect();
  activityObserver = new IntersectionObserver((entries) => {
    const entry = entries.find((e) => e.isIntersecting);
    if (!entry) return;
    fetchMoreActivities();
  });
  if (activitySentinel.value) activityObserver.observe(activitySentinel.value);
}

async function fetchMoreActivities() {
  if (activityLoading.value || !activityHasMore.value) return;
  activityLoading.value = true;
  try {
    const res = await activityService.getActivities({
      page: activityPage.value,
      page_size: activityPageSize.value,
    });
    if (isSuccessResponse(res)) {
      const list = res.data?.content || [];
      activityList.value.push(...list);
      if (list.length < activityPageSize.value) {
        activityHasMore.value = false;
      } else {
        activityPage.value += 1;
      }
    }
  } catch (e) {
    console.error(e);
  }
  activityLoading.value = false;
}

async function runAiParseActivity() {
  const text = aiRawText.value.trim();
  if (!text) {
    showToast("请先粘贴宣传文本");
    return;
  }
  aiParsing.value = true;
  try {
    const res = await promotionParse.parsePromotion("ACTIVITY", text);
    if (!isSuccessResponse(res) || !res.data?.activity) {
      showToast(res.message || "解析失败");
      return;
    }
    aiPreview.activity = res.data.activity;
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

async function onAiPreviewBeforeClose(action: string): Promise<boolean> {
  if (action !== "confirm") {
    return true;
  }
  const a = aiPreview.activity;
  if (!a) return true;

  let resolvedCompanyId = Number(a.company_id) || 0;

  if (aiCreateCompany.value && aiPreview.suggested_company_name) {
    const name = aiPreview.suggested_company_name.trim();
    try {
      const created = await companyService.createCompany({
        name,
        type: "PRIVATE",
      });
      if (!isSuccessResponse(created) || !created.data?.id) {
        showToast(created.message || "创建公司失败，已取消应用");
        return false;
      }
      resolvedCompanyId = created.data.id;
      showSuccessToast(`已创建公司「${name}」(ID=${resolvedCompanyId})`);
    } catch {
      return false;
    }
  }

  activityForm.company_id = resolvedCompanyId;
  activityForm.title = a.title || "";
  activityForm.time = a.time || "";
  activityForm.type = a.type && ACTIVITY_TYPE_LABELS[a.type] ? a.type : "LECTURE";
  activityForm.link = a.link || "";
  activityForm.location = a.location || "";
  activityForm.extra = a.extra || "";

  showSuccessToast("已填入表单，请核对后点确定保存");
  return true;
}

function openActivityEdit(act: activityService.Activity | null) {
  editingActivity.value = act;
  aiRawText.value = "";
  if (act) {
    Object.assign(activityForm, {
      company_id: act.company.id,
      title: act.title,
      time: act.time,
      type: act.type && ACTIVITY_TYPE_LABELS[act.type] ? act.type : "LECTURE",
      link: act.link || "",
      location: act.location || "",
      extra: act.extra || "",
    });
  } else {
    Object.assign(activityForm, {
      company_id: 0,
      title: "",
      time: "",
      type: "LECTURE",
      link: "",
      location: "",
      extra: "",
    });
  }
  showActivityEdit.value = true;
}

async function saveActivity(): Promise<boolean> {
  try {
    if (!activityForm.company_id || !activityForm.title.trim() || !activityForm.time.trim()) {
      showToast("公司ID、标题和时间不能为空");
      return false;
    }
    const payload: activityService.ActivityPayload = {
      company_id: Number(activityForm.company_id) || 0,
      title: activityForm.title,
      time: activityForm.time,
      type: activityForm.type,
      link: activityForm.link || null,
      location: activityForm.location || null,
      extra: activityForm.extra || null,
    };
    if (editingActivity.value) {
      const res = await activityService.updateActivity(editingActivity.value.id, payload);
      if (!isSuccessResponse(res)) {
        showToast(res.message || "更新失败");
        return false;
      }
      showSuccessToast("更新成功");
    } else {
      const res = await activityService.createActivity(payload);
      if (!isSuccessResponse(res)) {
        showToast(res.message || "发布失败");
        return false;
      }
      showSuccessToast("发布成功");
    }
    activityPage.value = 1;
    activityHasMore.value = true;
    activityList.value = [];
    await fetchMoreActivities();
    return true;
  } catch {
    showToast("操作失败");
    return false;
  }
}

function onActivityDialogBeforeClose(action: string) {
  if (action === "cancel") return true;
  return saveActivity();
}

function handleDeleteActivity(id: number) {
  showConfirmDialog({ title: "确认删除", message: "确定要删除这个活动吗？" })
    .then(async () => {
      const res = await activityService.deleteActivity(id);
      if (!isSuccessResponse(res)) {
        showToast(res.message || "删除失败");
        return;
      }
      showSuccessToast("删除成功");
      activityPage.value = 1;
      activityHasMore.value = true;
      activityList.value = [];
      await fetchMoreActivities();
    })
    .catch(() => {});
}

onMounted(async () => {
  if (!assertAdmin()) return;
  activityPage.value = 1;
  activityHasMore.value = true;
  activityList.value = [];
  await nextTick();
  setupActivityObserver();
  await fetchMoreActivities();
});

onUnmounted(() => {
  activityObserver?.disconnect();
});
</script>

<!-- Teleport 到 body，需非 scoped 才能加宽默认约 320px 的弹窗 -->
<style>
.admin-editor-wide-dialog {
  width: min(820px, 94vw) !important;
  max-width: 96vw;
}
</style>
