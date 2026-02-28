import { api } from "@/utils/request";
import { currentUser } from "./auth";

export type SubscribableType = "job" | "activity";

export interface SubscriptionPayload {
  type: SubscribableType;
  id: number;
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
    return api.del(`/jobs/subscriptions/${payload.id}`);
  }
  return api.del(`/activities/subscriptions/${payload.id}`);
}
