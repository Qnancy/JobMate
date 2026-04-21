import { api } from "@/utils/request";
import { currentUser } from "./auth";

export type SubscribableType = "job" | "activity";

export interface SubscriptionPayload {
  type: SubscribableType;
  id: number;
}

type SubscriptionPage<T> = {
  content: T[];
  total: number;
  count: number;
  page: number;
  page_size: number;
  total_pages: number;
};

type JobSubscriptionItem = {
  id: number;
  job_info: { id: number };
};

type ActivitySubscriptionItem = {
  id: number;
  activity_info: { id: number };
};

async function findJobSubscriptionIdByJobId(jobInfoId: number): Promise<number | null> {
  const pageSize = 100;
  let page = 1;

  while (true) {
    const res = await api.get<SubscriptionPage<JobSubscriptionItem>>("/jobs/subscriptions", {
      page,
      page_size: pageSize,
    });
    const list = res.data?.content || [];
    const matched = list.find((item) => Number(item.job_info?.id) === Number(jobInfoId));
    if (matched) return Number(matched.id);

    const totalPages = Number(res.data?.total_pages || 0);
    if (page >= totalPages || list.length === 0) return null;
    page += 1;
  }
}

async function findActivitySubscriptionIdByActivityId(activityInfoId: number): Promise<number | null> {
  const pageSize = 100;
  let page = 1;

  while (true) {
    const res = await api.get<SubscriptionPage<ActivitySubscriptionItem>>("/activities/subscriptions", {
      page,
      page_size: pageSize,
    });
    const list = res.data?.content || [];
    const matched = list.find((item) => Number(item.activity_info?.id) === Number(activityInfoId));
    if (matched) return Number(matched.id);

    const totalPages = Number(res.data?.total_pages || 0);
    if (page >= totalPages || list.length === 0) return null;
    page += 1;
  }
}

export function subscribe(payload: SubscriptionPayload) {
  const user = currentUser();
  if (!user) {
    return Promise.reject(new Error('请先登录'));
  }

  if (payload.type === 'job') {
    return api.post<unknown>("/jobs/subscriptions", {
      user_id: user.id,
      job_info_id: payload.id,
    });
  }

  return api.post<unknown>("/activities/subscriptions", {
    user_id: user.id,
    activity_info_id: payload.id,
  });
}

export function unsubscribe(payload: SubscriptionPayload) {
  if (payload.type === 'job') {
    return findJobSubscriptionIdByJobId(payload.id)
      .then((subscriptionId) => {
        if (subscriptionId === null) return Promise.resolve({ code: 200, message: '未找到订阅', data: null });
        return api.del(`/jobs/subscriptions/${subscriptionId}`);
      })
      .catch(() => api.del(`/jobs/subscriptions/${payload.id}`));
  }

  return findActivitySubscriptionIdByActivityId(payload.id)
    .then((subscriptionId) => {
      if (subscriptionId === null) return Promise.resolve({ code: 200, message: '未找到订阅', data: null });
      return api.del(`/activities/subscriptions/${subscriptionId}`);
    })
    .catch(() => api.del(`/activities/subscriptions/${payload.id}`));
}
