<template>
  <AdminManageLayout
    v-model:keyword="keyword"
    v-model:refreshing="refreshing"
    title="企业管理"
    search-placeholder="搜索企业名称"
    @back="goBack"
    @refresh="onRefresh"
    @apply-search="applySearch"
  >
    <template #toolbar>
      <van-button size="small" type="primary" icon="plus" @click="openEdit(null)">新建企业</van-button>
    </template>

    <van-cell-group :border="false">
      <van-cell
        v-for="c in list"
        :key="c.id"
        :title="c.name"
        :label="labelCompanyType(c.type) + (c.description ? ' · 已填写简介' : '')"
      >
        <template #value>
          <div class="flex flex-col gap-1 items-end shrink-0" @click.stop>
            <van-button size="small" type="primary" plain @click="openEdit(c)">编辑</van-button>
            <van-button size="small" type="danger" plain @click="handleDelete(c)">删除</van-button>
          </div>
        </template>
      </van-cell>
    </van-cell-group>
    <van-empty v-if="!loading && list.length === 0" class="py-10" description="暂无企业" />

    <template #after-card>
      <div ref="sentinel" class="h-6" />
      <div v-if="loading" class="text-center text-gray-400 py-3 text-sm">加载中…</div>
      <div v-else-if="!hasMore && list.length" class="text-center text-gray-400 py-3 text-sm">没有更多了</div>
    </template>
  </AdminManageLayout>

  <van-dialog
    v-model:show="showEdit"
    class="admin-editor-wide-dialog"
    width="min(820px, 94vw)"
    :title="editingCompany?.id ? '编辑企业' : '新建企业'"
    show-cancel-button
    :before-close="onDialogBeforeClose"
  >
    <van-form class="p-4 max-h-[70vh] overflow-y-auto">
      <van-field v-model="form.name" label="企业名称" placeholder="请输入企业名称" />
      <van-field
        :model-value="COMPANY_TYPE_LABELS[form.type]"
        is-link
        readonly
        label="企业类型"
        placeholder="点击选择"
        @click="showTypePicker = true"
      />
      <van-popup v-model:show="showTypePicker" position="bottom" round safe-area-inset-bottom>
        <van-picker
          :columns="typeColumns"
          @confirm="onConfirmType"
          @cancel="showTypePicker = false"
        />
      </van-popup>
      <van-field
        v-model="form.description"
        type="textarea"
        rows="8"
        maxlength="8000"
        show-word-limit
        label="企业简介"
        label-align="top"
        placeholder="填写企业介绍，将在资讯页「企业简介」中展示；可不填。"
      />
    </van-form>
  </van-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, reactive } from "vue";
import { useRouter } from "vue-router";
import {
  showSuccessToast,
  showFailToast,
  showToast,
  showConfirmDialog,
} from "vant";
import AdminManageLayout from "@/components/admin/AdminManageLayout.vue";
import * as auth from "@/services/auth";
import {
  searchCompany,
  createCompany,
  updateCompany,
  deleteCompany,
  labelCompanyType,
  COMPANY_TYPE_LABELS,
  type Company,
  type CompanyType,
} from "@/services/company";
import { isSuccessResponse } from "@/utils/request";

const router = useRouter();

const keyword = ref("");
const list = ref<Company[]>([]);
const page = ref(1);
const pageSize = 15;
const loading = ref(false);
const hasMore = ref(true);
const refreshing = ref(false);

const sentinel = ref<HTMLElement | null>(null);
let listObserver: IntersectionObserver | null = null;

const showEdit = ref(false);
const editingCompany = ref<Company | null>(null);
const saving = ref(false);
const showTypePicker = ref(false);

const form = reactive({
  name: "",
  type: "PRIVATE" as CompanyType,
  description: "",
});

