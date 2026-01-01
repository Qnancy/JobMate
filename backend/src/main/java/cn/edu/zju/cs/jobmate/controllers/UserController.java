package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.user.*;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User REST Controller.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Register a new User.
     * 
     * @apiNote POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
        @Valid @RequestBody UserRegisterRequest request
    ) {
        log.info("Registering User with {}", request);
        User user = userService.register(request);
        UserResponse response = UserResponse.from(user);
        log.info("Successfully registered User(id={})", response.getId());
        return ResponseEntity.ok(ApiResponse.ok("注册成功", response));
    }

    /**
     * Delete current User.
     * 
     * @apiNote DELETE /api/users/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> delete() {
        userService.delete();
        log.info("Successfully deleted current User");
        return ResponseEntity.ok(ApiResponse.ok("删除成功"));
    }

    /**
     * Update current User.
     * 
     * @apiNote PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> update(
        @Valid @RequestBody UserUpdateRequest request
    ) {
        log.info("Updating current User with {})", request);
        User user = userService.update(request);
        UserResponse response = UserResponse.from(user);
        log.info("Successfully updated current User");
        return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
    }

    /**
     * Get current User info.
     * 
     * @apiNote GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        User user = userService.getCurrentUser();
        UserResponse response = UserResponse.from(user);
        log.info("Successfully retrieved current User");
        return ResponseEntity.ok(ApiResponse.ok("获取成功", response));
    }
}
