<template>
  <van-nav-bar title="问卷 CSV 导入" left-arrow @click-left="goBack" />

  <div class="min-h-screen bg-[#f7f8fa] pb-28">
    <div class="mx-auto max-w-[min(820px,96vw)] px-3 pt-3 space-y-3">
      <div class="bg-white rounded-2xl shadow-sm p-4 text-sm text-gray-600 leading-relaxed">
        <p class="font-semibold text-gray-800 mb-2">导入流程</p>
        <ol class="list-decimal pl-5 space-y-1">
          <li>选择 CSV 并解析预览</li>
          <li>展开每一行，核对并可直接修改字段</li>
          <li>勾选要导入的行，确认后写入数据库</li>
        </ol>
      </div>

      <div class="bg-white rounded-2xl shadow-sm p-4">
        <input
          ref="fileInputRef"
          type="file"
          accept=".csv,text/csv"
          class="hidden"
          @change="onFileSelected"
        />
        <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
          <van-button block type="primary" plain icon="plus" @click="pickFile">
            {{ selectedFile ? "重新选择 CSV" : "选择 CSV 文件" }}
          </van-button>
          <van-button
            block
            type="primary"
            icon="search"
            :loading="previewing"
            :disabled="!selectedFile"
            @click="runPreview"
          >
            解析预览
          </van-button>
        </div>
        <p v-if="selectedFile" class="mt-3 text-sm text-gray-500 break-all">
          已选：{{ selectedFile.name }}（{{ formatSize(selectedFile.size) }}）
        </p>
      </div>

      <template v-if="editableRows.length">
        <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
          <div class="px-4 py-3 border-b border-gray-100">
            <div class="font-semibold text-gray-800">解析预览 · 可编辑</div>
            <div class="text-xs text-gray-400 mt-1 break-all">{{ previewFileName }}</div>
          </div>
          <div class="grid grid-cols-3 text-center py-4 text-sm">
            <div>
              <div class="text-lg font-bold text-gray-800">{{ editableRows.length }}</div>
              <div class="text-gray-400">总行数</div>
            </div>
            <div>
              <div class="text-lg font-bold text-emerald-600">{{ validRowCount }}</div>
              <div class="text-gray-400">可导入</div>
            </div>
            <div>
              <div class="text-lg font-bold text-red-500">{{ invalidRowCount }}</div>
              <div class="text-gray-400">有问题</div>
            </div>
          </div>

          <div class="px-4 pb-3 flex items-center justify-between text-sm border-b border-gray-50">
            <van-checkbox v-model="selectAll" :indeterminate="indeterminate">
              全选可导入行
            </van-checkbox>
            <span class="text-gray-400">已选 {{ selectedCount }} 行</span>
          </div>
        </div>

        <van-collapse v-model="expandedRows" :border="false">
          <van-collapse-item
            v-for="row in editableRows"
            :key="row.row_number"
            :name="row.row_number"
            class="mb-3 !rounded-2xl overflow-hidden bg-white shadow-sm border border-gray-100"
            :class="row.valid ? '' : '!border-red-100'"
          >
              <template #title>
                <div class="flex items-start gap-2 py-0.5" @click.stop>
                  <van-checkbox
                    :model-value="row.selected"
                    :disabled="!row.valid || (row.has_conflict && !row.conflict_action)"
                    @click.stop
                    @update:model-value="(v: boolean) => setRowSelected(row.row_number, v)"
                  />
                  <div class="min-w-0 flex-1 text-left">
                    <div class="text-[15px] font-medium text-gray-800">
                      第 {{ row.row_label || row.row_number }} 行 · {{ row.form.company_name || "—" }}
                    </div>
                    <div class="text-xs text-gray-500 mt-0.5">{{ row.summary }}</div>
                    <div v-if="!row.valid && row.error_message" class="text-xs text-red-500 mt-1">
                      {{ row.error_message }}
                    </div>
                  </div>
                  <van-tag
                    :type="row.has_conflict && !row.conflict_action ? 'warning' : row.valid ? 'primary' : 'danger'"
                    plain
                    class="shrink-0"
                  >
                    {{
                      row.has_conflict && !row.conflict_action
                        ? "待确认"
                        : row.valid
                          ? labelPreviewAction(row.planned_action)
                          : "需修正"
                    }}
                  </van-tag>
                </div>
              </template>

              <div class="px-1 pb-3">
                <div
                  v-if="row.has_conflict"
                  class="mx-2 mb-3 rounded-xl border border-amber-200 bg-amber-50 p-3"
                >
                  <div class="text-sm font-semibold text-amber-900 mb-2">
                    与网站已有数据冲突，请对比后选择处理方式
                  </div>

                  <div
                    v-if="row.existing_company && isCompanyRow(row.form)"
                    class="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3 text-sm"
                  >
                    <div class="rounded-lg bg-white/80 p-3 border border-gray-100">
                      <div class="text-xs font-medium text-gray-500 mb-2">网站现有</div>
                      <div class="space-y-1.5 text-gray-700">
                        <div><span class="text-gray-400">企业类型：</span>{{ displayField(row.existing_company.type_label) }}</div>
                        <div class="whitespace-pre-wrap">
                          <span class="text-gray-400">企业简介：</span>{{ displayField(row.existing_company.description) }}
                        </div>
                      </div>
                    </div>
                    <div class="rounded-lg bg-white p-3 border border-sky-100">
                      <div class="text-xs font-medium text-sky-600 mb-2">问卷 / 编辑后</div>
                      <div class="space-y-1.5 text-gray-800">
                        <div><span class="text-gray-400">企业类型：</span>{{ displayField(row.form.company_type) }}</div>
                        <div class="whitespace-pre-wrap">
                          <span class="text-gray-400">企业简介：</span>{{ displayField(proposedCompanyDescription(row.form)) }}
                        </div>
                      </div>
                    </div>
                  </div>

                  <div
                    v-if="row.existing_job && !isCompanyRow(row.form)"
                    class="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3 text-sm"
                  >
                    <div class="rounded-lg bg-white/80 p-3 border border-gray-100">
                      <div class="text-xs font-medium text-gray-500 mb-2">网站现有职位</div>
                      <div class="space-y-1.5 text-gray-700">
                        <div><span class="text-gray-400">职位：</span>{{ displayField(row.existing_job.position) }}</div>
                        <div><span class="text-gray-400">招聘类型：</span>{{ displayField(row.existing_job.recruit_type_label) }}</div>
                        <div><span class="text-gray-400">学历：</span>{{ displayField(row.existing_job.education_label) }}</div>
                        <div><span class="text-gray-400">地点：</span>{{ displayField(row.existing_job.location) }}</div>
                        <div><span class="text-gray-400">链接：</span>{{ displayField(row.existing_job.link) }}</div>
                        <div><span class="text-gray-400">截止：</span>{{ displayField(row.existing_job.deadline) }}</div>
                        <div class="whitespace-pre-wrap">
                          <span class="text-gray-400">JD：</span>{{ displayField(row.existing_job.extra) }}
                        </div>
                      </div>
                    </div>
                    <div class="rounded-lg bg-white p-3 border border-sky-100">
                      <div class="text-xs font-medium text-sky-600 mb-2">问卷 / 编辑后</div>
                      <div class="space-y-1.5 text-gray-800">
                        <div><span class="text-gray-400">职位：</span>{{ displayField(row.form.position) }}</div>
                        <div><span class="text-gray-400">招聘类型：</span>{{ displayField(row.form.recruit_type) }}</div>
                        <div><span class="text-gray-400">学历：</span>{{ displayField(row.form.education || "不限") }}</div>
                        <div><span class="text-gray-400">地点：</span>{{ displayField(row.form.location) }}</div>
                        <div><span class="text-gray-400">链接：</span>{{ displayField(row.form.link) }}</div>
                        <div><span class="text-gray-400">截止：</span>{{ displayField(row.form.deadline) }}</div>
                        <div class="whitespace-pre-wrap">
                          <span class="text-gray-400">JD：</span>{{ displayField(row.form.job_extra) }}
                        </div>
                      </div>
                    </div>
                  </div>

                  <van-radio-group
                    :model-value="row.conflict_action ?? ''"
                    direction="vertical"
                    class="conflict-radio-group"
                    @update:model-value="(v: string) => setConflictAction(row.row_number, v as ConflictAction)"
                  >
                    <van-radio v-for="opt in CONFLICT_ACTION_OPTIONS" :key="opt.name" :name="opt.name">
                      {{ opt.label }}
                    </van-radio>
                  </van-radio-group>
                </div>

                <van-form class="import-row-form">
                  <van-field
                    :model-value="row.form.info_type"
                    is-link
                    readonly
                    label="招聘信息类型"
                    placeholder="点击选择"
                    @click="openPicker(row.row_number, 'info_type')"
                  />
                  <van-field
                    v-model="row.form.company_name"
                    label="企业全称"
                    placeholder="与营业执照一致"
                    @update:model-value="() => onRowEdit(row.row_number)"
                  />
                  <van-field
                    v-model="row.form.company_short_name"
                    label="企业简称"
                    placeholder="可选，当前不入库"
                    @update:model-value="() => onRowEdit(row.row_number)"
                  />
                  <van-field
                    :model-value="row.form.company_type || ''"
                    is-link
                    readonly
                    label="企业类型"
                    placeholder="点击选择"
                    @click="openPicker(row.row_number, 'company_type')"
                  />
                  <van-field
                    v-model="row.form.company_website"
                    label="公司官网"
                    placeholder="可选，当前不入库"
                    @update:model-value="() => onRowEdit(row.row_number)"
                  />

                  <template v-if="isCompanyRow(row.form)">
                    <van-field
                      v-model="row.form.company_tagline"
                      label="公司描述"
                      placeholder="短描述"
                      @update:model-value="() => onRowEdit(row.row_number)"
                    />
                    <van-field
                      v-model="row.form.company_intro"
                      type="textarea"
                      rows="4"
                      autosize
                      maxlength="8000"
                      show-word-limit
                      label="公司简介"
                      label-align="top"
                      placeholder="写入企业管理中的企业简介"
                      @update:model-value="() => onRowEdit(row.row_number)"
                    />
                    <van-field
                      v-model="row.form.location"
                      label="工作地点"
                      placeholder="公司整体宣传时仅作核对，不入库"
                      @update:model-value="() => onRowEdit(row.row_number)"
                    />
                    <van-field
                      v-model="row.form.link"
                      label="投递链接"
                      placeholder="公司整体宣传时仅作核对，不入库"
                      @update:model-value="() => onRowEdit(row.row_number)"
                    />
                  </template>

                  <template v-else>
                    <van-field
                      v-model="row.form.position"
                      label="职位名称"
                      placeholder="必填"
                      @update:model-value="() => onRowEdit(row.row_number)"
                    />
                    <van-field
                      :model-value="row.form.recruit_type || ''"
                      is-link
                      readonly
                      label="招聘类型"
                      placeholder="点击选择"
                      @click="openPicker(row.row_number, 'recruit_type')"
                    />
                    <van-field
                      v-model="row.form.job_extra"
                      type="textarea"
                      rows="5"
                      autosize
                      maxlength="8000"
                      show-word-limit
                      label="职位描述（JD）"
                      label-align="top"
                      placeholder="岗位介绍、职责与要求"
                      @update:model-value="() => onRowEdit(row.row_number)"
                    />
                    <van-field
                      :model-value="row.form.education || '不限'"
                      is-link
                      readonly
                      label="学历要求"
                      placeholder="点击选择"
                      @click="openPicker(row.row_number, 'education')"
                    />
                    <van-field
                      v-model="row.form.location"
                      label="工作地点"
                      placeholder="多城用顿号分隔，如：上海、杭州"
                      @update:model-value="() => onRowEdit(row.row_number)"
                    />
                    <van-field
                      v-model="row.form.link"
                      label="投递链接"
                      placeholder="网申 / 投递 URL"
                      @update:model-value="() => onRowEdit(row.row_number)"
                    />
                    <van-field
                      v-model="row.form.deadline"
                      label="投递截止日期"
                      placeholder="如 2026/6/30 或 2026-06-30"
                      @update:model-value="() => onRowEdit(row.row_number)"
                    />
                  </template>
                </van-form>
              </div>
            </van-collapse-item>
        </van-collapse>

        <div class="bg-white rounded-2xl shadow-sm p-4 sticky bottom-3 z-10">
          <van-button
            block
            type="primary"
            icon="passed"
            :loading="importing"
            :disabled="selectedCount === 0"
            @click="confirmImport"
          >
            确认导入 {{ selectedCount }} 行
          </van-button>
        </div>
      </template>

      <div v-if="result" class="bg-white rounded-2xl shadow-sm overflow-hidden">
        <div class="px-4 py-3 border-b border-gray-100">
          <div class="font-semibold text-gray-800">导入结果</div>
        </div>
        <div class="grid grid-cols-3 text-center py-4 text-sm">
          <div>
            <div class="text-lg font-bold text-gray-800">{{ result.total_rows }}</div>
            <div class="text-gray-400">总行数</div>
          </div>
          <div>
            <div class="text-lg font-bold text-emerald-600">{{ result.success_count }}</div>
            <div class="text-gray-400">成功</div>
          </div>
          <div>
            <div class="text-lg font-bold text-red-500">{{ result.failed_count }}</div>
            <div class="text-gray-400">失败</div>
          </div>
        </div>
        <van-cell-group :border="false">
          <van-cell
            v-for="row in result.rows"
            :key="row.row_number"
            :title="`第 ${row.row_label || row.row_number} 行`"
            :label="row.message"
          >
            <template #value>
              <van-tag :type="row.success ? 'success' : 'danger'" plain>
                {{ row.success ? labelImportAction(row.action) : "失败" }}
              </van-tag>
            </template>
          </van-cell>
        </van-cell-group>
      </div>
    </div>
  </div>

  <van-popup v-model:show="picker.show" position="bottom" round safe-area-inset-bottom>
    <van-picker
      :columns="picker.columns"
      @confirm="onPickerConfirm"
      @cancel="picker.show = false"
    />
  </van-popup>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { showConfirmDialog, showFailToast, showSuccessToast, showToast } from "vant";
