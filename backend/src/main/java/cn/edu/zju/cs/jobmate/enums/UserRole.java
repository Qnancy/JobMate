package cn.edu.zju.cs.jobmate.enums;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * User role enumeration for User entities.
 * 
 * @see cn.edu.zju.cs.jobmate.models.User
 */
public enum UserRole {

    ADMIN(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))),
    USER(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

    private final Collection<GrantedAuthority> authorities;

    UserRole(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "UserRole." + this.name();
    }
}
