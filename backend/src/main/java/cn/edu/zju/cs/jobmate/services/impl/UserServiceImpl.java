package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;

import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.UserRepository;
import cn.edu.zju.cs.jobmate.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for User entity.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        if (userRepository.existsByName(user.getUsername())) {
            throw new IllegalArgumentException("User with name " + user.getUsername() + " already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
        Integer id = user.getId();
        if (id == null || !userRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return userRepository.save(user);
    }

    @Override
    public User updateById(Integer id, String name, UserRole role) {
        if (id == null || !userRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        
        // Use custom update query to modify specific fields
        userRepository.updateUserById(id, name, role);
        
        // Return the updated user
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Failed to retrieve updated user with id " + id));
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && userRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }
}

