package cn.edu.zju.cs.jobmate.dto.user;

import cn.edu.zju.cs.jobmate.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * User creation request DTO
 */
@Data
@Builder
public class UserCreateRequest {
    
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 1, max = 31, message = "Username length must be between 1 and 31")
    private String name;
    
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password length cannot be less than 6 characters")
    private String password;
    
    @NotNull(message = "User role cannot be empty")
    private UserRole role;
}
