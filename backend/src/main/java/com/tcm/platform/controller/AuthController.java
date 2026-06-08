package com.tcm.platform.controller;

import com.tcm.platform.common.Result;
import com.tcm.platform.dto.LoginRequest;
import com.tcm.platform.dto.LoginResponse;
import com.tcm.platform.dto.RegisterRequest;
import com.tcm.platform.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success("注册成功", authService.registerPatient(request));
    }

    @PostMapping("/login/patient")
    public Result<LoginResponse> loginPatient(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.loginPatient(request));
    }

    @PostMapping("/login/admin")
    public Result<LoginResponse> loginAdmin(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.loginAdmin(request));
    }
}
