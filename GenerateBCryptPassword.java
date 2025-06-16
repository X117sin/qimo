import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCryptPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("Raw password: " + rawPassword);
        System.out.println("BCrypt encoded: " + encodedPassword);
        
        // Verify password match
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Password verification: " + matches);
        
        // Test match with existing database password
        String dbPassword = "$2a$10$nVkCX83ckgXbVhEWbK86.efy7pBMpj0A8sN5.A4XWsnmctKmiwC7W";
        boolean dbMatches = encoder.matches(rawPassword, dbPassword);
        System.out.println("Database password match: " + dbMatches);
        
        // Test other common passwords
        String[] testPasswords = {"admin123", "admin", "password", "123456", "111"};
        for (String testPwd : testPasswords) {
            boolean testMatch = encoder.matches(testPwd, dbPassword);
            System.out.println("Password '" + testPwd + "' matches: " + testMatch);
        }
    }
}