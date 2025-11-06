package cn.edu.zju.cs.jobmate.enums;

/**
 * Recruitment type enumeration for JobInfo entity.
 * 
 * @see cn.edu.zju.cs.jobmate.models.JobInfo
 */
public enum RecruitType {

    INTERN("intern"),
    CAMPUS("campus"),
    EXPERIENCED("experienced");

    private final String value;

    RecruitType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
