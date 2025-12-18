import { api } from "@/utils/request";

export interface Job {
  id: number;
  company_id: number; 
  recruit_type: string; //招聘类型
  position: string; //岗位名称
  link: string; //投递链接
  city: string; //工作城市
  location?: string | null; //工作地点
  extra?: string | null; //备注信息
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
