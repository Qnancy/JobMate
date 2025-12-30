package cn.edu.zju.cs.jobmate.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import cn.edu.zju.cs.jobmate.models.User;

/**
 * JWT authentication token.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final User principal; // The authenticated user.
    private String credentials; // The JWT token.

    public JwtAuthenticationToken(
        User principal,
        String credentials
    ) {
        super(principal.getAuthorities());
        this.principal = principal;
        this.credentials = credentials;
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
}
