package cn.edu.zju.cs.jobmate.dto.user;

import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * User response DTO
 * Does not contain sensitive information (e.g., password)
 */
@Data
@Builder
public class UserResponse {
    
    private Integer id;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;

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
}
