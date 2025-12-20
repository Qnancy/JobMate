import { api } from "@/utils/request";
import type { Company } from "./company";

// 响应体：携带完整公司信息
export interface Activity {
  id: number;
  company: Company;
  title: string;
  time: string;
  link: string | null;
  location: string | null;
  extra: string | null;
  status?: string; // 若后端返回状态，保持可选以兼容旧页面
}

// 创建/更新时的请求体：传 company_id 等基础字段
export interface ActivityPayload {
  company_id: number;
  title: string;
  time: string;
  link: string | null;
  location: string | null;
  extra: string | null;
  status?: string;
}

// 列表分页
export interface ActivitySearchParams {
  page?: number;
  page_size?: number;
  keyword?: string;
  [key: string]: string | number | boolean | null | undefined;
}

export function getActivities(params?: ActivitySearchParams) {
  return api.get<Activity[]>("/activities", params);
}

export async function getActivityById(id: number) {
  return api.get<Activity>(`/activities/${id}`);
}

export async function createActivity(data: ActivityPayload) {
  return api.post<Activity>("/activities", data);
}

export async function updateActivity(id: number, data: Partial<ActivityPayload>) {
  return api.put<Activity>(`/activities/${id}`, data);
}

export async function deleteActivity(id: number) {
  return api.del(`/activities/${id}`);
}
