package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DAO for {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Delete user by username.
     * 
     * @param username user name
     */
    void deleteByUsername(String username);

    /**
     * Find user by username.
     *
     * @param username user name
     * @return optional user
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if user exists by username.
     *
     * @param username user name
     * @return true if exists
     */
    boolean existsByUsername(String username);
}
