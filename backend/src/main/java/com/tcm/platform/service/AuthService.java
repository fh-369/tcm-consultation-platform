package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tcm.platform.dto.LoginRequest;
import com.tcm.platform.dto.LoginResponse;
import com.tcm.platform.dto.RegisterRequest;
import com.tcm.platform.entity.PatientAccount;
import com.tcm.platform.entity.User;
import com.tcm.platform.mapper.PatientAccountMapper;
import com.tcm.platform.mapper.UserMapper;
import com.tcm.platform.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 患者、医生和管理员的注册登录业务。
 */
@Service
public class AuthService {

    private static final String PATIENT_ROLE = "patient";

    private final PatientAccountMapper patientAccountMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            PatientAccountMapper patientAccountMapper,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.patientAccountMapper = patientAccountMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public LoginResponse registerPatient(RegisterRequest request) {
        PatientAccount existingAccount = findPatientByUsername(request.getUsername());
        if (existingAccount != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        PatientAccount account = new PatientAccount();
        account.setUsername(request.getUsername());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setDisplayName(defaultDisplayName(request.getDisplayName(), request.getUsername()));
        account.setPhone(request.getPhone());

        if (patientAccountMapper.insert(account) != 1) {
            throw new IllegalStateException("患者注册失败");
        }

        return createLoginResponse(
                account.getId(),
                account.getUsername(),
                PATIENT_ROLE,
                account.getDisplayName()
        );
    }

    public LoginResponse loginPatient(LoginRequest request) {
        PatientAccount account = findPatientByUsername(request.getUsername());
        if (account == null || !passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        return createLoginResponse(
                account.getId(),
                account.getUsername(),
                PATIENT_ROLE,
                account.getDisplayName()
        );
    }

    public LoginResponse loginAdmin(LoginRequest request) {
        User user = findUserByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        return createLoginResponse(user.getId(), user.getUsername(), user.getRole(), user.getDisplayName());
    }

    private PatientAccount findPatientByUsername(String username) {
        return patientAccountMapper.selectOne(
                Wrappers.<PatientAccount>lambdaQuery().eq(PatientAccount::getUsername, username)
        );
    }

    private User findUserByUsername(String username) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    private LoginResponse createLoginResponse(Long userId, String username, String role, String displayName) {
        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtil.generateToken(userId, username, role));
        response.setRole(role);
        response.setUserId(userId);
        response.setDisplayName(displayName);
        return response;
    }

    private String defaultDisplayName(String displayName, String username) {
        return displayName == null || displayName.isBlank() ? username : displayName;
    }
}
