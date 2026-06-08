package com.tcm.platform.config;

import com.tcm.platform.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * 
 * 任务：
 * 1. 禁用 CSRF（无状态 API 不需要）
 * 2. 配置无状态会话管理
 * 3. 设置 URL 权限规则
 * 4. 添加 JWT 过滤器
 * 
 * URL 权限规则：
 * - /api/auth/**     → 允许所有访问（permitAll）
 * - /api/patient/**  → 需要 PATIENT/DOCTOR/ADMIN 角色
 * - /api/admin/**    → 需要 DOCTOR/ADMIN 角色
 * - /uploads/**      → 允许所有访问
 * - /swagger-ui/**   → 允许所有访问
 * - 其他请求         → 需要认证
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: 配置 SecurityFilterChain
        // 提示：
        // http
        //     .csrf(AbstractHttpConfigurer::disable)
        //     .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        //     .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //     .authorizeHttpRequests(auth -> auth
        //         .requestMatchers("/api/auth/**").permitAll()
        //         .requestMatchers("/api/patient/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
        //         .requestMatchers("/api/admin/**").hasAnyRole("DOCTOR", "ADMIN")
        //         .requestMatchers("/uploads/**").permitAll()
        //         .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
        //         .anyRequest().authenticated()
        //     )
        //     .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // return http.build();
        
        throw new UnsupportedOperationException("TODO: 实现 securityFilterChain 配置");
    }
}
