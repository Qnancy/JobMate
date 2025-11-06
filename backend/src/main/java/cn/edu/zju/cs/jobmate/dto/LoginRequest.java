package cn.edu.zju.cs.jobmate.dto;

public class LoginRequest {
    private String username;
    private String password;
    
    // 默认构造函数
    public LoginRequest() {}
    
    // 构造函数
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Getter和Setter方法
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}