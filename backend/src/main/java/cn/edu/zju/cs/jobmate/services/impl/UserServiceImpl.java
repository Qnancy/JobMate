package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.configs.properties.AdminProperties;
import cn.edu.zju.cs.jobmate.dto.user.*;
import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;

import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.UserRepository;
import cn.edu.zju.cs.jobmate.security.authentication.AuthenticationLoader;
import cn.edu.zju.cs.jobmate.services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User service implementation.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdminProperties adminProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(UserRegisterRequest dto) {
        // Validate admin secret for admin registration.
        if (dto.getRole() == UserRole.ADMIN &&
            !dto.getAdminSecret().equals(adminProperties.getSecret())) {
            throw new BusinessException(ErrorCode.INVALID_ADMIN_SECRET);
        }
        // Check for existing username.
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        // Encode password.
        User user = dto.toModel();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User update(Integer id, UserUpdateRequest dto) {
        // Check if no updates needed.
        if (!dto.isUpdatable()) {
            throw new BusinessException(ErrorCode.NO_UPDATES);
        }

        // Fetch existing User.
        User user = getById(id);

        // Update and save.
        dto.apply(user);
        return userRepository.save(Objects.requireNonNull(user));
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return userRepository.findByUsername(AuthenticationLoader.getCurrentUsername())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
