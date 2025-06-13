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
        String dbPassword = "$2a$10$N.zmdr9k7uOsaLQJeuTtVOvoSz.AeMKoQ4iwzSGfzr5f5PJQKqKvS";
        boolean dbMatches = encoder.matches(rawPassword, dbPassword);
        System.out.println("Database password match: " + dbMatches);
    }
}