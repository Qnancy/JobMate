package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.dto.*;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // 允许跨域访问
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthService.RegisterResult result = authService.register(
                request.getUsername(), 
                request.getPassword(), 
                request.getDisplayName()
            );
            
            if (result.isSuccess()) {
                UserResponse userResponse = UserResponse.fromUser(result.getUser());
                return ResponseEntity.ok(ApiResponse.success(result.getMessage(), userResponse));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(result.getMessage()));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("服务器内部错误：" + e.getMessage()));
        }
    }
    
    /**
     * 获取当前登录用户信息
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                User currentUser = (User) authentication.getPrincipal();
                UserResponse userResponse = UserResponse.fromUser(currentUser);
                return ResponseEntity.ok(ApiResponse.success("获取当前用户信息成功", userResponse));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户未登录"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("服务器内部错误：" + e.getMessage()));
        }
    }
    
    /**
     * 获取用户信息
     * GET /api/auth/profile?username=xxx
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@RequestParam String username) {
        try {
            var user = authService.findUserByUsername(username);
            if (user != null) {
                UserResponse userResponse = UserResponse.fromUser(user);
                return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", userResponse));
            } else {
                return ResponseEntity.notFound()
                    .build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("服务器内部错误：" + e.getMessage()));
        }
    }
    
    /**
     * 获取CSRF Token
     * GET /api/auth/csrf
     */
    @GetMapping("/csrf")
    public ResponseEntity<ApiResponse<String>> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            return ResponseEntity.ok(ApiResponse.success("获取CSRF Token成功", csrfToken.getToken()));
        } else {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("无法获取CSRF Token"));
        }
    }

    /**
     * 测试接口
     * GET /api/auth/test
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(ApiResponse.success("API测试成功", "JobMate认证服务运行正常"));
    }
}