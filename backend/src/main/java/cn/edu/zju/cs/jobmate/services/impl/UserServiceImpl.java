package cn.edu.zju.cs.jobmate.services.impl;

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
    @Transactional(readOnly = true)
    public Optional<User> getById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
        Integer id = user.getId();
        if (id == null || !userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with id " + id + " does not exist");
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            userRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return id != null && userRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }
}

