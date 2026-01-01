package cn.edu.zju.cs.jobmate.integrations;

import cn.edu.zju.cs.jobmate.configs.properties.AdminProperties;
import cn.edu.zju.cs.jobmate.dto.user.UserRegisterRequest;
import cn.edu.zju.cs.jobmate.dto.user.UserUpdateRequest;
import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminProperties adminProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.save(new User(
            "user1",
            "pwd",
            UserRole.USER
        ));
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @SuppressWarnings("null")
    void testRegister_User_Success() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
            .username("user2")
            .password("pwd2")
            .role(UserRole.USER)
            .build();

        mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("注册成功"))
            .andExpect(jsonPath("$.data.username").value("user2"));
    }

    @Test
    @SuppressWarnings("null")
    void testRegister_User_AlreadyExists() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
            .username("user1")
            .password("pwd")
            .role(UserRole.USER)
            .build();

        mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(409))
            .andExpect(jsonPath("$.message").value("用户已存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testRegister_Admin_Success() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
            .username("admin1")
            .password("adminpwd")
            .role(UserRole.ADMIN)
            .adminSecret(adminProperties.getSecret())
            .build();

        mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("注册成功"))
            .andExpect(jsonPath("$.data.username").value("admin1"));
    }

    @Test
    @SuppressWarnings("null")
    void testRegister_Admin_InvalidSecret() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
            .username("admin2")
            .password("adminpwd2")
            .role(UserRole.ADMIN)
            .adminSecret("wrongsecret")
            .build();
        
        mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(403))
            .andExpect(jsonPath("$.message").value("无效的管理员密钥"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void testDeleteCurrentUser_Success() throws Exception {
        mockMvc.perform(delete("/api/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser(username = "user1", roles = {"USER"})
    void testUpdateCurrentUser_Success() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
            .username("newname")
            .password("newpwd")
            .build();

        mockMvc.perform(put("/api/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.username").value("newname"));
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser(username = "user1", roles = {"USER"})
    void testUpdateCurrentUser_NoUpdate() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder().build();

        mockMvc.perform(put("/api/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("没有更新"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void testGetCurrentUser_Success() throws Exception {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("获取成功"))
            .andExpect(jsonPath("$.data.username").value("user1"));
    }

    @Test
    @WithMockUser(username = "notfound", roles = {"USER"})
    void testGetCurrentUser_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("用户不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }
}
