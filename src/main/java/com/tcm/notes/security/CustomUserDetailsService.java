package com.tcm.notes.security;

import com.tcm.notes.entity.User;
import com.tcm.notes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义用户详情服务，实现Spring Security的UserDetailsService接口
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名或用户ID加载用户
     * @param username 用户名或用户ID
     * @return UserDetails对象
     * @throws UsernameNotFoundException 用户名未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("正在加载用户: " + username);
        
        User user = null;
        
        // 判断传入的是用户ID还是用户名
        try {
            // 尝试将username解析为Long类型的ID
            Long userId = Long.parseLong(username);
            System.out.println("检测到用户ID: " + userId + "，按ID查找用户");
            user = userRepository.findById(userId).orElse(null);
        } catch (NumberFormatException e) {
            // 如果解析失败，说明是用户名
            System.out.println("检测到用户名: " + username + "，按用户名查找用户");
            user = userRepository.findByUsername(username).orElse(null);
        }
        
        if (user == null) {
            System.out.println("用户不存在: " + username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        System.out.println("成功加载用户: " + username + ", 角色: " + user.getRole());
        
        // 确保角色名称正确（必须是全大写的"ADMIN"）
        if (user.getRole() != null) {
            if (!user.getRole().equals(user.getRole().toUpperCase())) {
                String originalRole = user.getRole();
                String upperCaseRole = user.getRole().toUpperCase();
                System.out.println("警告: 用户角色'" + originalRole + "'不是大写，自动转换为'" + upperCaseRole + "'");
                user.setRole(upperCaseRole);
                userRepository.save(user);
                System.out.println("已将用户角色从'" + originalRole + "'更新为'" + upperCaseRole + "'");
            }
        } else {
            System.out.println("错误: 用户" + username + "的角色为null");
        }
        
        // 直接使用数据库中的角色，不添加ROLE_前缀
        String role = user.getRole();
        System.out.println("授予权限: " + role);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}