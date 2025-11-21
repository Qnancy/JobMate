package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find user by name.
     *
     * @param name user name
     * @return optional user
     */
    Optional<User> findByName(String name);

    /**
     * Check if user exists by name.
     *
     * @param name user name
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Update user by id with new values.
     *
     * @param id user id
     * @param name new username
     * @param role new user role
     */
    @Modifying
    @Query("UPDATE User u SET u.name = :name, u.role = :role WHERE u.id = :id")
    void updateUserById(@Param("id") Integer id, 
                       @Param("name") String name, 
                       @Param("role") UserRole role);
}

