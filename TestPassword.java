import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class TestPassword {
    public static void main(String[] args) {
        // Database password hash
        String dbHash = "$2a$10$nVkCX83ckgXbVhEWbK86.efy7pBMpj0A8sN5.A4XWsnmctKmiwC7W";
        
        // Test password list
        String[] passwords = {"admin123", "admin", "password", "123456", "111", "222", "333"};
        
        System.out.println("Database password hash: " + dbHash);
        System.out.println("This is a BCrypt hash, need BCrypt library to verify");
        System.out.println("Suggested passwords to try:");
        
        for (String pwd : passwords) {
            System.out.println("- " + pwd);
        }
        
        System.out.println("\nPlease manually test these passwords or check application logs for authentication errors.");
    }
}