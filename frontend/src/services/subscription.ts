import { api } from "@/utils/request";

export type SubscribableType = "job" | "activity";

export interface SubscriptionPayload {
  type: SubscribableType;
  id: number;
}

export function subscribe(payload: SubscriptionPayload) {
  // 后端接口占位：发送订阅指令
  return api.post<unknown>("/subscribe", payload);
}

export function unsubscribe(payload: SubscriptionPayload) {
  // 后端接口占位：取消订阅指令
  return api.post<unknown>("/unsubscribe", payload);
}
