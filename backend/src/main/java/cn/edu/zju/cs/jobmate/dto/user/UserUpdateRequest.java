package cn.edu.zju.cs.jobmate.dto.user;

import cn.edu.zju.cs.jobmate.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

/**
 * 用户更新请求DTO
 * 所有字段都是可选的
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequest {
    
    @Size(min = 1, max = 31, message = "用户名长度必须在1-31之间")
    private String name;
    
    @Size(min = 6, message = "密码长度不能少于6位")
    private String password;
    
    private UserRole role;

    public UserUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}

