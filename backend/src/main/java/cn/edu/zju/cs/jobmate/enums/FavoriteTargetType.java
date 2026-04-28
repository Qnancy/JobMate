package cn.edu.zju.cs.jobmate.enums;

/**
 * Type of the entity that a {@link cn.edu.zju.cs.jobmate.models.Favorite} points to.
 */
public enum FavoriteTargetType {

    /** Job posting ({@link cn.edu.zju.cs.jobmate.models.JobInfo}). */
    JOB,

    /** Campus talk / career-fair event ({@link cn.edu.zju.cs.jobmate.models.ActivityInfo}). */
    ACTIVITY
}
