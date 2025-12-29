package cn.edu.zju.cs.jobmate.configs.security.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;

/**
 * JWT authentication token.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UserDetails principal;
    private String credentials;
    private String ip;
    private String device;

    public JwtAuthenticationToken(
        UserDetails principal,
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
    public UserDetails getPrincipal() {
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
