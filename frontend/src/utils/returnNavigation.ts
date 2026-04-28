import type {
  RouteLocationNormalizedLoaded,
  RouteLocationRaw,
  Router,
} from 'vue-router';

/**
 * 仅允许站内相对路径，避免 open redirect。
 * fullPath 可含 query/hash，如 `/info/search?keywords=a`。
 */
export function isSafeInternalReturnPath(fullPath: string): boolean {
  if (!fullPath || typeof fullPath !== 'string') return false;
  const pathOnly = fullPath.split('?')[0].split('#')[0];
  if (!pathOnly.startsWith('/') || pathOnly.startsWith('//')) return false;
  if (fullPath.includes('://')) return false;
  return true;
}

/** 进入详情页时附带「返回目标」，供详情页 {@link navigateBackPreferFrom} 使用。 */
export function withReturnFrom(
  toPath: string,
  fromRoute: RouteLocationNormalizedLoaded,
): { path: string; query: Record<string, string> } {
  const full = fromRoute.fullPath || fromRoute.path;
  if (!isSafeInternalReturnPath(full)) {
    return { path: toPath, query: {} };
  }
  return {
    path: toPath,
    query: { from: encodeURIComponent(full) },
  };
}

/** 与 {@link withReturnFrom} 相同，但合并已有 query（如搜索条件）。 */
export function withReturnFromQuery(
  to: { path: string; query?: Record<string, string> },
  fromRoute: RouteLocationNormalizedLoaded,
): { path: string; query: Record<string, string> } {
  const full = fromRoute.fullPath || fromRoute.path;
  const q: Record<string, string> = { ...(to.query || {}) };
  if (isSafeInternalReturnPath(full)) {
    q.from = encodeURIComponent(full);
  }
  return { path: to.path, query: q };
}

/**
 * 详情页返回：优先回到进入前记录的 `from`（replace，不污染前进栈），
 * 否则浏览器后退，再无历史则用 fallback。
 */
export function navigateBackPreferFrom(
  router: Router,
  route: RouteLocationNormalizedLoaded,
  fallback: RouteLocationRaw,
): void {
  const rawQ = route.query.from;
  const raw = typeof rawQ === 'string' ? rawQ : Array.isArray(rawQ) ? rawQ[0] : '';
  if (raw) {
    try {
      const decoded = decodeURIComponent(raw);
      if (isSafeInternalReturnPath(decoded)) {
        router.replace(decoded);
        return;
      }
    } catch {
      /* ignore */
    }
  }
  if (typeof window !== 'undefined' && window.history.length > 1) {
    router.back();
    return;
  }
  router.replace(fallback);
}
