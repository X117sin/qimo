package com.tcm.notes.controller;

import com.tcm.notes.entity.Passage;
import com.tcm.notes.entity.User;
import com.tcm.notes.repository.PassageRepository;
import com.tcm.notes.repository.UserRepository;
import com.tcm.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员后台控制器
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    /**
     * 检查管理员权限
     * 此端点仅供管理员访问，用于前端验证用户是否具有管理员权限
     * @return 成功响应
     */
    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAdminAuth() {
        return ResponseEntity.ok().body("管理员权限验证成功");
    }

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PassageRepository passageRepository;
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取所有用户列表
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userRepository.findAll(pageable));
    }

    /**
     * 获取指定用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param userDetails 用户详情
     * @return 更新后的用户信息
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setUsername(userDetails.getUsername());
                    user.setEmail(userDetails.getEmail());
                    user.setRole(userDetails.getRole());
                    
                    // 如果提供了新密码，则更新密码
                    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }
                    
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    Map<String, Boolean> response = new HashMap<>();
                    response.put("deleted", true);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取所有条文列表
     * @param pageable 分页参数
     * @return 条文分页结果
     */
    @GetMapping("/passages")
    public ResponseEntity<Page<Passage>> getAllPassages(Pageable pageable) {
        return ResponseEntity.ok(passageRepository.findAll(pageable));
    }

    /**
     * 获取指定条文信息
     * @param passageId 条文ID
     * @return 条文信息
     */
    @GetMapping("/passages/{passageId}")
    public ResponseEntity<Passage> getPassageById(@PathVariable Long passageId) {
        return passageRepository.findById(passageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新条文信息
     * @param passageId 条文ID
     * @param passageDetails 条文详情
     * @return 更新后的条文信息
     */
    @PutMapping("/passages/{passageId}")
    public ResponseEntity<Passage> updatePassage(@PathVariable Long passageId, @RequestBody Passage passageDetails) {
        return passageRepository.findById(passageId)
                .map(passage -> {
                    passage.setTitle(passageDetails.getTitle());
                    passage.setContent(passageDetails.getContent());
                    passage.setCategory(passageDetails.getCategory());
                    // 如果Passage类没有tags属性,需要先在Passage实体类中添加相应的字段和方法
                    // passage.setTags(passageDetails.getTags());
                    return ResponseEntity.ok(passageRepository.save(passage));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除条文
     * @param passageId 条文ID
     * @return 删除结果
     */
    @DeleteMapping("/passages/{passageId}")
    public ResponseEntity<Map<String, Boolean>> deletePassage(@PathVariable Long passageId) {
        return passageRepository.findById(passageId)
                .map(passage -> {
                    passageRepository.delete(passage);
                    Map<String, Boolean> response = new HashMap<>();
                    response.put("deleted", true);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
 * 创建新用户
 * @param user 用户信息
 * @return 创建的用户
 */
@PostMapping("/users")
@PreAuthorize("permitAll()")
public ResponseEntity<User> createUser(@RequestBody User user) {
    // 检查用户名是否已存在
    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
        return ResponseEntity.badRequest().build();
    }
    
    // 加密密码
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    
    User savedUser = userRepository.save(user);
    return ResponseEntity.ok(savedUser);
}

    /**
     * 创建新条文
     * @param passage 条文信息
     * @return 创建的条文
     */
    @PostMapping("/passages")
    public ResponseEntity<Passage> createPassage(@RequestBody Passage passage) {
        Passage savedPassage = passageRepository.save(passage);
        return ResponseEntity.ok(savedPassage);
    }

    /**
     * 获取系统统计信息
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSystemStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 用户总数
        long userCount = userRepository.count();
        statistics.put("totalUsers", userCount);
        
        // 条文总数
        long passageCount = passageRepository.count();
        statistics.put("totalPassages", passageCount);
        
        // 笔记总数
        long noteCount = noteRepository.count();
        statistics.put("totalNotes", noteCount);
        
        // 错误总数 (暂时设为0)
        statistics.put("totalErrors", 0);
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 检查是否存在管理员账号
     * @return 是否存在管理员账号
     */
    @GetMapping("/check-admin")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> checkAdminExists() {
        Map<String, Object> result = new HashMap<>();
        
        // 查询是否存在ADMIN角色的用户
        java.util.List<User> adminUsers = userRepository.findByRole("ADMIN");
        boolean adminExists = !adminUsers.isEmpty();
        
        result.put("adminExists", adminExists);
        
        // 如果存在管理员，返回管理员数量
        if (adminExists) {
            result.put("adminCount", adminUsers.size());
        }
        
        return ResponseEntity.ok(result);
    }
}