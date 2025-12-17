import { api } from "@/utils/request";

export interface Job {
  id: number;
  title: string;
  company: string;
  location: string;
  type: string;
  salary: string;
  // add other fields
}

export async function getJobs() {
  return api.get<Job[]>('/jobs');
}

export async function createJob(data: Omit<Job, 'id'>) {
  return api.post<Job>('/jobs', data);
}

export async function updateJob(id: number, data: Partial<Job>) {
  return api.put<Job>(`/jobs/${id}`, data);
}

export async function deleteJob(id: number) {
  return api.del(`/jobs/${id}`);
}
