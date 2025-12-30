package cn.edu.zju.cs.jobmate.services.impl;

import org.springframework.stereotype.Service;

import cn.edu.zju.cs.jobmate.security.jwt.JwtBlacklistManager;
import cn.edu.zju.cs.jobmate.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authentication service implementation.
 * 
 * @see AuthenticationService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtBlacklistManager jwtBlacklistManager;

    @Override
    public void logout(String authorization) {
        // JWT validation is handled by JwtAuthenticationFilter.
        String token = authorization.substring(7);
        jwtBlacklistManager.addToBlacklist(token);
    }
}
