package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.dto.*;
import cn.edu.zju.cs.jobmate.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest request) {
        try {
            AuthService.LoginResult result = authService.login(
                request.getUsername(), 
                request.getPassword()
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
     * 测试接口
     * GET /api/auth/test
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(ApiResponse.success("API测试成功", "JobMate认证服务运行正常"));
    }
}