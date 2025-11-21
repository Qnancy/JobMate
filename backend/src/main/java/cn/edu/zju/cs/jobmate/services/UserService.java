package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User entity.
 */
public interface UserService {

    /**
     * Create a new user.
     *
     * @param user user to create
     * @return created user
     */
    User create(User user);

    /**
     * Get user by id.
     *
     * @param id user id
     * @return optional user
     */
    Optional<User> getById(Integer id);

    /**
     * Get user by name.
     *
     * @param name user name
     * @return optional user
     */
    Optional<User> getByName(String name);

    /**
     * Get all users.
     *
     * @return list of users
     */
    List<User> getAll();

    /**
     * Update user.
     *
     * @param user user to update
     * @return updated user
     */
    User update(User user);

    /**
     * Update user by id with new values.
     *
     * @param id user id
     * @param name new username
     * @param role new user role
     * @return updated user
     */
    User updateById(Integer id, String name, UserRole role);

    /**
     * Delete user by id.
     *
     * @param id user id
     */
    void deleteById(Integer id);

    /**
     * Check if user exists by id.
     *
     * @param id user id
     * @return true if exists
     */
    boolean existsById(Integer id);

    /**
     * Check if user exists by name.
     *
     * @param name user name
     * @return true if exists
     */
    boolean existsByName(String name);
}