import * as auth from "@/services/auth";
import {
  previewSurveyCsv,
  confirmSurveyImport,
  labelPreviewAction,
  labelImportAction,
  toEditableRow,
  revalidateEditableRow,
  isCompanyRow,
  displayField,
  proposedCompanyDescription,
  INFO_TYPE_OPTIONS,
  COMPANY_TYPE_OPTIONS,
  RECRUIT_TYPE_OPTIONS,
  EDUCATION_OPTIONS,
  CONFLICT_ACTION_OPTIONS,
  type ConflictAction,
  type EditableImportRow,
  type SurveyImportResponse,
} from "@/services/surveyImport";
import { isSuccessResponse } from "@/utils/request";

type PickerField = "info_type" | "company_type" | "recruit_type" | "education";

const router = useRouter();
const fileInputRef = ref<HTMLInputElement | null>(null);
const selectedFile = ref<File | null>(null);
const previewing = ref(false);
const importing = ref(false);
const previewFileName = ref("");
const editableRows = ref<EditableImportRow[]>([]);
const expandedRows = ref<number[]>([]);
const result = ref<SurveyImportResponse | null>(null);

const picker = ref<{
  show: boolean;
  rowNumber: number;
  field: PickerField;
  columns: { text: string; value: string }[];
}>({
  show: false,
  rowNumber: 0,
  field: "info_type",
  columns: [],
});

