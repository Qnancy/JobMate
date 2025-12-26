package cn.edu.zju.cs.jobmate.configs.security.filters;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jose.JOSEException;

import cn.edu.zju.cs.jobmate.configs.security.authentication.JwtAuthenticationToken;
import cn.edu.zju.cs.jobmate.configs.security.jwt.JwtTokenProvider;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.utils.security.SecurityResponder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * JWT authentication filter to process incoming requests and validate JWT tokens.
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public final SecurityResponder responder;
    public final JwtTokenProvider provider;
    public final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
        SecurityResponder responder,
        JwtTokenProvider provider,
        UserDetailsService userDetailsService
    ) {
        this.responder = responder;
        this.provider = provider;
        this.userDetailsService = userDetailsService;
    }

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
                responder.writeResponse(response, ErrorCode.INVALID_TOKEN);
                return ;
            }

            // Set authentication in the security context.
            String username = provider.getUsernameFromToken(token);
            User userDetails = (User) userDetailsService.loadUserByUsername(username);
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                userDetails,
                token,
                request
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Continue the filter chain.
            filterChain.doFilter(request, response);
        } catch (ParseException | JOSEException e) {
            log.error("JWT parsing error: {}", e.getMessage());
            responder.writeResponse(response, ErrorCode.TOKEN_PARSING_ERROR);
        } catch (Exception e) {
            log.error("JWT authentication error: {}", e.getMessage());
            responder.writeResponse(response, ErrorCode.AUTHENTICATION_FAILED);
        }
    }
}
