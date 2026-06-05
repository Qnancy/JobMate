import { showToast } from 'vant';

// 基础路径，配合 vite.config.ts 中的 proxy
const BASE_URL = '/api';
export const TOKEN_KEY = 'jobmate_token';

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

export function isSuccessCode(code: number) {
  return code === 0 || code === 200;
}

export function isSuccessResponse<T = any>(res: ApiResponse<T>) {
  return isSuccessCode(res.code);
}

/**
 * 扩展的请求配置项。在 fetch 原生 RequestInit 之上，允许调用方显式声明
 * 「这是一次后台静默请求」，失败时不弹全局 toast，由调用方自行处理。
 */
export interface RequestOptions extends RequestInit {
  /** true 表示不弹全局 toast。默认 false，保持原来交互行为 */
  silent?: boolean;
}

/**
 * 通用请求封装
 * @param url 请求地址 (例如 '/users/login')
 * @param options fetch 配置项
 */
export async function request<T = any>(url: string, options: RequestOptions = {}): Promise<ApiResponse<T>> {
  const { silent, ...fetchOptions } = options;
  // 1. 自动携带 Token
  const headers = new Headers(fetchOptions.headers);
  const token = localStorage.getItem(TOKEN_KEY);
  if (token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`);
  }
  
  if (!headers.has('Content-Type') && !(fetchOptions.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  // 开发环境下打印请求日志
  if (import.meta.env.DEV) {
    console.group(`[Request] ${fetchOptions.method || 'GET'} ${url}`);
    if (fetchOptions.body) {
      try {
        console.log('Body:', typeof fetchOptions.body === 'string' ? JSON.parse(fetchOptions.body) : fetchOptions.body);
      } catch (e) {
        console.log('Body:', fetchOptions.body);
      }
    }
    console.groupEnd();
  }

  try {
    // 发送请求
    const response = await fetch(`${BASE_URL}${url}`, {
      ...fetchOptions,
      headers,
    });

    const textBody = await response.text();
    let resData: ApiResponse<T> | null = null;
    if (textBody) {
      try {
        resData = JSON.parse(textBody) as ApiResponse<T>;
      } catch {
        // 非 JSON（如反向代理错误页）
      }
    }

    if (!response.ok) {
      const errorMsg =
        (resData && typeof resData.message === 'string' && resData.message
          ? resData.message
          : null) || `请求失败: ${response.status} ${response.statusText}`;
      throw new Error(errorMsg);
    }

    if (!resData) {
      throw new Error('响应为空或不是 JSON');
    }

    // 处理业务错误码
    if (!isSuccessCode(resData.code)) {
      // 可以根据 code 做特殊处理，比如 401 token 过期跳转登录
      // showToast({ type: 'fail', message: resData.message || '业务处理失败' });
    }

    return resData;
  } catch (error: any) {
    console.error('API Request Error:', error);
    const msg =
      typeof error?.message === 'string' && error.message.trim()
        ? error.message.trim()
        : '网络请求异常';
    // 再保险一次：message 必须是非空字符串，否则 Vant 会渲染出一个无内容的白色方块
    if (!silent) {
      showToast({ type: 'fail', message: msg && msg.trim() ? msg : '网络请求异常' });
    }
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

  postForm: <T>(url: string, formData: FormData) =>
    request<T>(url, {
      method: "POST",
      body: formData,
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
