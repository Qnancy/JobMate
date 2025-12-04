package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.UserRepository;
import cn.edu.zju.cs.jobmate.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserController功能测试")
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("测试根据ID获取用户 - 成功")
    void testGetUserById_Success() throws Exception {
        System.out.println("\n========== 测试根据ID获取用户 - 成功 ==========");
        
        // 先创建一个用户
        User user = new User("testuser", "password123", UserRole.ADMIN);
        User savedUser = userRepository.save(user);

        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.name").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.create_at").exists());

        System.out.println("✓ 根据ID查询用户成功");
    }

    @Test
    @DisplayName("测试根据ID获取用户 - 不存在")
    void testGetUserById_NotFound() throws Exception {
        System.out.println("\n========== 测试根据ID获取用户 - 不存在 ==========");
        
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))  // USER_NOT_FOUND
                .andExpect(jsonPath("$.message").value("用户不存在"));

        System.out.println("✓ 查询不存在的用户返回4041错误码");
    }

    @Test
    @DisplayName("测试获取当前用户(/me) - 成功")
    void testGetCurrentUser_Success() throws Exception {
        System.out.println("\n========== 测试获取当前用户(/me) - 成功 ==========");
        
        // 先创建一个用户
        User user = new User("currentuser", "password123", UserRole.USER);
        User savedUser = userRepository.save(user);

        String requestBody = String.format("""
            {
                "id": %d
            }
            """, savedUser.getId());

        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.name").value("currentuser"))
                .andExpect(jsonPath("$.data.role").value("USER"));

        System.out.println("✓ 获取当前用户成功");
    }

    @Test
    @DisplayName("测试获取当前用户(/me) - 用户不存在")
    void testGetCurrentUser_NotFound() throws Exception {
        System.out.println("\n========== 测试获取当前用户(/me) - 用户不存在 ==========");
        
        String requestBody = """
            {
                "id": 999
            }
            """;

        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))  // USER_NOT_FOUND
                .andExpect(jsonPath("$.message").value("用户不存在"));

        System.out.println("✓ 当前用户不存在时返回4041错误码");
    }

    @Test
    @Rollback(false)
    @DisplayName("测试更新用户 - 成功")
    void testUpdateUser_Success() throws Exception {
        System.out.println("\n========== 测试更新用户 - 成功 ==========");
        
        // 先创建一个用户
        User user = new User("originaluser", "password123", UserRole.USER);
        User savedUser = userRepository.save(user);

        String requestBody = """
            {
                "name": "updateduser",
                "role": "ADMIN"
            }
            """;

        mockMvc.perform(put("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.data.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.name").value("updateduser"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));

        // 验证数据库中的数据确实更新了
        User updatedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals(UserRole.ADMIN, updatedUser.getRole());
        System.out.println("✓ 用户更新成功");
    }

    @Test
    @DisplayName("测试更新用户 - 用户不存在")
    void testUpdateUser_NotFound() throws Exception {
        System.out.println("\n========== 测试更新用户 - 用户不存在 ==========");
        
        String requestBody = """
            {
                "name": "updateduser",
                "role": "ADMIN"
            }
            """;

        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))  // USER_NOT_FOUND
                .andExpect(jsonPath("$.message").value("用户不存在"));

        System.out.println("✓ 更新不存在的用户返回4041错误码");
    }

    @Test
    @DisplayName("测试更新用户 - 验证失败")
    void testUpdateUser_ValidationFail() throws Exception {
        System.out.println("\n========== 测试更新用户 - 验证失败 ==========");
        
        // 先创建一个用户
        User user = new User("testuser", "password123", UserRole.USER);
        User savedUser = userRepository.save(user);

        // 测试空用户名
        String invalidRequestBody = """
            {
                "name": "",
                "role": "ADMIN"
            }
            """;

        mockMvc.perform(put("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody))
                .andExpect(status().isBadRequest());

        // 验证用户数据没有改变
        User unchangedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(unchangedUser);
        assertEquals("testuser", unchangedUser.getUsername());
        System.out.println("✓ 验证失败时用户数据不会被修改");
    }

    @Test
    @DisplayName("测试删除用户 - 成功")
    void testDeleteUser_Success() throws Exception {
        System.out.println("\n========== 测试删除用户 - 成功 ==========");
        
        // 先创建一个用户
        User user = new User("deleteuser", "password123", UserRole.USER);
        User savedUser = userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"))
                .andExpect(jsonPath("$.data").isEmpty());

        // 验证数据库中的数据确实删除了
        assertFalse(userRepository.existsById(savedUser.getId()));
        System.out.println("✓ 用户删除成功");
    }

    @Test
    @DisplayName("测试删除用户 - 用户不存在")
    void testDeleteUser_NotFound() throws Exception {
        System.out.println("\n========== 测试删除用户 - 用户不存在 ==========");
        
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))  // USER_NOT_FOUND
                .andExpect(jsonPath("$.message").value("用户不存在"));

        System.out.println("✓ 删除不存在的用户返回4041错误码");
    }

    @Test
    @DisplayName("测试参数验证 - 负数ID")
    void testInvalidParameters() throws Exception {
        System.out.println("\n========== 测试参数验证 - 负数ID ==========");
        
        mockMvc.perform(get("/api/users/-1"))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 负数ID验证失败，符合预期");
    }

    @Test
    @DisplayName("测试用户角色枚举")
    void testUserRoleEnums() throws Exception {
        System.out.println("\n========== 测试用户角色枚举 ==========");
        
        // 测试不同的用户角色
        User adminUser = new User("admin", "password", UserRole.ADMIN);
        User normalUser = new User("user", "password", UserRole.USER);
        
        User savedAdmin = userRepository.save(adminUser);
        User savedUser = userRepository.save(normalUser);

        // 验证ADMIN用户
        mockMvc.perform(get("/api/users/" + savedAdmin.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("ADMIN"));

        // 验证普通用户
        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("USER"));

        System.out.println("✓ 用户角色枚举工作正常");
    }

    @Test
    @DisplayName("测试用户名长度限制")
    void testUsernameLength() throws Exception {
        System.out.println("\n========== 测试用户名长度限制 ==========");
        
        // 先创建一个用户
        User user = new User("testuser", "password123", UserRole.USER);
        User savedUser = userRepository.save(user);

        // 测试超长用户名（超过31个字符）
        String tooLongName = "a".repeat(32);
        String requestBody = String.format("""
            {
                "name": "%s",
                "role": "USER"
            }
            """, tooLongName);

        mockMvc.perform(put("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 用户名长度限制验证正常工作");
    }

}