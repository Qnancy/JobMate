import { api, request, type ApiResponse, isSuccessResponse, TOKEN_KEY } from "@/utils/request";

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

export async function login(username: string, password: string): Promise<ApiResponse<User | null>> {
  const loginRes = await api.post<LoginData>('/auth/login', {
    username,
    password,
  });

  if (!isSuccessResponse(loginRes) || !loginRes.data?.token) {
    return {
      code: loginRes.code,
      message: loginRes.message,
      data: null,
    };
  }

  localStorage.setItem(TOKEN_KEY, loginRes.data.token);
  const meRes = await getMe();
  if (isSuccessResponse(meRes) && meRes.data) {
    setCurrentUser(meRes.data);
  }

  return meRes;
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