const typeColumns = [
  { text: COMPANY_TYPE_LABELS.STATE, value: "STATE" as CompanyType },
  { text: COMPANY_TYPE_LABELS.PRIVATE, value: "PRIVATE" },
  { text: COMPANY_TYPE_LABELS.FOREIGN, value: "FOREIGN" },
];

function onConfirmType({
  selectedOptions,
}: {
  selectedOptions: { value: CompanyType }[];
}) {
  const opt = selectedOptions?.[0];
  if (opt?.value) form.type = opt.value;
  showTypePicker.value = false;
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

function setupListObserver() {
  listObserver?.disconnect();
  listObserver = new IntersectionObserver((entries) => {
    const hit = entries.find((e) => e.isIntersecting);
    if (hit) loadMore();
  });
  if (sentinel.value) listObserver.observe(sentinel.value);
}

async function loadMore() {
  if (loading.value || !hasMore.value) return;
  loading.value = true;
  try {
    const res = await searchCompany({
      page: page.value,
      page_size: pageSize,
      ...(keyword.value.trim() ? { keyword: keyword.value.trim() } : {}),
    });
    if (!isSuccessResponse(res)) {
      showFailToast(res.message || "加载失败");
      return;
    }
    const chunk = res.data?.content || [];
    list.value.push(...chunk);
    if (chunk.length < pageSize) {
      hasMore.value = false;
    } else {
      page.value += 1;
    }
  } catch {
    showFailToast("加载失败");
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
}

async function resetAndLoad() {
  page.value = 1;
  hasMore.value = true;
  list.value = [];
  await nextTick();
  setupListObserver();
  await loadMore();
}

function applySearch() {
  resetAndLoad();
}

function onRefresh() {
  resetAndLoad();
}

function openEdit(c: Company | null) {
  editingCompany.value = c;
  if (c) {
    form.name = c.name;
    form.type = (c.type as CompanyType) || "PRIVATE";
    form.description = c.description?.trim() ? String(c.description) : "";
  } else {
    form.name = "";
    form.type = "PRIVATE";
    form.description = "";
  }
  showEdit.value = true;
}

async function saveCompany(): Promise<boolean> {
  const name = form.name.trim();
  if (!name) {
    showToast("请输入企业名称");
    return false;
  }
  saving.value = true;
  try {
    const rawDesc = form.description.trim();

    if (editingCompany.value) {
      const res = await updateCompany(editingCompany.value.id, {
        name,
        type: form.type,
        description: rawDesc,
      });
      if (!isSuccessResponse(res)) {
        showFailToast(res.message || "保存失败");
        return false;
      }
      showSuccessToast("已保存");
    } else {
      const res = await createCompany({
        name,
        type: form.type,
        description: rawDesc || null,
      });
      if (!isSuccessResponse(res)) {
        showFailToast(res.message || "创建失败");
        return false;
      }
      showSuccessToast("已创建");
    }
    showEdit.value = false;
    await resetAndLoad();
    return true;
  } catch {
    showFailToast("操作失败");
    return false;
  } finally {
    saving.value = false;
  }
}

function onDialogBeforeClose(action: string) {
  if (action === "cancel") return true;
  return saveCompany();
}

function handleDelete(c: Company) {
  showConfirmDialog({
    title: "确认删除",
    message: `确定删除企业「${c.name}」吗？若仍有职位或活动关联该企业，删除可能失败。`,
  })
    .then(async () => {
      const res = await deleteCompany(c.id);
      if (!isSuccessResponse(res)) {
        showFailToast(res.message || "删除失败");
        return;
      }
      showSuccessToast("已删除");
      await resetAndLoad();
    })
    .catch(() => {});
}

onMounted(async () => {
  if (!assertAdmin()) return;
  await nextTick();
  await resetAndLoad();
});

onUnmounted(() => {
  listObserver?.disconnect();
});
</script>

<style>
.admin-editor-wide-dialog {
  width: min(820px, 94vw) !important;
  max-width: 96vw;
}
</style>
