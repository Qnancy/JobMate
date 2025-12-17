import { api } from "@/utils/request";

export interface Activity {
  id: number;
  title: string;
  date: string;
  location: string;
  status: string;
  // add other fields
}

export async function getActivities() {
  return api.get<Activity[]>('/activities');
}

export async function createActivity(data: Omit<Activity, 'id'>) {
  return api.post<Activity>('/activities', data);
}

export async function updateActivity(id: number, data: Partial<Activity>) {
  return api.put<Activity>(`/activities/${id}`, data);
}

export async function deleteActivity(id: number) {
  return api.del(`/activities/${id}`);
}
