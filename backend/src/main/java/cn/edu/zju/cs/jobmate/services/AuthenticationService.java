package cn.edu.zju.cs.jobmate.services;

/**
 * Authentication service interface.
 */
public interface AuthenticationService {

    /**
     * Log out the authenticated user.
     * 
     * @param authorization the authorization header value
     */
    void logout(String authorization);
}
