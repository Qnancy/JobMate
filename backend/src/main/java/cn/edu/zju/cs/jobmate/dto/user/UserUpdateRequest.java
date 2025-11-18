package cn.edu.zju.cs.jobmate.dto.user;

import cn.edu.zju.cs.jobmate.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * User update request DTO
 * All fields are optional
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequest {
    
    @Size(min = 1, max = 31, message = "Username length must be between 1 and 31")
    private String name;
    
    @Size(min = 6, message = "Password length cannot be less than 6 characters")
    private String password;
    
    private UserRole role;
}
