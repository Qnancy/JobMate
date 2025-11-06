package cn.edu.zju.cs.jobmate.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String displayName;
    
    // 默认构造函数
    public RegisterRequest() {}
    
    // 构造函数
    public RegisterRequest(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
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
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}