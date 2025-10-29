export type User = {
  username: string
  password: string
  displayName?: string
  avatarUrl?: string
}

// Currently Written by AI. Need Refactor when backend is ready.

const USERS_KEY = 'jobmate_users'
const CURRENT_KEY = 'jobmate_current_user'

function readUsers(): User[] {
  try {
    const raw = localStorage.getItem(USERS_KEY)
    return raw ? JSON.parse(raw) as User[] : []
  } catch (e) {
    return []
  }
}

function writeUsers(users: User[]) {
  localStorage.setItem(USERS_KEY, JSON.stringify(users))
}

export function register(user: User): { success: boolean; message?: string } {
  const users = readUsers()
  if (!user.username || !user.password) return { success: false, message: '用户名和密码不能为空' }
  if (users.find(u => u.username === user.username)) {
    return { success: false, message: '用户名已存在' }
  }
  users.push(user)
  writeUsers(users)
  return { success: true }
}

export function login(username: string, password: string): { success: boolean; user?: User; message?: string } {
  const users = readUsers()
  const found = users.find(u => u.username === username && u.password === password)
  if (!found) return { success: false, message: '用户名或密码错误' }
  // store a copy without password for safety when reading current user
  const safe = { ...found }
  localStorage.setItem(CURRENT_KEY, JSON.stringify(safe))
  return { success: true, user: safe }
}

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
