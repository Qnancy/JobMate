import { api } from "@/utils/request";

// 通用搜索请求参数
export interface SearchParams {
  page: number;
  page_size: number;
  keyword: string;
  [key: string]: string | number | boolean | null | undefined;
}

// 支持自定义响应类型，方便各模块按需声明返回结构
export async function search<T = any>(params: SearchParams) {
  return api.get<T>("/search", params);
}
