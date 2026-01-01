package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.configs.properties.AdminProperties;
import cn.edu.zju.cs.jobmate.dto.user.UserRegisterRequest;
import cn.edu.zju.cs.jobmate.dto.user.UserUpdateRequest;
import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.UserRepository;
import cn.edu.zju.cs.jobmate.security.authentication.AuthenticationLoader;
import cn.edu.zju.cs.jobmate.services.impl.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(UserServiceImpl.class)
class UserServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AdminProperties adminProperties;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Test
    @SuppressWarnings("null")
    void testRegister_User_Success() {
        UserRegisterRequest dto = UserRegisterRequest.builder()
            .username("user1")
            .password("pwd")
            .role(UserRole.USER)
            .build();
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(passwordEncoder.encode("pwd")).thenReturn("encodedPwd");
        User user = new User("user1", "encodedPwd", UserRole.USER);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(dto);
        assertEquals("user1", result.getUsername());
        assertEquals("encodedPwd", result.getPassword());
        assertEquals(UserRole.USER, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @SuppressWarnings("null")
    void testRegister_Admin_Success() {
        UserRegisterRequest dto = UserRegisterRequest.builder()
            .username("admin1")
            .password("pwd")
            .role(UserRole.ADMIN)
            .adminSecret("secret")
            .build();
        when(adminProperties.getSecret()).thenReturn("secret");
        when(userRepository.existsByUsername("admin1")).thenReturn(false);
        when(passwordEncoder.encode("pwd")).thenReturn("encodedPwd");
        User user = new User("admin1", "encodedPwd", UserRole.ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(dto);
        assertEquals("admin1", result.getUsername());
        assertEquals(UserRole.ADMIN, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_AdminSecretInvalid() {
        UserRegisterRequest dto = UserRegisterRequest.builder()
            .username("admin1")
            .password("pwd")
            .role(UserRole.ADMIN)
            .adminSecret("wrong")
            .build();
        when(adminProperties.getSecret()).thenReturn("secret");
        when(userRepository.existsByUsername("admin1")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.register(dto));
        assertEquals(ErrorCode.INVALID_ADMIN_SECRET, ex.getErrorCode());
    }

    @Test
    void testRegister_UserAlreadyExists() {
        UserRegisterRequest dto = UserRegisterRequest.builder()
            .username("user1")
            .password("pwd")
            .role(UserRole.USER)
            .build();
        when(userRepository.existsByUsername("user1")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.register(dto));
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void testDelete_Success() {
        try (MockedStatic<AuthenticationLoader> mocked = mockStatic(AuthenticationLoader.class)) {
            mocked.when(AuthenticationLoader::getCurrentUsername).thenReturn("user1");
            doNothing().when(userRepository).deleteByUsername("user1");

            userService.delete();
            verify(userRepository).deleteByUsername("user1");
        }
    }

    @Test
    @SuppressWarnings("null")
    void testUpdate_Success() {
        UserUpdateRequest dto = UserUpdateRequest.builder()
            .username("newname")
            .password("newpwd")
            .build();
        User user = new User("user1", "oldpwd", UserRole.USER);

        try (MockedStatic<AuthenticationLoader> mocked = mockStatic(AuthenticationLoader.class)) {
            mocked.when(AuthenticationLoader::getCurrentUsername).thenReturn("user1");
            when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.update(dto);
            assertEquals("newname", result.getUsername());
            assertEquals("newpwd", result.getPassword());
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void testUpdate_NoUpdates() {
        UserUpdateRequest dto = UserUpdateRequest.builder().build();
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.update(dto));
        assertEquals(ErrorCode.NO_UPDATES, ex.getErrorCode());
    }

    @Test
    void testGetById_Success() {
        User user = new User("user1", "pwd", UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);
        assertEquals("user1", result.getUsername());
    }

    @Test
    void testGetById_MissingParameter() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.getById(null));
        assertEquals(ErrorCode.MISSING_PARAMETER, ex.getErrorCode());
    }

    @Test
    void testGetById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.getById(1L));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testGetCurrentUser_Success() {
        try (MockedStatic<AuthenticationLoader> mocked = mockStatic(AuthenticationLoader.class)) {
            mocked.when(AuthenticationLoader::getCurrentUsername).thenReturn("user1");
            User user = new User("user1", "pwd", UserRole.USER);
            when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

            User result = userService.getCurrentUser();
            assertEquals("user1", result.getUsername());
        }
    }

    @Test
    void testGetCurrentUser_NotFound() {
        try (MockedStatic<AuthenticationLoader> mocked = mockStatic(AuthenticationLoader.class)) {
            mocked.when(AuthenticationLoader::getCurrentUsername).thenReturn("user1");
            when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.getCurrentUser());
            assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        }
    }
}
