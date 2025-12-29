package cn.edu.zju.cs.jobmate.utils.httpservlet;

import jakarta.servlet.http.HttpServletRequest;

/**
 * {@link HttpServletRequest} related utilities.
 */
public final class RequestUtil {

    private static final String TIMER = "REQUEST_TIMER";

    private RequestUtil() {
    }

    /**
     * Set the start time of the request.
     * 
     * @param request the HTTP servlet request
     */
    public static void setTimer(HttpServletRequest request) {
        request.setAttribute(TIMER, System.currentTimeMillis());
    }

    /**
     * Check the elapsed time of the request from now.
     * 
     * @param request the HTTP servlet request
     * @return the elapsed time in milliseconds
     */
    public static long checkTimer(HttpServletRequest request) {
        Object timer = request.getAttribute(TIMER);
        return timer == null ? 0L : System.currentTimeMillis() - (Long) timer;
    }

    /**
     * Get basic info of the request for logging.
     * 
     * @param request the HTTP servlet request
     * @return the formatted info string
     */
    public static String info(HttpServletRequest request) {
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        return String.format("%s %s from %s", method, uri, ip);
    }
}
