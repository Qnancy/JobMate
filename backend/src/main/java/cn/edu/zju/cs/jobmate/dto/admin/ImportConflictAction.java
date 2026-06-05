package cn.edu.zju.cs.jobmate.dto.admin;

/**
 * How admin resolves a conflict with existing website data.
 */
public final class ImportConflictAction {

    /** Overwrite with questionnaire / edited content. */
    public static final String UPDATE = "UPDATE";

    /** Keep existing record unchanged (no-op for this row). */
    public static final String KEEP_EXISTING = "KEEP_EXISTING";

    /** Skip importing this row entirely. */
    public static final String SKIP = "SKIP";

    private ImportConflictAction() {
    }

    public static boolean isResolved(String action) {
        return UPDATE.equals(action) || KEEP_EXISTING.equals(action) || SKIP.equals(action);
    }
}
