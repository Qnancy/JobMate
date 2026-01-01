package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.dto.user.UserRegisterRequest;
import cn.edu.zju.cs.jobmate.dto.user.UserUpdateRequest;
import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.services.UserService;
import cn.edu.zju.cs.jobmate.testing.ControllerTestStartUp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(value = MonitorProperties.class)
class UserControllerTest extends ControllerTestStartUp {

    @MockitoBean
    private UserService userService;

    @Test
    @SuppressWarnings("null")
    void testRegisterUser() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
            .username("user1")
            .password("pwd")
            .role(UserRole.USER)
            .build();

        User user = new User(
            "user1",
            "pwd",
            UserRole.USER
        );

        when(userService.register(any(UserRegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("注册成功"))
            .andExpect(jsonPath("$.data.username").value("user1"))
            .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void testDeleteCurrentUser() throws Exception {
        doNothing().when(userService).delete();

        mockMvc.perform(delete("/api/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateCurrentUser() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
            .username("oldname")
            .build();

        User user = new User(
            "newname",
            "newpwd",
            UserRole.USER
        );
        user.setRole(UserRole.USER);

        when(userService.update(any(UserUpdateRequest.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.username").value("newname"))
            .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void testGetCurrentUser() throws Exception {
        User user = new User(
            "user1",
            "pwd",
            UserRole.USER
        );

        when(userService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("获取成功"))
            .andExpect(jsonPath("$.data.username").value("user1"))
            .andExpect(jsonPath("$.data.role").value("USER"));
    }
}
