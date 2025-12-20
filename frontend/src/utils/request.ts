import { showToast } from 'vant';

// 基础路径，配合 vite.config.ts 中的 proxy
const BASE_URL = '/api';

interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

/**
 * 通用请求封装
 * @param url 请求地址 (例如 '/users/login')
 * @param options fetch 配置项
 */
export async function request<T = any>(url: string, options: RequestInit = {}): Promise<ApiResponse<T>> {
  // 1. 自动携带 Token
  const headers = new Headers(options.headers);
  const userJson = localStorage.getItem('jobmate_current_user');
  if (userJson) {
    try {
      // 假设后端需要 Authorization 头，格式视后端要求而定
      // const user = JSON.parse(userJson);
      // if (user.token) headers.set('Authorization', `Bearer ${user.token}`);
      JSON.parse(userJson);
    } catch (e) { /* ignore */ }
  }
  
  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  // 开发环境下打印请求日志
  if (import.meta.env.DEV) {
    console.group(`[Request] ${options.method || 'GET'} ${url}`);
    if (options.body) {
      try {
        console.log('Body:', typeof options.body === 'string' ? JSON.parse(options.body) : options.body);
      } catch (e) {
        console.log('Body:', options.body);
      }
    }
    console.groupEnd();
  }

  try {
    // 发送请求
    const response = await fetch(`${BASE_URL}${url}`, {
      ...options,
      headers,
    });

    // 处理 HTTP 错误状态
    if (!response.ok) {
      const errorMsg = `请求失败: ${response.status} ${response.statusText}`;
      showToast({ type: 'fail', message: errorMsg });
      throw new Error(errorMsg);
    }

    // 解析响应
    const resData: ApiResponse<T> = await response.json();

    // 处理业务错误码 (假设 code !== 0 为错误)
    if (resData.code !== 0) {
      // 可以根据 code 做特殊处理，比如 401 token 过期跳转登录
      // showToast({ type: 'fail', message: resData.message || '业务处理失败' });
    }

    return resData;
  } catch (error: any) {
    console.error('API Request Error:', error);
    showToast({ type: 'fail', message: error.message || '网络请求异常' });
    throw error;
  }
}

// api规范化封装
type QueryParams = Record<string, string | number | boolean | null | undefined>;

function buildQuery(params?: QueryParams) {
  if (!params) return "";

  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value === null || value === undefined) return;
    search.append(key, String(value));
  });

  const q = search.toString();
  return q ? `?${q}` : "";
}

const jsonHeaders = {
  "Content-Type": "application/json",
};

export const api = {
  get: <T>(url: string, params?: QueryParams) =>
    request<T>(url + buildQuery(params), { method: "GET" }),

  post: <T>(url: string, data: any) =>
    request<T>(url, {
      method: "POST",
      headers: jsonHeaders,
      body: JSON.stringify(data),
    }),

  put: <T>(url: string, data: any) =>
    request<T>(url, {
      method: "PUT",
      headers: jsonHeaders,
      body: JSON.stringify(data),
    }),

  del: <T>(url: string) =>
    request<T>(url, { method: "DELETE" }),
};