const validRowCount = computed(() => editableRows.value.filter((r) => r.valid).length);
const invalidRowCount = computed(() => editableRows.value.filter((r) => !r.valid).length);
const selectedCount = computed(() => editableRows.value.filter((r) => r.selected).length);

const selectAll = computed({
  get() {
    const ok = editableRows.value.filter((r) => r.valid && (!r.has_conflict || r.conflict_action));
    if (ok.length === 0) return false;
    return ok.every((r) => r.selected);
  },
  set(checked: boolean) {
    editableRows.value = editableRows.value.map((r) => {
      const canSelect = r.valid && (!r.has_conflict || !!r.conflict_action);
      return canSelect ? { ...r, selected: checked } : { ...r, selected: false };
    });
  },
});

const indeterminate = computed(() => {
  const ok = editableRows.value.filter((r) => r.valid && (!r.has_conflict || r.conflict_action));
  if (ok.length === 0) return false;
  const n = ok.filter((r) => r.selected).length;
  return n > 0 && n < ok.length;
});

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

function pickFile() {
  fileInputRef.value?.click();
}

function onFileSelected(e: Event) {
  const input = e.target as HTMLInputElement;
  selectedFile.value = input.files?.[0] ?? null;
  editableRows.value = [];
  expandedRows.value = [];
  result.value = null;
  if (input) input.value = "";
}

