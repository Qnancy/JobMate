package cn.edu.zju.cs.jobmate.dto.authentication;

import lombok.Builder;
import lombok.Data;

/**
 * Login response DTO.
 */
@Data
@Builder
public class LoginResponse {

    private String token;
    private String tokenType;
}
