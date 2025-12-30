package cn.edu.zju.cs.jobmate.configs.security.filters;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.edu.zju.cs.jobmate.configs.security.authentication.JwtAuthenticationToken;
import cn.edu.zju.cs.jobmate.configs.security.jwt.JwtTokenProvider;
import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.utils.httpservlet.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT authentication filter to process incoming requests and validate JWT tokens.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public final ResponseUtil responder;
    public final JwtTokenProvider provider;
    public final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        try {
            // Get the JWT from the Authorization header.
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return ;
            }
            String token = authHeader.substring(7);

            // Parse and validate the token.
            if (!provider.validateToken(token)) {
                log.info("JWT invalid or expired");
                responder.writeResponse(response, ApiResponse.error(ErrorCode.INVALID_TOKEN));
                return ;
            }

            // Set authentication in the security context.
            String username = provider.getUsernameFromToken(token);
            User user = (User) userDetailsService.loadUserByUsername(username);
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                user,
                token,
                request
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("JWT authentication successful, loaded User(id={})", user.getId());

            // Continue the filter chain.
            filterChain.doFilter(request, response);
        } catch (ParseException e) {
            log.info("JWT parsing error: {}", e.getMessage());
            responder.writeResponse(response, ApiResponse.error(ErrorCode.INVALID_TOKEN));
        } catch (Exception e) {
            log.error("JWT authentication error: {}", e.getMessage());
            responder.writeResponse(response, ApiResponse.error(ErrorCode.AUTHENTICATION_FAILED));
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // TODO: implement.
        String path = request.getServletPath();
        return path.equals("/api/auth/login")
            || path.equals("/api/users/register");
    }
}
