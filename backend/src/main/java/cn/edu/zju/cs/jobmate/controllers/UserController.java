package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.user.UserMeRequest;
import cn.edu.zju.cs.jobmate.dto.user.UserResponse;
import cn.edu.zju.cs.jobmate.dto.user.UserUpdateRequest;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * User REST Controller
 * 
 * Provides RESTful APIs for user management operations
 * 
 * @author JobMate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Constructor injection for UserService
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Retrieving user with ID: {}", id);
        
        User user = userService.getById(id);
        UserResponse response = UserResponse.from(user);
        logger.info("Successfully retrieved user: {}", user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Get current user
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @Valid @RequestBody UserMeRequest request) {
        
        logger.info("Retrieving current user with ID: {}", request.getId());
        
        User user = userService.getById(request.getId());
        UserResponse response = UserResponse.from(user);
        logger.info("Successfully retrieved current user: {}", user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Update user
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable @NotNull @Positive Integer id,
            @Valid @RequestBody UserUpdateRequest request) {
        
        logger.info("Updating user with ID: {}", id);
        
        User savedUser = userService.updateById(id, request.getName(), request.getRole());
        UserResponse response = UserResponse.from(savedUser);
        
        logger.info("Successfully updated user with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
    }

    /**
     * Delete user
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Deleting user with ID: {}", id);
        
        userService.deleteUserById(id);
        logger.info("Successfully deleted user with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功"));
    }
}