package cn.edu.zju.cs.jobmate.dto.user;

import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * User response DTO
 * Does not contain sensitive information (e.g., password)
 */
public class UserResponse {
    
    private Integer id;
    private String name;
    private UserRole role;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(Integer id, String name, UserRole role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.createdAt = createdAt;
    }

    /**
     * Convert from User entity to UserResponse
     */
    public static UserResponse from(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getCreatedAt()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

