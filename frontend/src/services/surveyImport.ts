import { api } from "@/utils/request";

export const INFO_TYPE_COMPANY = "公司整体宣传";
export const INFO_TYPE_JOB = "定向岗位宣传";

export type ConflictAction = "UPDATE" | "KEEP_EXISTING" | "SKIP";

export interface SurveyImportExistingCompany {
  id: number;
  name: string;
  type?: string | null;
  type_label?: string | null;
  description?: string | null;
}

export interface SurveyImportExistingJob {
  id: number;
  position: string;
  recruit_type?: string | null;
  recruit_type_label?: string | null;
  location?: string | null;
  link?: string | null;
  extra?: string | null;
  education_label?: string | null;
  deadline?: string | null;
}

export interface SurveyImportRowPayload {
  row_number: number;
  row_label?: string | null;
  info_type: string;
  company_name: string;
  company_type?: string | null;
  company_short_name?: string | null;
  company_website?: string | null;
  company_tagline?: string | null;
  company_intro?: string | null;
  company_description?: string | null;
  position?: string | null;
  recruit_type?: string | null;
  education?: string | null;
  location?: string | null;
  link?: string | null;
  deadline?: string | null;
  job_extra?: string | null;
  conflict_action?: ConflictAction | null;
}

export interface SurveyImportPreviewRow {
  row_number: number;
  row_label: string;
  valid: boolean;
  info_type?: string | null;
  company_name?: string | null;
  company_type?: string | null;
  summary: string;
  planned_action?: string | null;
  error_message?: string | null;
  has_conflict?: boolean;
  existing_company?: SurveyImportExistingCompany | null;
  existing_job?: SurveyImportExistingJob | null;
  payload?: SurveyImportRowPayload | null;
}

export interface SurveyImportPreviewResponse {
  file_name: string;
  total_rows: number;
  valid_count: number;
  invalid_count: number;
  rows: SurveyImportPreviewRow[];
}

export interface SurveyImportRowResult {
  row_number: number;
  row_label: string;
  success: boolean;
  action?: string | null;
  message: string;
  company_id?: number | null;
  job_id?: number | null;
}

export interface SurveyImportResponse {
  file_name?: string | null;
  total_rows: number;
  success_count: number;
  failed_count: number;
  rows: SurveyImportRowResult[];
}

/** 本地可编辑行（预览后由前端维护） */
export interface EditableImportRow {
  row_number: number;
  row_label: string;
  selected: boolean;
  valid: boolean;
  has_conflict: boolean;
  summary: string;
  planned_action?: string | null;
  error_message?: string;
  conflict_action?: ConflictAction | null;
  existing_company?: SurveyImportExistingCompany | null;
  existing_job?: SurveyImportExistingJob | null;
  form: SurveyImportRowPayload;
}

export const CONFLICT_ACTION_OPTIONS: { name: ConflictAction; label: string }[] = [
  { name: "UPDATE", label: "用新内容覆盖" },
  { name: "KEEP_EXISTING", label: "保留网站现有内容" },
  { name: "SKIP", label: "跳过此行" },
];

export const INFO_TYPE_OPTIONS = [
  { text: INFO_TYPE_COMPANY, value: INFO_TYPE_COMPANY },
  { text: INFO_TYPE_JOB, value: INFO_TYPE_JOB },
];

export const COMPANY_TYPE_OPTIONS = [
  { text: "民营企业", value: "民营企业" },
  { text: "国企 / 事业单位", value: "国企 / 事业单位" },
  { text: "外资 / 合资", value: "外资 / 合资" },
];

export const RECRUIT_TYPE_OPTIONS = [
  { text: "实习", value: "实习" },
  { text: "校招", value: "校招" },
  { text: "社招", value: "社招" },
];

export const EDUCATION_OPTIONS = [
  { text: "不限", value: "不限" },
  { text: "本科及以上", value: "本科及以上" },
  { text: "硕士及以上", value: "硕士及以上" },
  { text: "博士及以上", value: "博士及以上" },
];

export const PREVIEW_ACTION_LABELS: Record<string, string> = {
  COMPANY_WILL_CREATE: "将新建企业",
  COMPANY_WILL_UPDATE: "与已有企业冲突",
  JOB_WILL_CREATE: "将新建职位",
  JOB_WILL_UPDATE: "与已有职位冲突",
  INVALID: "无法导入",
};

export const IMPORT_ACTION_LABELS: Record<string, string> = {
  COMPANY_CREATED: "新建企业",
  COMPANY_UPDATED: "覆盖企业",
  JOB_CREATED: "新建职位",
  JOB_UPDATED: "覆盖职位",
  SKIPPED: "已跳过",
  KEPT_EXISTING: "保留原样",
};

function blank(v?: string | null) {
  return v == null || v.trim() === "";
}

function norm(v?: string | null) {
  return v?.trim() ?? "";
}

export function displayField(v?: string | null) {
  return blank(v) ? "（空）" : String(v);
}

export function proposedCompanyDescription(form: SurveyImportRowPayload): string {
  const tagline = norm(form.company_tagline);
  const intro = norm(form.company_intro);
  if (intro) {
    if (tagline && tagline !== intro) return `${tagline}\n\n${intro}`;
    return intro;
  }
  if (tagline) return tagline;
  return norm(form.company_description);
}

