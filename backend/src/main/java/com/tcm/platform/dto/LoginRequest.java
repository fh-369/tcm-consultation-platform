package com.tcm.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO
 */
@Data
public class LoginRequest {
    
    // TODO: 添加 @NotBlank 注解
    private String username;
    private String password;
}
