package com.tcm.notes.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordMatch {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "test123";
        String hashedPassword = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Hashed password: " + hashedPassword);
        System.out.println("Password matches: " + encoder.matches(rawPassword, hashedPassword));
        
        // 生成一个新的哈希来测试
        String newHash = encoder.encode(rawPassword);
        System.out.println("New hash: " + newHash);
        System.out.println("New hash matches: " + encoder.matches(rawPassword, newHash));
    }
}