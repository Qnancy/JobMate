package cn.edu.zju.cs.jobmate.enums;

/**
 * Activity type enumeration for ActivityInfo entity.
 *
 * <ul>
 *   <li>{@link #LECTURE} 宣讲会 — single-company campus talk / info session.</li>
 *   <li>{@link #JOB_FAIR} 双选会 — multi-employer recruitment fair.</li>
 *   <li>{@link #COMPANY_VISIT} 名企探访 — student visit / open-day at a company site.</li>
 * </ul>
 *
 * @see cn.edu.zju.cs.jobmate.models.ActivityInfo
 */
public enum ActivityType {

    LECTURE("lecture"),
    JOB_FAIR("job_fair"),
    COMPANY_VISIT("company_visit");

    private final String value;

    ActivityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ActivityType." + this.name();
    }
}
