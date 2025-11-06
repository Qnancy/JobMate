package cn.edu.zju.cs.jobmate.service;

import cn.edu.zju.cs.jobmate.entity.User;
import cn.edu.zju.cs.jobmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param displayName 昵称（可选）
     * @return 注册结果
     */
    public RegisterResult register(String username, String password, String displayName) {
        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            return new RegisterResult(false, "用户名不能为空", null);
        }
        if (password == null || password.length() < 3) {
            return new RegisterResult(false, "密码至少3位", null);
        }
        
        username = username.trim();
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            return new RegisterResult(false, "用户名已存在", null);
        }
        
        // 创建新用户
        User user = new User(username, password, displayName);
        User savedUser = userRepository.save(user);
        
        return new RegisterResult(true, "注册成功", savedUser);
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    public LoginResult login(String username, String password) {
        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            return new LoginResult(false, "用户名不能为空", null);
        }
        if (password == null || password.isEmpty()) {
            return new LoginResult(false, "密码不能为空", null);
        }
        
        username = username.trim();
        
        // 查找用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return new LoginResult(false, "用户名或密码错误", null);
        }
        
        User user = userOpt.get();
        
        // 验证密码（注意：实际项目中应该使用加密密码）
        if (!password.equals(user.getPassword())) {
            return new LoginResult(false, "用户名或密码错误", null);
        }
        
        return new LoginResult(true, "登录成功", user);
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息（不包含密码）
     */
    public User findUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 创建一个不包含密码的用户对象
            User safeUser = new User();
            safeUser.setId(user.getId());
            safeUser.setUsername(user.getUsername());
            safeUser.setDisplayName(user.getDisplayName());
            safeUser.setAvatarUrl(user.getAvatarUrl());
            safeUser.setCreatedAt(user.getCreatedAt());
            safeUser.setUpdatedAt(user.getUpdatedAt());
            return safeUser;
        }
        return null;
    }
    
    // 内部类：注册结果
    public static class RegisterResult {
        private boolean success;
        private String message;
        private User user;
        
        public RegisterResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        // Getter和Setter方法
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
    }
    
    // 内部类：登录结果
    public static class LoginResult {
        private boolean success;
        private String message;
        private User user;
        
        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        // Getter和Setter方法
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
    }
}