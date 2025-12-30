package cn.edu.zju.cs.jobmate.integrations;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import cn.edu.zju.cs.jobmate.testing.EmbeddedRedis;

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

    // Embedded Redis for testing token storage.
    private static final EmbeddedRedis embeddedRedis = new EmbeddedRedis();

    @BeforeAll
    static void setup() throws Exception {
        embeddedRedis.start();
    }

    @AfterAll
    static void teardown() {
        embeddedRedis.stop();
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void testAccessWithNoAuthentication_Rejected() throws Exception {
        mockMvc.perform(get("/api/test"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SuppressWarnings("null")
    void testLoginAndAccessAndLogout_Successful() throws Exception {
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

        // Logout.
        mockMvc.perform(post("/api/auth/logout")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("登出成功"));

        // Access protected endpoint after logout.
        mockMvc.perform(get("/api/test")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401))
            .andExpect(jsonPath("$.message").value("无效或过期的令牌"));
    }

    @Test
    void testAccessWithInvalidToken_Rejected() throws Exception {
        // Invalid token.
        mockMvc.perform(get("/api/test")
            .header("Authorization", "Bearer invalidtoken"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401))
            .andExpect(jsonPath("$.message").value("无效或过期的令牌"));

        mockMvc.perform(get("/api/test")
            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNzY3MTgwNTUzLCJpYXQiOjE3NjcwOTQxNTMsImp0aSI6ImJkNmM2NDRlLWEwZWQtNDJhZi1iMmE2LWQ3NTMwY2JmOTg1ZSJ9.N-O2phE0LmUei2cq3wDa6y0SfdPEe7WeChXUClJy67U"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401))
            .andExpect(jsonPath("$.message").value("无效或过期的令牌"));

        mockMvc.perform(get("/api/test")
            .header("Authorization", "bear er "))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401))
            .andExpect(jsonPath("$.message").value("认证失败"));
    }

    @Test
    @SuppressWarnings("null")
    void testLogin_UsernameNotFound() throws Exception {
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
    void testLogin_IncorrectPassword() throws Exception {
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
    }
}
