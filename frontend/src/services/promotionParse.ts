import { api } from "@/utils/request";
import type { ActivityType } from "./activity";
import type { EducationRequirement, JobType } from "./job";

export type PromotionParseTarget = "JOB" | "ACTIVITY";

export interface ParsedJobPromotion {
  company_id: number;
  company_name: string | null;
  position: string;
  recruit_type: JobType;
  link: string | null;
  location: string | null;
  extra: string | null;
  /** 后端解析出的投递截止时间（yyyy-MM-dd HH:mm:ss）；未识别时为 null */
  deadline: string | null;
  /** 学历要求枚举名；未识别时可能为 null */
  education_requirement?: EducationRequirement | null;
}

export interface ParsedActivityPromotion {
  company_id: number;
  company_name: string | null;
  title: string;
  time: string;
  /** 后端返回字符串名称（LECTURE/JOB_FAIR/COMPANY_VISIT）；缺失时默认 LECTURE。 */
  type: ActivityType;
  link: string | null;
  location: string | null;
  extra: string | null;
}

export interface PromotionParseResponse {
  warnings: string[];
  /** 当文本里识别出公司名但数据库里找不到时，后端会把原始名字塞这里，给前端做「一并创建公司」的快捷入口。 */
  suggested_company_name?: string | null;
  job?: ParsedJobPromotion;
  activity?: ParsedActivityPromotion;
}

export function parsePromotion(target: PromotionParseTarget, text: string) {
  return api.post<PromotionParseResponse>("/admin/promotion/parse", { target, text });
}
