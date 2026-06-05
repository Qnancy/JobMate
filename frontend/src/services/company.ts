import { api } from "@/utils/request";

export interface Company {
    id: number;
    name: string;
    type: CompanyType;
    /** 企业简介，可选；与接口 snake_case 反序列化后可能为 description */
    description?: string | null;
}

export type CompanyType = "STATE" | "PRIVATE" | "FOREIGN";

export const COMPANY_TYPE_LABELS: Record<CompanyType, string> = {
  STATE: "国企 / 事业单位",
  PRIVATE: "民营企业",
  FOREIGN: "外资 / 合资",
};

export function labelCompanyType(t?: string | null): string {
  if (!t) return "类型未知";
  const key = t as CompanyType;
  return COMPANY_TYPE_LABELS[key] ?? t;
}

export interface CompanyPayload {
    name: string;
    type: CompanyType;
    /** 企业简介，可选 */
    description?: string | null;
}

// 列表分页
export interface CompanySearchParams {
  page: number;
  page_size: number;
    keyword?: string;
    type?: CompanyType;
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

export async function createCompany(data: CompanyPayload) {
    return api.post<Company>("/companies", data);
}

export async function updateCompany(id: number, data: Partial<CompanyPayload>) {
    return api.put<Company>(`/companies/${id}`, data);
}

export async function deleteCompany(id: number) {
    return api.del(`/companies/${id}`);
}

export async function getCompanyById(id: number) {
    return api.get<Company>(`/companies/${id}`);
}

export function getCompany(params: CompanySearchParams) {
    return api.get<PageData<Company>>("/companies", params);
}

export function searchCompany(params: CompanySearchParams) {
    return api.get<PageData<Company>>("/companies/search", params);
}