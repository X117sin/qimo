package com.tcm.notes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链
     * @param http HTTP安全配置
     * @return 安全过滤器链
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                // 明确允许访问passages接口
                .requestMatchers("/api/public/passages").permitAll()
                .requestMatchers("/api/public/passages/**").permitAll()
                // 允许访问管理员用户创建接口
                .requestMatchers("/admin/users").permitAll()
                // 允许访问静态资源
                .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/passages.html", "/create-admin.html", "/admin-check.html", "/static/**", "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // 管理员页面和用户中心页面先允许访问静态资源，权限控制由前端和API请求时进行
                .requestMatchers("/admin-dashboard.html", "/dashboard.html").permitAll()
                // API请求时进行权限控制
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").authenticated()
.requestMatchers("/api/notes/**").authenticated()
                // 允许访问错题本API，但需要认证
                .requestMatchers("/api/wrong-answers/**").authenticated()
                // 同时允许带有/api前缀的静态资源路径
                .requestMatchers("/api", "/api/", "/api/index.html", "/api/login.html", "/api/register.html", "/api/dashboard.html", "/api/passages.html", "/api/static/**", "/api/assets/**", "/api/css/**", "/api/js/**", "/api/images/**", "/api/favicon.ico").permitAll()
                .requestMatchers("/health").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
            // 注意：在application.properties中设置了server.servlet.context-path=/
            // 但我们仍然配置了带有/api前缀的路径，以确保所有可能的访问路径都能正确处理

        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 密码编码器
     * @return BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * @param authenticationConfiguration 认证配置
     * @return 认证管理器
     * @throws Exception 异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}