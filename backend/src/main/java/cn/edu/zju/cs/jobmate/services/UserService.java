package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.user.*;
import cn.edu.zju.cs.jobmate.models.User;

/**
 * User service interface.
 */
public interface UserService {

    /**
     * Register a new user.
     *
     * @param dto user register request
     * @return registered user
     * @throws BusinessException if user with the same username already exists
     *  or admin secret is incorrect
     */
    User register(UserRegisterRequest dto);

    /**
     * Delete current user.
     */
    void delete();

    /**
     * Update current user.
     *
     * @param dto user update request
     * @return updated user
     */
    User update(UserUpdateRequest dto);

    /**
     * Get user by id.
     *
     * @param id user id
     * @return retrieved user
     */
    User getById(Integer id);

    /**
     * Get current logged-in user.
     *
     * @return current user
     * @throws BusinessException if no user is logged in or not found
     */
    User getCurrentUser();
}
