import { api } from "@/utils/request";

export interface User {
  id: number;
  name: string;
  role: string;
  // add other fields as needed
}

export async function getUsers() {
  return api.get<User[]>('/users');
}

export async function getUser(id: number) {
  return api.get<User>(`/users/${id}`);
}

export async function updateUser(id: number, data: Partial<User>) {
  return api.put<User>(`/users/${id}`, data);
}

export async function deleteUser(id: number) {
  return api.del(`/users/${id}`);
}
