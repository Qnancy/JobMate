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
      const user = JSON.parse(userJson);
      // 假设后端需要 Authorization 头，格式视后端要求而定
      // if (user.token) headers.set('Authorization', `Bearer ${user.token}`);
    } catch (e) { /* ignore */ }
  }
  
  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  try {
    // 2. 发送请求
    const response = await fetch(`${BASE_URL}${url}`, {
      ...options,
      headers,
    });

    // 3. 处理 HTTP 错误状态
    if (!response.ok) {
      const errorMsg = `请求失败: ${response.status} ${response.statusText}`;
      showToast({ type: 'fail', message: errorMsg });
      throw new Error(errorMsg);
    }

    // 4. 解析响应
    const resData: ApiResponse<T> = await response.json();

    // 5. 处理业务错误码 (假设 code !== 0 为错误)
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

// 便捷方法
export const api = {
  get: <T>(url: string) => request<T>(url, { method: 'GET' }),
  post: <T>(url: string, data: any) => request<T>(url, { method: 'POST', body: JSON.stringify(data) }),
  put: <T>(url: string, data: any) => request<T>(url, { method: 'PUT', body: JSON.stringify(data) }),
  del: <T>(url: string) => request<T>(url, { method: 'DELETE' }),
};
