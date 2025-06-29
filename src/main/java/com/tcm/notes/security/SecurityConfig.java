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
import org.springframework.web.cors.CorsConfigurationSource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

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
            .cors(cors -> cors.configurationSource(corsConfigurationSource)) // 使用自定义CORS配置
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/me").authenticated()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                // 明确允许访问passages接口
                .requestMatchers("/api/public/passages").permitAll()
                .requestMatchers("/api/public/passages/**").permitAll()
                // 允许访问静态资源和错误页面
                .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/passages.html", "/create-admin.html", "/admin-check.html", "/simulate_browser_login.html", "/static/**", "/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()
                // 管理员页面和用户中心页面先允许访问静态资源，权限控制由前端和API请求时进行
                .requestMatchers("/admin-dashboard.html", "/admin-simple.html", "/dashboard.html", "/charts.html", "/notes.html").permitAll()
                // API请求时进行权限控制
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/admin/**").hasAuthority("ADMIN") // 管理员接口需要ADMIN权限
                .requestMatchers("/api/user/**").authenticated()
                .requestMatchers("/api/users/**").authenticated() // 用户相关接口需要认证
                .requestMatchers("/users/**").authenticated() // 用户相关接口需要认证
                .requestMatchers("/api/notes/**").authenticated()
                .requestMatchers("/api/favorites/**").authenticated() // 收藏夹API需要认证
                .requestMatchers("/api/passages/**").authenticated() // 文章API需要认证
                // 允许访问错题本API，但需要认证
                .requestMatchers("/api/wrong-answers/**").authenticated()
                // 允许访问学习记录API，但需要认证
                .requestMatchers("/api/study-records/**").authenticated()
                // 同时允许带有/api前缀的静态资源路径
                .requestMatchers("/api", "/api/", "/api/index.html", "/api/login.html", "/api/register.html", "/api/dashboard.html", "/api/passages.html", "/api/static/**", "/api/assets/**", "/api/css/**", "/api/js/**", "/api/images/**", "/api/favicon.ico").permitAll()
                .requestMatchers("/health").permitAll()
                .anyRequest().authenticated()
            );
            // 注意：在application.properties中设置了server.servlet.context-path=/
            // 但我们仍然配置了带有/api前缀的路径，以确保所有可能的访问路径都能正确处理

        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // 添加异常处理
        http.exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint((request, response, authException) -> {
                System.out.println("=== 认证入口点被调用 ===");
                System.out.println("请求URI: " + request.getRequestURI());
                System.out.println("请求方法: " + request.getMethod());
                System.out.println("异常信息: " + authException.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            })
        );

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