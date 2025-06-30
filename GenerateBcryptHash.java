import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBcryptHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "test123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        
        // 验证哈希值
        boolean matches = encoder.matches(password, hash);
        System.out.println("Hash matches password: " + matches);
        
        // 测试现有的哈希值
        String existingHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        boolean existingMatches = encoder.matches(password, existingHash);
        System.out.println("Existing hash matches password: " + existingMatches);
    }
}