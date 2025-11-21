package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.user.UserMeRequest;
import cn.edu.zju.cs.jobmate.dto.user.UserResponse;
import cn.edu.zju.cs.jobmate.dto.user.UserUpdateRequest;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
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

import java.util.Optional;

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
        
        try {
            Optional<User> user = userService.getById(id);
            
            if (user.isPresent()) {
                UserResponse response = UserResponse.from(user.get());
                logger.info("Successfully retrieved user: {}", user.get().getUsername());
                return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
            } else {
                logger.warn("User not found with ID: {}", id);
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving user with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get current user
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @Valid @RequestBody UserMeRequest request) {
        
        logger.info("Retrieving current user with ID: {}", request.getId());
        
        try {
            Optional<User> user = userService.getById(request.getId());
            
            if (user.isPresent()) {
                UserResponse response = UserResponse.from(user.get());
                logger.info("Successfully retrieved current user: {}", user.get().getUsername());
                return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
            } else {
                logger.warn("Current user not found with ID: {}", request.getId());
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving current user with ID {}: {}", request.getId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
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
        
        try {
            // Use the new updateById method that handles ID preservation
            User savedUser = userService.updateById(id, request.getName(), request.getRole());
            UserResponse response = UserResponse.from(savedUser);
            
            logger.info("Successfully updated user with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating user with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete user
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Deleting user with ID: {}", id);
        
        try {
            if (!userService.existsById(id)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
            userService.deleteById(id);
            logger.info("Successfully deleted user with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("删除成功"));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}