/**
 * 收藏状态 store（模块级 reactive singleton）
 *
 * 目标：
 * 1. 登录用户：以后端 `/api/favorites/*` 为唯一真实来源；本地 localStorage 只作为冷启动缓存，
 *    让首屏渲染不等待网络。
 * 2. 未登录用户：完全走 localStorage，保持原有的离线体验；登录后有一次 loadFromServer
 *    就会被覆盖。
 *
 * 所有页面应当通过 `favoriteStore` 访问收藏状态，而不是自己读写 localStorage。
 */
import { reactive, computed, type ComputedRef } from 'vue';

import {
  addFavorite as apiAdd,
  removeFavorite as apiRemove,
  type FavoriteTargetType,
  type FavoriteCheckBatchResult,
  type FavoriteListResponse,
} from '../services/favorite';
import { isSuccessResponse, request, TOKEN_KEY } from './request';

/**
 * 这里定义本地静默版的 list / check-batch：失败时不弹全局 toast，
 * 让后台同步能优雅降级，不打扰用户。
 */
function listFavoritesSilent(params: {
  target_type?: FavoriteTargetType;
  page?: number;
  page_size?: number;
}) {
  const search = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v === undefined || v === null) return;
    search.append(k, String(v));
  });
  const q = search.toString();
  return request<FavoriteListResponse>(`/favorites${q ? `?${q}` : ''}`, {
    method: 'GET',
    silent: true,
  });
}

function checkFavoritesBatchSilent(target_type: FavoriteTargetType, ids: number[]) {
  return request<FavoriteCheckBatchResult>('/favorites/check-batch', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ target_type, ids }),
    silent: true,
  });
}

// ---------- 存储键 ----------
const LS_JOBS_KEY = 'jobmate_favorite_jobs';
const LS_ACTIVITIES_KEY = 'jobmate_favorite_fairs'; // 历史命名，沿用

// ---------- 本地持久化 ----------
function readIds(key: string): number[] {
  try {
    const raw = localStorage.getItem(key);
    const parsed = raw ? JSON.parse(raw) : [];
    if (!Array.isArray(parsed)) return [];
    return parsed.map((x) => Number(x)).filter((x) => Number.isFinite(x) && x > 0);
  } catch {
    return [];
  }
}

function writeIds(key: string, ids: Iterable<number>) {
  try {
    localStorage.setItem(key, JSON.stringify([...new Set(ids)]));
  } catch {
    // localStorage 写失败就忽略（例如隐身模式）
  }
}

function lsKey(type: FavoriteTargetType) {
  return type === 'JOB' ? LS_JOBS_KEY : LS_ACTIVITIES_KEY;
}

function isLoggedIn(): boolean {
  return !!localStorage.getItem(TOKEN_KEY);
}

// ---------- 响应式状态 ----------
const state = reactive<{
  jobs: Set<number>;
  activities: Set<number>;
  loading: boolean;
  loadedFromServer: boolean;
}>({
  jobs: new Set<number>(readIds(LS_JOBS_KEY)),
  activities: new Set<number>(readIds(LS_ACTIVITIES_KEY)),
  loading: false,
  loadedFromServer: false,
});

function setForType(type: FavoriteTargetType): Set<number> {
  return type === 'JOB' ? state.jobs : state.activities;
}

function persist(type: FavoriteTargetType) {
  writeIds(lsKey(type), setForType(type));
}

// ---------- 公开 API ----------

/**
 * 从后端加载当前用户全部收藏（可用于登录后、进入收藏夹页时）。
 * 若未登录则是 no-op。
 *
 * 采用"分页拉全部"的策略。一般用户的收藏不会很多；超大体量以后再换延迟加载。
 */
