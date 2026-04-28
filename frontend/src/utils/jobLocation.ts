/**
 * 岗位工作地点：仍用后端单个 `location` 字符串，多个 base 地用常见分隔符书写，
 * 前端解析后展示（列表摘要、详情多标签）。
 *
 * 推荐录入格式：顿号 `、`，例如：北京、上海、杭州
 * 同时兼容：，, ; / | ／ 等分隔符。
 */

const LOCATION_SPLIT_RE = /[、，,;/|／]+/;

function dedupePreserveOrder(parts: string[]): string[] {
  const seen = new Set<string>();
  const out: string[] = [];
  for (const p of parts) {
    if (seen.has(p)) continue;
    seen.add(p);
    out.push(p);
  }
  return out;
}

/** 将一条 location 文案拆成多个城市/办公点（无分隔符则整段作为一项）。 */
export function parseJobLocations(raw: string | null | undefined): string[] {
  if (raw == null) return [];
  const s = String(raw).trim();
  if (!s) return [];
  const parts = s.split(LOCATION_SPLIT_RE).map((p) => p.trim()).filter(Boolean);
  if (parts.length <= 1) return [s];
  return dedupePreserveOrder(parts);
}

/**
 * 列表/收藏等窄卡片：最多展示若干个城市标签，其余用「等 n 城」概括。
 */
export function summarizeJobLocationsForList(
  raw: string | null | undefined,
  maxTags = 2,
): { tags: string[]; moreCount: number } {
  const cities = parseJobLocations(raw);
  if (cities.length === 0) return { tags: [], moreCount: 0 };
  if (cities.length <= maxTags) return { tags: cities, moreCount: 0 };
  return { tags: cities.slice(0, maxTags), moreCount: cities.length - maxTags };
}
