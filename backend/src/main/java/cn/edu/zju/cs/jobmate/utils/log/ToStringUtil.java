package cn.edu.zju.cs.jobmate.utils.log;

/**
 * Utility used in {@link #toString()} for friendly logging.
 */
public final class ToStringUtil {

    private ToStringUtil() {
    }

    /**
     * Wrap the string with single quotes.
     * 
     * @param str the string field
     * @return the wrapped string
     */
    public static String wrap(String str) {
        return str != null ? "\'" + str + "\'" : null;
    }
}
