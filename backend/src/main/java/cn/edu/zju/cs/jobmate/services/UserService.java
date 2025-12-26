package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.user.*;
import cn.edu.zju.cs.jobmate.models.User;

/**
 * User service interface.
 * 
 * TODO: add authentication and authorization methods.
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
     * Delete user by id.
     *
     * @param id user id
     */
    void delete(Integer id);

    /**
     * Update user.
     *
     * @param id user id
     * @param dto user update request
     * @return updated user
     */
    User update(Integer id, UserUpdateRequest dto);

    /**
     * Get user by id.
     *
     * @param id user id
     * @return retrieved user
     */
    User getById(Integer id);
}
