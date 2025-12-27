package cn.edu.zju.cs.jobmate.dto.authentication;

import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Login request DTO.
 */
@Data
@Builder
public class LoginRequest {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Override
    public String toString() {
        return "LoginRequest{" +
                "username=" + ToStringUtil.wrap(username) +
                '}';
    }
}
