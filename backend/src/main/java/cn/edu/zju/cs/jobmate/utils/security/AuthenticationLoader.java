package cn.edu.zju.cs.jobmate.utils.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import cn.edu.zju.cs.jobmate.configs.security.authentication.JwtAuthenticationToken;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.User;

/**
 * Authentication utility for retrieving the current authenticated user.
 */
public class AuthenticationLoader {

    /**
     * Get the currently authenticated user from the security context.
     * 
     * @return the current User, or null if not authenticated.
     * @throws BusinessException if the user is not authenticated.
     */
    public static User getCurrentUser() throws BusinessException {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        if (auth == null) {
            throw new BusinessException(ErrorCode.UNAUTHENTICATED);
        }
        return switch (auth) {
            case JwtAuthenticationToken jwtAuth -> {
                if (!auth.isAuthenticated()) {
                    throw new BusinessException(ErrorCode.UNAUTHENTICATED);
                }
                yield (User) jwtAuth.getPrincipal();
            }
            default -> {
                if (!auth.isAuthenticated() || auth.getPrincipal() == null) {
                    throw new BusinessException(ErrorCode.UNAUTHENTICATED);
                }
                Object principal = auth.getPrincipal();
                if (principal instanceof User user) {
                    yield user;
                }
                throw new BusinessException(ErrorCode.UNAUTHENTICATED);
            }
        };
    }
}
