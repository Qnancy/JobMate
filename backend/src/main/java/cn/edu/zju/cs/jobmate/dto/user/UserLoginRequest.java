package cn.edu.zju.cs.jobmate.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * User login request DTO
 */
@Data
@Builder
public class UserLoginRequest {
    
    @NotBlank(message = "Username cannot be empty")
    private String name;
    
    @NotBlank(message = "Password cannot be empty")
    private String password;
}