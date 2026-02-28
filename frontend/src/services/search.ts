import { api } from "@/utils/request";
import type { JobType } from "./job";
import type { CompanyType } from "./company";

export interface BaseSearchParams {
  page: number;
  page_size: number;
  keyword?: string;
  [key: string]: string | number | boolean | null | undefined;
}

export function searchJobs<T = any>(params: BaseSearchParams & { recruit_type?: JobType }) {
  return api.get<T>("/jobs/search", params);
}

export function searchActivities<T = any>(params: BaseSearchParams) {
  return api.get<T>("/activities/search", params);
}

export function searchCompanies<T = any>(params: BaseSearchParams & { type?: CompanyType }) {
  return api.get<T>("/companies/search", params);
}
