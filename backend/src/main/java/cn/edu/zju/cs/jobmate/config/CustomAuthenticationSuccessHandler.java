package cn.edu.zju.cs.jobmate.config;

import cn.edu.zju.cs.jobmate.dto.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.UserResponse;
import cn.edu.zju.cs.jobmate.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        User user = (User) authentication.getPrincipal();
        UserResponse userResponse = UserResponse.fromUser(user);
        ApiResponse<UserResponse> apiResponse = ApiResponse.success("登录成功", userResponse);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}