async function loadFromServer(): Promise<void> {
  if (!isLoggedIn()) return;
  state.loading = true;
  try {
    const jobIds: number[] = [];
    const activityIds: number[] = [];

    let page = 1;
    const pageSize = 100;
    /** 防止后端 total_pages 异常时出现极长分页，拖垮浏览器 */
    const maxPages = 500;
    while (page <= maxPages) {
      let res;
      try {
        res = await listFavoritesSilent({ page, page_size: pageSize });
      } catch {
        // 网络/路由异常都不打扰用户，直接保留本地缓存
        return;
      }
      if (!isSuccessResponse(res) || !res.data) break;
      for (const item of res.data.content || []) {
        if (item.target_type === 'JOB') jobIds.push(item.target_id);
        else if (item.target_type === 'ACTIVITY') activityIds.push(item.target_id);
      }
      if (page >= (res.data.total_pages || 0) || (res.data.content || []).length === 0) break;
      page += 1;
    }

    state.jobs = new Set(jobIds);
    state.activities = new Set(activityIds);
    persist('JOB');
    persist('ACTIVITY');
    state.loadedFromServer = true;
  } finally {
    state.loading = false;
  }
}

/**
 * 针对当前列表页里可见的若干 id，一次性向后端查收藏状态（批量 check）。
 * 相比 loadFromServer 更轻量，适合列表页初始化时 fire-and-forget。
 */
async function syncForIds(type: FavoriteTargetType, ids: number[]): Promise<void> {
  if (!isLoggedIn() || ids.length === 0) return;
  try {
    const res = await checkFavoritesBatchSilent(type, ids);
    if (!isSuccessResponse(res) || !res.data) return;

    const favoritedIds = new Set(res.data.favorited_ids.map(Number));
    const set = setForType(type);
    // 以服务器为准，更新这批 id 的状态（未出现在服务器响应里的视为未收藏）
    for (const id of ids) {
      if (favoritedIds.has(id)) set.add(id);
      else set.delete(id);
    }
    persist(type);
  } catch {
    // 失败就保留本地已有状态，UX 上不阻塞
  }
}

/**
 * 切换收藏状态。乐观更新：立即翻转 UI，异步调后端；后端失败时回滚。
 *
 * @returns 切换后的最终状态（true=已收藏）
 */
async function toggle(type: FavoriteTargetType, id: number): Promise<boolean> {
  const numId = Number(id);
  if (!Number.isFinite(numId) || numId <= 0) return false;

  const set = setForType(type);
  const wasFavorited = set.has(numId);

  if (wasFavorited) set.delete(numId);
  else set.add(numId);
  persist(type);

  if (!isLoggedIn()) {
    return !wasFavorited;
  }

  try {
    const res = wasFavorited ? await apiRemove(type, numId) : await apiAdd(type, numId);
    if (!isSuccessResponse(res)) {
      if (wasFavorited) set.add(numId);
      else set.delete(numId);
      persist(type);
      return wasFavorited;
    }
    return !wasFavorited;
  } catch {
    if (wasFavorited) set.add(numId);
    else set.delete(numId);
    persist(type);
    return wasFavorited;
  }
}

function isFavorited(type: FavoriteTargetType, id: number | string): boolean {
  const numId = Number(id);
  if (!Number.isFinite(numId)) return false;
  return setForType(type).has(numId);
}

function idsOf(type: FavoriteTargetType): ComputedRef<number[]> {
  return computed(() => [...setForType(type)]);
}

/** 退出登录时调一下：清空内存和本地缓存，避免下一位用户看到上一位的收藏 */
function clearAll() {
  state.jobs.clear();
  state.activities.clear();
  state.loadedFromServer = false;
  try {
    localStorage.removeItem(LS_JOBS_KEY);
    localStorage.removeItem(LS_ACTIVITIES_KEY);
  } catch {
    // ignore
  }
}

export const favoriteStore = {
  state,
  isFavorited,
  toggle,
  loadFromServer,
  syncForIds,
  idsOf,
  clearAll,
};

export type { FavoriteTargetType } from '../services/favorite';
