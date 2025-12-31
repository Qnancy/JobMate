package cn.edu.zju.cs.jobmate.security.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * JWT authentication token.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal; // The authenticated user' username.
    private String credentials; // The JWT token.

    public JwtAuthenticationToken(
        String principal,
        String credentials,
        Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    @Override
    public String getCredentials() {
        return credentials;
    }
}
