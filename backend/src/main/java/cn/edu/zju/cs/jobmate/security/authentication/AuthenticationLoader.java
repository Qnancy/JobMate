package cn.edu.zju.cs.jobmate.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Authentication loader for retrieving the current authenticated user.
 */
@Slf4j
public final class AuthenticationLoader {

    /**
     * Get the currently authenticated user from the security context.
     * 
     * @return the current User, or null if not authenticated.
     * @throws BusinessException if the user is not authenticated.
     */
    public static String getCurrentUsername() throws BusinessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new BusinessException(ErrorCode.UNAUTHENTICATED);
        }
        return switch (auth) {
            case JwtAuthenticationToken jwtAuth -> jwtAuth.getPrincipal();
            case UsernamePasswordAuthenticationToken upAuth -> upAuth.getName();
            default -> {
                log.error("Unsupported authentication type: {}",
                    auth.getClass().getName());
                throw new BusinessException(ErrorCode.UNAUTHENTICATED);
            }
        };
    }
}
