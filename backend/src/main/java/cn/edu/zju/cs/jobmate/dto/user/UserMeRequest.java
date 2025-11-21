package cn.edu.zju.cs.jobmate.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

/**
 * Get current user request DTO
 */
@Data
@Builder
public class UserMeRequest {
    
    @NotNull(message = "User ID cannot be empty")
    @Positive(message = "User ID must be positive")
    private Integer id;
}