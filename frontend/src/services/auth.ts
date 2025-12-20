import { api } from "@/utils/request";

export type User = {
  id: number
  name: string
  role: string
  create_at?: string
}

export async function register(name: string, password: string, role: string) {
  return api.post<User>('/users/register', {
    name,
    password,
    role,
  });
}

export async function login(name: string, password: string) {
  // 内置管理员账号 (Mock逻辑，实际项目中应由后端处理)
  if (name === 'admin' && password === 'admin') {
    const adminUser: User = { id: 0, name: 'admin', role: 'admin', create_at: new Date().toISOString() }
    setCurrentUser(adminUser)
    return { code: 0, message: 'ok', data: adminUser }
  }

  // 内置访客账号
  if (name === 'user' && password === 'user') {
    const guestUser: User = { id: -1, name: 'user', role: 'user', create_at: new Date().toISOString() }
    setCurrentUser(guestUser)
    return { code: 0, message: 'ok', data: guestUser }
  }

  // 真实 API 调用
  const res = await api.post<User>('/users/login', {
    name,
    password,
  });
  
  if (res.code === 0 && res.data) {
    setCurrentUser(res.data);
  }
  return res;
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
