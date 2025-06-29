import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = args.length > 0 ? args[0] : "test123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        
        // Test verification
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: " + matches);
        
        // Test with the existing hash
        String existingHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        boolean matchesExisting = encoder.matches(password, existingHash);
        System.out.println("Matches existing hash: " + matchesExisting);
    }
}