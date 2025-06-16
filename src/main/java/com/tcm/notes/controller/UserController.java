package com.tcm.notes.controller;

import com.tcm.notes.entity.User;
import com.tcm.notes.repository.UserRepository;
import com.tcm.notes.service.StudyRecordService;
import com.tcm.notes.service.NoteService;
import com.tcm.notes.service.FavoriteService;
import com.tcm.notes.service.PassageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private StudyRecordService studyRecordService;
    
    @Autowired
    private NoteService noteService;
    
    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private PassageService passageService;

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        
        return userRepository.findById(userId)
                .map(user -> {
                    // 出于安全考虑，不返回密码
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新当前用户信息
     * @param userDetails 用户详情
     * @return 更新后的用户信息
     */
    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(@RequestBody User userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        
        return userRepository.findById(userId)
                .map(user -> {
                    // 只允许更新部分字段
                    if (userDetails.getEmail() != null) {
                        user.setEmail(userDetails.getEmail());
                    }
                    
                    // 如果提供了新密码，则更新密码
                    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }
                    
                    User updatedUser = userRepository.save(user);
                    updatedUser.setPassword(null); // 不返回密码
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取当前用户的统计信息
     * @return 统计信息
     */
    @GetMapping("/me/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Long userId = user.getId();
        
        // 获取学习统计
        Map<String, Object> studyStats = studyRecordService.getUserStudyStatistics(userId);
        
        // 获取笔记数量
        long notesCount = noteService.countByUserId(userId);
        
        // 获取收藏数量
        long favoritesCount = favoriteService.countByUserId(userId);
        
        // 获取学习天数
        long studyDaysCount = studyRecordService.getStudyDaysCount(userId);
        
        // 获取总条文数量
        long totalPassages = passageService.count();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("notesCount", notesCount);
        statistics.put("favoritesCount", favoritesCount);
        statistics.put("studyDays", studyDaysCount);
        statistics.put("totalStudyTime", studyStats.get("totalStudyTime"));
        statistics.put("studiedCount", studyStats.get("studiedCount"));
        statistics.put("totalPassages", totalPassages);
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 注册新用户
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 设置默认角色为USER
        user.setRole("USER");
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userRepository.save(user);
        savedUser.setPassword(null); // 不返回密码
        
        return ResponseEntity.ok(savedUser);
    }

    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@PathVariable String username) {
        boolean isAvailable = !userRepository.findByUsername(username).isPresent();
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        
        return ResponseEntity.ok(response);
    }
}