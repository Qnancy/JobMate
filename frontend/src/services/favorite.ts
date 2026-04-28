/**
 * 收藏夹（Favorites）前端服务
 *
 * 对接后端 `/api/favorites/*`，登录后所有操作默认归属当前用户。
 *
 * 后端合约（关键点）：
 * - JSON 统一 snake_case；
 * - ApiResponse 形式 `{ code, message, data }`，业务 OK 时 `code` 为 200；
 * - `target_type` 是枚举字符串：'JOB' | 'ACTIVITY'。
 */
import type { Job } from './job';
import type { Activity } from './activity';
import { api, request } from '../utils/request';

export type FavoriteTargetType = 'JOB' | 'ACTIVITY';

export interface FavoriteToggleResult {
  target_type: FavoriteTargetType;
  target_id: number;
  favorited: boolean;
}

export interface FavoriteItem {
  id: number;
  target_type: FavoriteTargetType;
  target_id: number;
  created_at: string;
  /** 仅当 target_type === 'JOB' 时存在 */
  job?: Job;
  /** 仅当 target_type === 'ACTIVITY' 时存在 */
  activity?: Activity;
}

export interface FavoriteListResponse {
  content: FavoriteItem[];
  total: number;
  count: number;
  page: number;
  page_size: number;
  total_pages: number;
}

export interface FavoriteCheckBatchResult {
  target_type: FavoriteTargetType;
  favorited_ids: number[];
}

/** 添加收藏（幂等） */
export function addFavorite(target_type: FavoriteTargetType, target_id: number) {
  return api.post<FavoriteToggleResult>('/favorites', { target_type, target_id });
}

/** 取消收藏（幂等）。注意后端读取的是 body，所以要走 request 而非 api.del */
export function removeFavorite(target_type: FavoriteTargetType, target_id: number) {
  return request<FavoriteToggleResult>('/favorites', {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ target_type, target_id }),
  });
}

/** 切换收藏状态，返回切换后的新状态 */
export function toggleFavorite(target_type: FavoriteTargetType, target_id: number) {
  return api.post<FavoriteToggleResult>('/favorites/toggle', { target_type, target_id });
}

/** 检查当前用户是否收藏了某个对象 */
export function checkFavorite(target_type: FavoriteTargetType, target_id: number) {
  return api.get<FavoriteToggleResult>('/favorites/check', { target_type, target_id });
}

/**
 * 批量检查：给一组 id，返回当前用户已经收藏的那一部分。
 * 适合列表页渲染时一次性拿到所有卡片的「是否收藏」状态。
 */
export function checkFavoritesBatch(target_type: FavoriteTargetType, ids: number[]) {
  return api.post<FavoriteCheckBatchResult>('/favorites/check-batch', { target_type, ids });
}

/**
 * 分页列出当前用户的收藏夹（按收藏时间倒序）。
 * 不传 target_type 则 JOB 和 ACTIVITY 一起返回。
 */
export function listFavorites(params: {
  target_type?: FavoriteTargetType;
  page?: number;
  page_size?: number;
} = {}) {
  return api.get<FavoriteListResponse>('/favorites', params);
}
