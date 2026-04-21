import { api } from "@/utils/request";

export interface User {
  id: number;
  username: string;
  role: 'ADMIN' | 'USER';
  created_at?: string;
}

export async function getUsers() {
  const res = await api.get<User>('/users/me');
  return {
    ...res,
    data: res.data ? [res.data] : [],
  };
}

export async function getCurrentUser() {
  return api.get<User>('/users/me');
}

export async function updateCurrentUser(data: { username: string | null; password: string | null }) {
  return api.put<User>(`/users/me`, data);
}

export async function deleteCurrentUser() {
  return api.del(`/users/me`);
}
