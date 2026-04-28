import {
  labelEducationRequirement,
  parseEducationRequirement,
  parseRecruitType,
  type EducationRequirement,
  type Job,
} from '@/services/job';
import { ACTIVITY_TYPE_LABELS, type Activity, type ActivityType } from '@/services/activity';

export type JobCard = {
  id: number;
  title: string;
  company: string;
  location: string;
  type: string;
  salary: string;
  /** 学历要求展示文案 */
  educationLabel: string;
  /** 与后端枚举一致，用于筛选 */
  educationKey: EducationRequirement;
  description: string;
  publishDate: string;
  deadlineText: string | null;
  category: string;
};

export type FairCard = {
  id: number;
  status: string;
  type: '宣讲会' | '双选会' | '名企探访';
  title: string;
  date: string;
  location: string;
  companies: number;
};

export const RECRUIT_TYPE_MAP: Record<string, string> = {
  INTERN: '实习',
  CAMPUS: '校招',
  EXPERIENCED: '校招',
};

export function normalizeTimeText(value?: string | null): string {
  if (!value) return '时间待定';
  return value;
}

export function getActivityStatus(time?: string | null): string {
  if (!time) return '状态未知';
  const date = new Date(time.replace(' ', 'T'));
  if (Number.isNaN(date.getTime())) return '状态未知';
  return date.getTime() > Date.now() ? '即将开始' : '已结束';
}

/**
 * "yyyy-MM-dd HH:mm:ss" → 相对时间（刚发布 / X 分钟前 / X 小时前 / X 天前 / yyyy-MM-dd）。
 */
export function formatRelativeTime(value?: string | null): string {
  if (!value) return '最新发布';
  const date = new Date(value.replace(' ', 'T'));
  if (Number.isNaN(date.getTime())) return '最新发布';
  const diffMs = Date.now() - date.getTime();
  if (diffMs < 0) return value.slice(0, 10);
  const sec = Math.floor(diffMs / 1000);
  if (sec < 60) return '刚刚发布';
  const min = Math.floor(sec / 60);
  if (min < 60) return `${min} 分钟前`;
  const hr = Math.floor(min / 60);
  if (hr < 24) return `${hr} 小时前`;
  const day = Math.floor(hr / 24);
  if (day < 30) return `${day} 天前`;
  return value.slice(0, 10);
}

/** 截止时间的友好展示：如 "截止 04-30 23:59"；已过期则显示 "已截止"。 */
export function formatDeadline(value?: string | null): string | null {
  if (!value) return null;
  const date = new Date(value.replace(' ', 'T'));
  if (Number.isNaN(date.getTime())) return null;
  if (date.getTime() < Date.now()) return '已截止';
  return `截止 ${value.slice(5, 16)}`;
}

export function mapJobToCard(job: Job): JobCard {
  const raw = job as Job & Record<string, unknown>;
  const edu = parseEducationRequirement(raw);
  const recruit = parseRecruitType(raw);
  return {
    id: job.id,
    title: job.position,
    company: job.company?.name || '未知企业',
    location: job.location || '地点待定',
    type: (recruit && RECRUIT_TYPE_MAP[recruit]) || recruit || job.recruit_type || '未知',
    salary: '面议',
    educationLabel: labelEducationRequirement(edu),
    educationKey: edu,
    description: job.extra || '暂无更多描述',
    publishDate: formatRelativeTime(job.created_at),
    deadlineText: formatDeadline(job.deadline),
    category: (recruit && RECRUIT_TYPE_MAP[recruit]) || '全部',
  };
}

/** 优先用后端 type，老数据缺字段时再退回标题/简介启发式。 */
export function resolveFairType(item: Activity): FairCard['type'] {
  const backendType = item.type as ActivityType | undefined;
  if (backendType && ACTIVITY_TYPE_LABELS[backendType]) {
    return ACTIVITY_TYPE_LABELS[backendType] as FairCard['type'];
  }
  const text = `${item.title || ''} ${item.extra || ''}`.toLowerCase();
  if (/(双选|双选会|招聘会|招聘双选|联合招聘)/.test(text)) return '双选会';
  if (/(探访|参观|开放日|open\s*day|名企行|企业行|实地)/.test(text)) return '名企探访';
  return '宣讲会';
}

export function mapActivityToCard(item: Activity): FairCard {
  return {
    id: item.id,
    status: getActivityStatus(item.time),
    type: resolveFairType(item),
    title: item.title,
    date: normalizeTimeText(item.time),
    location: item.location || '地点待定',
    companies: item.company?.name ? 1 : 0,
  };
}