function formatSize(bytes: number) {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

function findRowIndex(rowNumber: number) {
  return editableRows.value.findIndex((r) => r.row_number === rowNumber);
}

function onRowEdit(rowNumber: number) {
  const idx = findRowIndex(rowNumber);
  if (idx < 0) return;
  editableRows.value[idx] = revalidateEditableRow(editableRows.value[idx]);
}

function setConflictAction(rowNumber: number, action: ConflictAction) {
  const idx = findRowIndex(rowNumber);
  if (idx < 0) return;
  editableRows.value[idx] = revalidateEditableRow({
    ...editableRows.value[idx],
    conflict_action: action,
    selected: action !== "SKIP",
  });
}

function setRowSelected(rowNumber: number, selected: boolean) {
  const idx = findRowIndex(rowNumber);
  if (idx < 0) return;
  const row = editableRows.value[idx];
  const canSelect = row.valid && (!row.has_conflict || !!row.conflict_action);
  if (!canSelect) return;
  editableRows.value[idx] = { ...row, selected };
}

function openPicker(rowNumber: number, field: PickerField) {
  const columns =
    field === "info_type"
      ? INFO_TYPE_OPTIONS
      : field === "company_type"
        ? [{ text: "（不指定）", value: "" }, ...COMPANY_TYPE_OPTIONS]
        : field === "recruit_type"
          ? RECRUIT_TYPE_OPTIONS
          : EDUCATION_OPTIONS;
  picker.value = { show: true, rowNumber, field, columns };
}

function onPickerConfirm({
  selectedOptions,
}: {
  selectedOptions: { text: string; value: string }[];
}) {
  const opt = selectedOptions?.[0];
  if (!opt) {
    picker.value.show = false;
    return;
  }
  const idx = findRowIndex(picker.value.rowNumber);
  if (idx >= 0) {
    const form = { ...editableRows.value[idx].form };
    switch (picker.value.field) {
      case "info_type":
        form.info_type = opt.value;
        break;
      case "company_type":
        form.company_type = opt.value;
        break;
      case "recruit_type":
        form.recruit_type = opt.value;
        break;
      case "education":
        form.education = opt.value === "不限" ? "" : opt.value;
        break;
    }
    editableRows.value[idx] = revalidateEditableRow({
      ...editableRows.value[idx],
      form,
    });
  }
  picker.value.show = false;
}

async function runPreview() {
  if (!selectedFile.value) {
    showToast("请先选择 CSV 文件");
    return;
  }
  previewing.value = true;
  result.value = null;
  try {
    const res = await previewSurveyCsv(selectedFile.value);
    if (!isSuccessResponse(res)) {
      showFailToast(res.message || "解析失败");
      return;
    }
    previewFileName.value = res.data.file_name;
    editableRows.value = res.data.rows.map(toEditableRow);
    expandedRows.value = res.data.rows.map((r) => r.row_number);
    const conflicts = res.data.rows.filter((r) => r.has_conflict).length;
    const invalid = invalidRowCount.value;
    if (conflicts > 0) {
      showToast(`解析完成：${conflicts} 行与网站数据冲突，请对比并选择处理方式`);
    } else if (invalid > 0) {
      showToast(`解析完成：${validRowCount.value} 行可导入，${invalid} 行需修正`);
    } else {
      showSuccessToast(`解析完成，请核对 ${editableRows.value.length} 行内容`);
    }
  } catch {
    // request 已 toast
  } finally {
    previewing.value = false;
  }
}

async function confirmImport() {
  const unresolved = editableRows.value.filter(
    (r) => r.selected && r.has_conflict && !r.conflict_action
  );
  if (unresolved.length > 0) {
    showToast("请先为已勾选冲突行选择处理方式");
    return;
  }

  const selected = editableRows.value.filter(
    (r) => r.selected && r.valid && (!r.has_conflict || r.conflict_action)
  );
  if (selected.length === 0) {
    showToast("请至少选择一行有效数据");
    return;
  }

  try {
    await showConfirmDialog({
      title: "确认导入",
      message: `确定将 ${selected.length} 行（已编辑内容）写入数据库吗？`,
    });
  } catch {
    return;
  }

  importing.value = true;
  try {
    const res = await confirmSurveyImport({
      file_name: previewFileName.value,
      rows: selected.map((r) => ({
        ...r.form,
        conflict_action: r.conflict_action ?? r.form.conflict_action,
      })),
    });
    if (!isSuccessResponse(res)) {
      showFailToast(res.message || "导入失败");
      return;
    }
    result.value = res.data;
    if (res.data.failed_count === 0) {
      showSuccessToast(`导入完成，共 ${res.data.success_count} 行成功`);
    } else {
      showToast(`导入完成：成功 ${res.data.success_count}，失败 ${res.data.failed_count}`);
    }
  } catch {
    // request 已 toast
  } finally {
    importing.value = false;
  }
}

onMounted(() => {
  assertAdmin();
});
</script>

<style scoped>
.import-row-form :deep(.van-field__label) {
  width: 6.2em;
}
.conflict-radio-group :deep(.van-radio) {
  margin-bottom: 8px;
}
</style>
