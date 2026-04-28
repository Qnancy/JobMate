import { api } from "@/utils/request";
import type { Company } from "./company";

/** 与后端 {@code EducationRequirement} 枚举一致 */
export type EducationRequirement =
  | "UNSPECIFIED"
  | "BACHELOR_AND_ABOVE"
  | "MASTER_AND_ABOVE"
  | "PHD_AND_ABOVE";

export const EDUCATION_REQUIREMENT_LABELS: Record<EducationRequirement, string> = {
  UNSPECIFIED: "不限",
  BACHELOR_AND_ABOVE: "本科及以上",
  MASTER_AND_ABOVE: "硕士及以上",
  PHD_AND_ABOVE: "博士及以上",
};

export function labelEducationRequirement(v?: string | null): string {
  if (v == null || v === "") return EDUCATION_REQUIREMENT_LABELS.UNSPECIFIED;
  const key = coerceEducationRaw(v) ?? "UNSPECIFIED";
  return EDUCATION_REQUIREMENT_LABELS[key] ?? EDUCATION_REQUIREMENT_LABELS.UNSPECIFIED;
}

const EDU_KEYS = new Set<string>([
  "UNSPECIFIED",
  "BACHELOR_AND_ABOVE",
  "MASTER_AND_ABOVE",
  "PHD_AND_ABOVE",
]);

/** 中文展示值 → 枚举名（兼容后端若按中文序列化） */
const EDU_ZH_TO_KEY: Record<string, EducationRequirement> = {
  不限: "UNSPECIFIED",
  本科及以上: "BACHELOR_AND_ABOVE",
  硕士及以上: "MASTER_AND_ABOVE",
  博士及以上: "PHD_AND_ABOVE",
};

function coerceEducationRaw(v: unknown): EducationRequirement | undefined {
  if (v == null) return undefined;
  if (typeof v === "object" && v !== null && "name" in v) {
    return coerceEducationRaw((v as { name: unknown }).name);
  }
  if (typeof v !== "string") return undefined;
  let s = v.trim();
  if (s.startsWith("EducationRequirement.")) {
    s = s.slice("EducationRequirement.".length);
  }
  if (EDU_KEYS.has(s)) return s as EducationRequirement;
  const fromZh = EDU_ZH_TO_KEY[s];
  if (fromZh) return fromZh;
  return undefined;
}

/**
 * 从接口对象读取学历枚举（兼容 snake_case / camelCase、Jackson 按 toString 写的带前缀串、嵌套 name）。
 */
export function parseEducationRequirement(
  raw: Partial<Job> & Record<string, unknown>
): EducationRequirement {
  const v = raw.education_requirement ?? raw.educationRequirement;
  const coerced = coerceEducationRaw(v);
  return coerced ?? "UNSPECIFIED";
}

/** 资讯页学历筛选档位（用 ASCII 枚举，避免与中文标签字符串比较失败导致筛选项形同失效） */
export type JobEducationFilterTier = "ALL" | "BACHELOR" | "MASTER" | "PHD";

/**
 * 资讯页学历：按「岗位对学历门槛」分层筛选（含不限/本科可投岗）。
 * 避免库里多为 UNSPECIFIED 时选本科档列表为空。
 */
export function jobMatchesEducationTier(
  key: EducationRequirement,
  tier: JobEducationFilterTier
): boolean {
  switch (tier) {
    case "ALL":
      return true;
    case "BACHELOR":
      return key === "UNSPECIFIED" || key === "BACHELOR_AND_ABOVE";
    case "MASTER":
      return (
        key === "UNSPECIFIED" ||
        key === "BACHELOR_AND_ABOVE" ||
        key === "MASTER_AND_ABOVE"
      );
    case "PHD":
      return key === "PHD_AND_ABOVE";
  }
}

/** 招聘类型：兼容 snake_case / camelCase */
export function parseRecruitType(
  raw: Partial<Job> & Record<string, unknown>
): string | undefined {
  const v = raw.recruit_type ?? raw.recruitType;
  return typeof v === "string" ? v : undefined;
}

export interface Job {
  id: number;
  company: Company; // 企业信息
  recruit_type: JobType; // 招聘类型
  position: string; // 岗位名称
  link: string | null; // 投递链接
  location?: string | null; // 工作地点
  extra?: string | null;
  /** 学历要求 */
  education_requirement?: EducationRequirement | null;
  /** 发布时间，由后端 JPA Auditing 自动写入 */
  created_at: string;
  /** 最后更新时间，由后端 JPA Auditing 自动维护 */
  updated_at: string;
  /** 投递截止时间，可选 */
  deadline: string | null;
}

export type JobType = "INTERN" | "CAMPUS" | "EXPERIENCED";

// 创建/更新时的请求体
export interface JobPayload {
  company_id: number;
  recruit_type: JobType;
  position: string;
  link: string | null;
  location: string | null;
  extra: string | null;
  /** 学历要求；创建时可选，缺省为 UNSPECIFIED */
  education_requirement?: EducationRequirement | null;
  /** 可选；不传时后端不修改截止时间 */
  deadline?: string | null;
  /** 仅 update 用：传 true 显式清除已有截止时间 */
  clear_deadline?: boolean;
}

export interface JobSearchParams {
    page: number;
    page_size: number;
    keyword?: string;
    recruit_type?: JobType;
    /** 与 GET /jobs/search 一致，仅看某公司发布的职位 */
    company_id?: number;
    [key: string]: string | number | boolean | null | undefined;
}

export interface PageData<T> {
  content: T[];
  total: number;
  count: number;
  page: number;
  page_size: number;
  total_pages: number;
}

export async function createJob(data: JobPayload) {
  return api.post<Job>('/jobs', data);
}

export async function updateJob(id: number, data: Partial<JobPayload>) {
  return api.put<Job>(`/jobs/${id}`, data);
}

export async function deleteJob(id: number) {
  return api.del(`/jobs/${id}`);
}

export async function getJobById(id: number) {
    return api.get<Job>(`/jobs/${id}`);
}

export function getJob(params: JobSearchParams) {
  return api.get<PageData<Job>>("/jobs", params);
}

export function searchJob(params: JobSearchParams) {
  return api.get<PageData<Job>>("/jobs/search", params);
}