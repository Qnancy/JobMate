package cn.edu.zju.cs.jobmate.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.security.authentication.AuthenticationLoader;
import cn.edu.zju.cs.jobmate.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authentication REST controller.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        authenticationService.logout(authorization);
        log.info("User(username={}) logged out successfully",
            AuthenticationLoader.getCurrentUsername());
        return ResponseEntity.ok(ApiResponse.ok("登出成功"));
    }
}
