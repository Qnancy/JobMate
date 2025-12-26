package cn.edu.zju.cs.jobmate.dto.user;

import org.springframework.lang.NonNull;

import cn.edu.zju.cs.jobmate.dto.common.CreateRequest;
import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * User register request DTO.
 */
@Data
@Builder
public class UserRegisterRequest implements CreateRequest<User> {

    @NotBlank(message = "Username cannot be empty")
    @Size(max = 31, message = "Username length cannot exceed 31")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotNull(message = "User role cannot be null")
    private UserRole role;

    private String adminSecret; // Admin secret for creating admin users.

    @Override
    public @NonNull User toModel() {
        return new User(
            username,
            password,
            role
        );
    }

    @Override
    public String toString() {
        return "UserRegisterRequest{" +
                "username=" + ToStringUtil.wrap(username) +
                ", role=" + role +
                '}';
    }
}
