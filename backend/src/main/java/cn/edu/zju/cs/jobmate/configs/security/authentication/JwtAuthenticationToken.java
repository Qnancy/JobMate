package cn.edu.zju.cs.jobmate.configs.security.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import cn.edu.zju.cs.jobmate.models.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JWT authentication token.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final User principal; // The authenticated user.
    private String credentials; // The JWT token.
    private String ip;
    private String device;

    public JwtAuthenticationToken(
        User principal,
        String credentials,
        HttpServletRequest request
    ) {
        super(principal.getAuthorities());
        this.principal = principal;
        this.credentials = credentials;
        this.ip = request.getRemoteAddr();
        this.device = request.getHeader(HttpHeaders.USER_AGENT);
        setAuthenticated(true);
    }

    @Override
    public User getPrincipal() {
        return principal;
    }

    @Override
    public String getCredentials() {
        return credentials;
    }

    public String getIp() {
        return ip;
    }

    public String getDevice() {
        return device;
    }
}