export function payloadFromPreview(row: SurveyImportPreviewRow): SurveyImportRowPayload {
  const p = row.payload;
  return {
    row_number: row.row_number,
    row_label: row.row_label,
    info_type: norm(p?.info_type ?? row.info_type),
    company_name: norm(p?.company_name ?? row.company_name),
    company_type: p?.company_type ?? row.company_type ?? "",
    company_short_name: p?.company_short_name ?? "",
    company_website: p?.company_website ?? "",
    company_tagline: p?.company_tagline ?? "",
    company_intro: p?.company_intro ?? "",
    company_description: p?.company_description ?? "",
    position: p?.position ?? "",
    recruit_type: p?.recruit_type ?? "",
    education: p?.education ?? "",
    location: p?.location ?? "",
    link: p?.link ?? "",
    deadline: p?.deadline ?? "",
    job_extra: p?.job_extra ?? "",
    conflict_action: null,
  };
}

export function validateImportRow(
  form: SurveyImportRowPayload,
  opts?: { hasConflict?: boolean; conflictAction?: ConflictAction | null }
): {
  valid: boolean;
  error_message?: string;
  summary: string;
} {
  const companyName = norm(form.company_name);
  const infoType = norm(form.info_type);

  if (blank(companyName)) {
    return { valid: false, error_message: "企业全称不能为空", summary: "—" };
  }

  if (infoType !== INFO_TYPE_COMPANY && infoType !== INFO_TYPE_JOB) {
    return {
      valid: false,
      error_message: "招聘信息类型须为「公司整体宣传」或「定向岗位宣传」",
      summary: companyName,
    };
  }

  const companyType = norm(form.company_type);
  if (!blank(companyType) && !COMPANY_TYPE_OPTIONS.some((o) => o.value === companyType)) {
    return { valid: false, error_message: `无法识别企业类型：${companyType}`, summary: companyName };
  }

  if (infoType === INFO_TYPE_COMPANY) {
    if (opts?.hasConflict && !opts.conflictAction) {
      return {
        valid: false,
        error_message: "网站已有该企业，请选择如何处理",
        summary: `冲突 · ${companyName}`,
      };
    }
    return {
      valid: true,
      summary: opts?.hasConflict
        ? `冲突待确认 · ${companyName}`
        : `公司整体宣传 · ${companyName}`,
    };
  }

  const position = norm(form.position);
  const recruitType = norm(form.recruit_type);
  if (blank(position)) {
    return { valid: false, error_message: "定向岗位宣传需填写职位名称", summary: companyName };
  }
  if (blank(recruitType)) {
    return { valid: false, error_message: "定向岗位宣传需填写招聘类型", summary: companyName };
  }
  if (!RECRUIT_TYPE_OPTIONS.some((o) => o.value === recruitType)) {
    return { valid: false, error_message: `无法识别招聘类型：${recruitType}`, summary: companyName };
  }

  if (opts?.hasConflict && !opts.conflictAction) {
    return {
      valid: false,
      error_message: "网站已有该职位，请选择如何处理",
      summary: `冲突 · ${position}`,
    };
  }

  return {
    valid: true,
    summary: opts?.hasConflict
      ? `冲突待确认 · ${companyName} · ${position}`
      : `定向岗位宣传 · ${companyName} · ${position}（${recruitType}）`,
  };
}

export function toEditableRow(row: SurveyImportPreviewRow): EditableImportRow {
  const hasConflict = !!row.has_conflict;
  const form = payloadFromPreview(row);
  const check = validateImportRow(form, { hasConflict, conflictAction: null });
  const ready = check.valid && !hasConflict;
  return {
    row_number: row.row_number,
    row_label: row.row_label,
    selected: ready,
    valid: check.valid,
    has_conflict: hasConflict,
    summary: row.summary || check.summary,
    planned_action: row.planned_action ?? undefined,
    error_message: check.error_message ?? row.error_message ?? undefined,
    conflict_action: null,
    existing_company: row.existing_company ?? null,
    existing_job: row.existing_job ?? null,
    form,
  };
}

export function revalidateEditableRow(row: EditableImportRow): EditableImportRow {
  const check = validateImportRow(row.form, {
    hasConflict: row.has_conflict,
    conflictAction: row.conflict_action,
  });
  const form = { ...row.form, conflict_action: row.conflict_action ?? null };
  const canSelect = check.valid && (!row.has_conflict || !!row.conflict_action);
  return {
    ...row,
    form,
    valid: check.valid,
    summary: row.has_conflict && !row.conflict_action ? check.summary : check.summary,
    error_message: check.error_message,
    selected: row.selected && canSelect,
  };
}

export function labelPreviewAction(action?: string | null): string {
  if (!action) return "—";
  return PREVIEW_ACTION_LABELS[action] ?? action;
}

export function labelImportAction(action?: string | null): string {
  if (!action) return "—";
  return IMPORT_ACTION_LABELS[action] ?? action;
}

export function isCompanyRow(form: SurveyImportRowPayload) {
  return norm(form.info_type) === INFO_TYPE_COMPANY;
}

export async function previewSurveyCsv(file: File) {
  const form = new FormData();
  form.append("file", file);
  return api.postForm<SurveyImportPreviewResponse>("/admin/survey/preview", form);
}

export async function confirmSurveyImport(payload: {
  file_name?: string;
  rows: SurveyImportRowPayload[];
}) {
  return api.post<SurveyImportResponse>("/admin/survey/import", payload);
}
