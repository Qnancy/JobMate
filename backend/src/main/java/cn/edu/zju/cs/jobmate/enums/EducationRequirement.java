package cn.edu.zju.cs.jobmate.enums;

/**
 * 岗位学历要求（展示为「本科及以上」等）。
 *
 * @see cn.edu.zju.cs.jobmate.models.JobInfo
 */
public enum EducationRequirement {

    /** 未在 JD 中识别或未填写 */
    UNSPECIFIED("不限"),

    BACHELOR_AND_ABOVE("本科及以上"),

    MASTER_AND_ABOVE("硕士及以上"),

    PHD_AND_ABOVE("博士及以上");

    private final String labelZh;

    EducationRequirement(String labelZh) {
        this.labelZh = labelZh;
    }

    public String getLabelZh() {
        return labelZh;
    }

    /**
     * 与 {@link #name()} 一致，避免 Jackson 在 {@code WRITE_ENUMS_USING_TO_STRING} 等配置下
     * 把非标准前缀写进 JSON，导致前端只认 {@code BACHELOR_AND_ABOVE} 等字面量而全部显示「不限」。
     */
    @Override
    public String toString() {
        return name();
    }
}
