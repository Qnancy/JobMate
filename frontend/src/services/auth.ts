import { api, request, type ApiResponse, isSuccessResponse, TOKEN_KEY } from "@/utils/request";
import { favoriteStore } from "@/utils/favoriteStore";

export type User = {
  id: number
  username: string
  role: 'ADMIN' | 'USER'
  created_at?: string
}

type LoginData = {
  token: string
  token_type: 'Bearer'
}

export async function register(username: string, password: string, role: User['role'], admin_secret: string | null = null) {
  return api.post<User>('/users/register', {
    username,
    password,
    role,
    admin_secret,
  });
}

export async function login(
  username: string,
  password: string,
  admin_secret: string | null = null,
): Promise<ApiResponse<User | null>> {
  let loginRes: ApiResponse<LoginData>;
  try {
    loginRes = await api.post<LoginData>('/auth/login', {
      username,
      password,
      admin_secret,
    });
  } catch {
    // request 内已对 HTTP 错误 toast；此处吞掉异常，避免页面未捕获 Promise + 重复提示
    return { code: 401, message: '', data: null };
  }

  if (!isSuccessResponse(loginRes) || !loginRes.data?.token) {
    return {
      code: loginRes.code,
      message: loginRes.message,
      data: null,
    };
  }

  localStorage.setItem(TOKEN_KEY, loginRes.data.token);
  try {
    const meRes = await getMe();
    if (isSuccessResponse(meRes) && meRes.data) {
      setCurrentUser(meRes.data);
      // 登录后把收藏夹 fresh 一下；失败不阻塞登录流程
      favoriteStore.loadFromServer().catch(() => undefined);
      return meRes;
    }
    localStorage.removeItem(TOKEN_KEY);
    return {
      code: meRes.code,
      message: meRes.message || "获取用户信息失败，请重试登录",
      data: null,
    };
  } catch {
    localStorage.removeItem(TOKEN_KEY);
    return {
      code: 500,
      message: "",
      data: null,
    };
  }
}

export function getMe() {
  return api.get<User>('/users/me');
}






// export type User = {
//   username: string
//   password: string
//   displayName?: string
//   avatarUrl?: string
// }

// // Currently Written by AI. Need Refactor when backend is ready.

const CURRENT_KEY = 'jobmate_current_user'

function setCurrentUser(user: User) {
  try {
    localStorage.setItem(CURRENT_KEY, JSON.stringify(user))
  } catch (e) {
    // ignore storage errors
  }
}

// function readUsers(): User[] {
//   try {
//     const raw = localStorage.getItem(USERS_KEY)
//     return raw ? JSON.parse(raw) as User[] : []
//   } catch (e) {
//     return []
//   }
// }

// function writeUsers(users: User[]) {
//   localStorage.setItem(USERS_KEY, JSON.stringify(users))
// }

// export function register(user: User): { success: boolean; message?: string } {
//   const users = readUsers()
//   if (!user.username || !user.password) return { success: false, message: '用户名和密码不能为空' }
//   if (users.find(u => u.username === user.username)) {
//     return { success: false, message: '用户名已存在' }
//   }
//   users.push(user)
//   writeUsers(users)
//   return { success: true }
// }

// export function login(username: string, password: string): { success: boolean; user?: User; message?: string } {
//   const users = readUsers()
//   const found = users.find(u => u.username === username && u.password === password)
//   if (!found) return { success: false, message: '用户名或密码错误' }
//   // store a copy without password for safety when reading current user
//   const safe = { ...found }
//   localStorage.setItem(CURRENT_KEY, JSON.stringify(safe))
//   return { success: true, user: safe }
// }

export function logout() {
  request<null>('/auth/logout', { method: 'POST' }).catch(() => undefined)
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(CURRENT_KEY)
  // 清掉收藏状态，避免不同账户切换时串味
  favoriteStore.clearAll()
}

export function currentUser(): User | null {
  const raw = localStorage.getItem(CURRENT_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as User
  } catch (e) {
    return null
  }
}
