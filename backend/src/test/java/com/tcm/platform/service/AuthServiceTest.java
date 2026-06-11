package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.tcm.platform.dto.LoginRequest;
import com.tcm.platform.dto.LoginResponse;
import com.tcm.platform.dto.RegisterRequest;
import com.tcm.platform.entity.PatientAccount;
import com.tcm.platform.entity.User;
import com.tcm.platform.mapper.PatientAccountMapper;
import com.tcm.platform.mapper.UserMapper;
import com.tcm.platform.security.JwtUtil;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @BeforeAll
    static void initializeTableInfo() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "patient-test"), PatientAccount.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "user-test"), User.class);
    }

    @Mock
    private PatientAccountMapper patientAccountMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    void registerPatientEncodesPasswordUsesUsernameAsDefaultDisplayNameAndReturnsToken() {
        when(patientAccountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(passwordEncoder.encode("patient123")).thenReturn("encoded-password");
        when(patientAccountMapper.insert(any(PatientAccount.class))).thenAnswer(invocation -> {
            PatientAccount account = invocation.getArgument(0);
            account.setId(9L);
            return 1;
        });
        when(jwtUtil.generateToken(9L, "patient1", "patient")).thenReturn("patient-token");
        AuthService service = service();
        RegisterRequest request = registerRequest("patient1", "patient123", " ", "13800000000");

        LoginResponse response = service.registerPatient(request);

        assertThat(response.getToken()).isEqualTo("patient-token");
        assertThat(response.getRole()).isEqualTo("patient");
        assertThat(response.getDisplayName()).isEqualTo("patient1");
        verify(passwordEncoder).encode("patient123");
        verify(patientAccountMapper).insert(any(PatientAccount.class));
    }

    @Test
    void registerPatientRejectsDuplicateUsernameBeforeEncodingOrInsert() {
        when(patientAccountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(new PatientAccount());

        assertThatThrownBy(() -> service().registerPatient(registerRequest("patient1", "patient123", null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户名已存在");

        verify(passwordEncoder, never()).encode(any());
        verify(patientAccountMapper, never()).insert(any());
    }

    @Test
    void registerPatientFailsWhenMapperDoesNotInsertRecord() {
        when(patientAccountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(passwordEncoder.encode("patient123")).thenReturn("encoded-password");
        when(patientAccountMapper.insert(any(PatientAccount.class))).thenReturn(0);

        assertThatThrownBy(() -> service().registerPatient(registerRequest("patient1", "patient123", null, null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("患者注册失败");

        verify(jwtUtil, never()).generateToken(any(), any(), any());
    }

    @Test
    void loginPatientRejectsIncorrectPassword() {
        PatientAccount account = patient(5L, "patient1", "encoded-password", "李女士");
        when(patientAccountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(account);
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> service().loginPatient(loginRequest("patient1", "wrong-password")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户名或密码错误");
    }

    @Test
    void loginAdminReturnsRoleAndDisplayName() {
        User admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPasswordHash("encoded-password");
        admin.setRole("admin");
        admin.setDisplayName("系统管理员");
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(admin);
        when(passwordEncoder.matches("admin123", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "admin", "admin")).thenReturn("admin-token");

        LoginResponse response = service().loginAdmin(loginRequest("admin", "admin123"));

        assertThat(response.getToken()).isEqualTo("admin-token");
        assertThat(response.getRole()).isEqualTo("admin");
        assertThat(response.getDisplayName()).isEqualTo("系统管理员");
    }

    private AuthService service() {
        return new AuthService(patientAccountMapper, userMapper, passwordEncoder, jwtUtil);
    }

    private RegisterRequest registerRequest(String username, String password, String displayName, String phone) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setDisplayName(displayName);
        request.setPhone(phone);
        return request;
    }

    private LoginRequest loginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private PatientAccount patient(Long id, String username, String passwordHash, String displayName) {
        PatientAccount account = new PatientAccount();
        account.setId(id);
        account.setUsername(username);
        account.setPasswordHash(passwordHash);
        account.setDisplayName(displayName);
        return account;
    }
}
