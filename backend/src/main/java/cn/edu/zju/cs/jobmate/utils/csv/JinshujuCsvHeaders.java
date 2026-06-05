package cn.edu.zju.cs.jobmate.utils.csv;

import java.util.HashMap;
import java.util.Map;

/**
 * Column names in Jinshuju survey export for employer information collection.
 */
public final class JinshujuCsvHeaders {

    public static final String ROW_NO = "序号";
    public static final String COMPANY_FULL_NAME = "企业全称（与营业执照一致）";
    public static final String COMPANY_SHORT_NAME = "企业常用简称";
    public static final String COMPANY_TYPE = "企业类型";
    public static final String COMPANY_WEBSITE = "公司官网";
    public static final String INFO_TYPE = "招聘信息类型";
    public static final String COMPANY_TAGLINE = "公司描述";
    public static final String COMPANY_DESCRIPTION = "公司简介";
    public static final String POSITION = "职位名称";
    public static final String RECRUIT_TYPE = "招聘类型";
    public static final String JOB_DESCRIPTION = "职位描述（JD）";
    public static final String EDUCATION = "学历要求";
    public static final String LOCATION = "工作地点";
    public static final String APPLY_LINK = "在线投递 / 网申链接";
    public static final String DEADLINE = "投递截止日期";

    public static final String INFO_TYPE_COMPANY = "公司整体宣传";
    public static final String INFO_TYPE_JOB = "定向岗位宣传";

    private JinshujuCsvHeaders() {
    }

    public static Map<String, String> rowToMap(String[] header, String[] values) {
        Map<String, String> map = new HashMap<>();
        int len = Math.min(header.length, values.length);
        for (int i = 0; i < len; i++) {
            map.put(header[i].trim(), values[i] != null ? values[i].trim() : "");
        }
        return map;
    }

    public static String get(Map<String, String> row, String key) {
        String v = row.get(key);
        return v == null ? "" : v.trim();
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
