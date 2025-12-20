import { api } from "@/utils/request";
import type { Company } from "./company";

export interface Job {
  id: number;
  company: Company; // 企业信息
  recruit_type: JobType; // 招聘类型
  position: string; // 岗位名称
  link: string; // 投递链接
  location?: string | null; // 工作地点
  extra?: string | null;
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
}

export interface JobSearchParams {
    page: number;
    page_size: number;
    [key: string]: string | number | boolean | null | undefined;
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
  return api.get<Job[]>("/jobs", params);
}