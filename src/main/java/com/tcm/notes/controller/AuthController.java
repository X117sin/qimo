package com.tcm.notes.controller;

import com.tcm.notes.dto.UserDto;
import com.tcm.notes.entity.User;
import com.tcm.notes.repository.UserRepository;
import com.tcm.notes.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器，处理用户注册和登录
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        logger.info("收到注册请求: 用户名={}, 邮箱={}", user.getUsername(), user.getEmail());
        
        try {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "用户名已存在");
                logger.warn("注册失败: 用户名已存在 - {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 检查邮箱是否已存在
            if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "邮箱已存在");
                logger.warn("注册失败: 邮箱已存在 - {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 创建新用户
            User newUser = new User();
            newUser.setUsername(user.getUsername());
            newUser.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码
            newUser.setEmail(user.getEmail());
            newUser.setRole("USER"); // 默认角色

            userRepository.save(newUser);
            logger.info("用户注册成功: 用户名={}, 邮箱={}", user.getUsername(), user.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("message", "用户注册成功");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("注册过程中发生错误: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "注册过程中发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return JWT令牌
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        logger.info("收到登录请求: 用户名={}", loginRequest.get("username"));
        
        try {
            // 检查请求参数
            if (loginRequest == null || loginRequest.get("username") == null || loginRequest.get("password") == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "用户名和密码不能为空");
                logger.warn("登录失败: 用户名或密码为空");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.get("username"),
                    loginRequest.get("password")
                )
            );
            
            logger.info("用户认证成功: {}", loginRequest.get("username"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户信息
            User user = userRepository.findByUsername(loginRequest.get("username"))
                .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            logger.info("从数据库获取用户信息成功: id={}, username={}, email={}", user.getId(), user.getUsername(), user.getEmail());
            
            // 生成JWT令牌
            String token = jwtTokenProvider.generateToken(user.getId().toString(), user.getRole());
            logger.info("生成JWT令牌成功: {}...", token.substring(0, Math.min(20, token.length())));
            
            // 返回令牌和用户信息以及角色信息（用于前端根据角色跳转到不同页面）
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            response.put("role", user.getRole());
            
            logger.info("登录成功，返回JWT响应");
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            logger.error("认证失败: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (RuntimeException e) {
            // 处理运行时异常
            logger.error("运行时异常: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // 处理所有其他类型的异常
            logger.error("登录过程中发生错误: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "登录过程中发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("未找到用户"));
        
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        
        return ResponseEntity.ok(userDto);
    }
}