package com.tcm.notes.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = args.length > 0 ? args[0] : "test123";
        String encoded = encoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("BCrypt: " + encoded);
        
        // Test verification
        boolean matches = encoder.matches(password, encoded);
        System.out.println("Verification: " + matches);
    }
}