package cn.edu.zju.cs.jobmate.dto.user;

import cn.edu.zju.cs.jobmate.dto.common.UpdateRequest;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

/**
 * User update request DTO.
 */
@Data
@Builder
public class UserUpdateRequest implements UpdateRequest<User> {

    @Size(max = 31, message = "Username length cannot exceed 31")
    private String username;

    private String password;

    @Override
    public boolean isUpdatable() {
        return username != null || password != null;
    }

    @Override
    public void apply(User user) {
        if (username != null) {
            user.setUsername(username);
        }
        if (password != null) {
            user.setPassword(password);
        }
    }

    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "username=" + ToStringUtil.wrap(username) + '}';
    }
}
