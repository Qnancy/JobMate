import { api } from "@/utils/request";

export interface Company {
    id: number;
    name: string;
    type: CompanyType;
}

export type CompanyType = "STATE" | "PRIVATE" | "FOREIGN";

// 用于创建或更新企业的信息
export interface CompanyPayload {
    name: string;
    type: CompanyType;
}

// 列表分页
export interface CompanySearchParams {
  page: number;
  page_size: number;
  [key: string]: string | number | boolean | null | undefined;
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
  return api.get<Company[]>("/companies", params);
}