package cn.edu.zju.cs.jobmate.integrations;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.dto.authentication.LoginRequest;
import cn.edu.zju.cs.jobmate.dto.user.UserRegisterRequest;
import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.repositories.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testNoAuthentication_AccessRejected() throws Exception {
        mockMvc.perform(get("/api/test"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SuppressWarnings("null")
    void testRegisterAndLogin_AccessSuccessful() throws Exception {
        // Register.
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
            .username("testuser")
            .password("TestPassword123")
            .role(UserRole.USER)
            .build();
        mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("注册成功"))
            .andExpect(jsonPath("$.data").isNotEmpty());

        // Login.
        LoginRequest loginRequest = LoginRequest.builder()
            .username("testuser")
            .password("TestPassword123")
            .build();
        MvcResult result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("登录成功"))
            .andExpect(jsonPath("$.data.token").isNotEmpty())
            .andExpect(jsonPath("$.data.token_type").value("Bearer"))
            .andReturn();
        JsonNode root = mapper.readTree(result.getResponse().getContentAsString());
        String token = root.path("data").path("token").asText();

        // Access protected endpoint.
        mockMvc.perform(get("/api/test")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(content().string("ok"));

        // Get current user info.
        mockMvc.perform(get("/api/users/me")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("获取成功"))
            .andExpect(jsonPath("$.data.username").value("testuser"));

        // Clean.
        userRepository.deleteAll();
    }

    @Test
    void testInvalidToken() throws Exception {
        mockMvc.perform(get("/api/test")
            .header("Authorization", "Bearer invalidtoken"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401))
            .andExpect(jsonPath("$.message").value("无效或过期的令牌"));
    }

    @Test
    @SuppressWarnings("null")
    void testUsernameNotFound() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("nonexistentuser")
            .password("SomePassword123")
            .build();
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401))
            .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    @SuppressWarnings("null")
    void testIncorrectPassword() throws Exception {
        // Register.
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
            .username("testuser")
            .password("TestPassword123")
            .role(UserRole.USER)
            .build();
        mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("注册成功"))
            .andExpect(jsonPath("$.data").isNotEmpty());

        // Login.
        LoginRequest loginRequest = LoginRequest.builder()
            .username("testuser")
            .password("WrongPassword123")
            .build();
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401))
            .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        // Clean.
        userRepository.deleteAll();
    }
}
