import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate BCrypt hashes for seeding users in Flyway migrations.
 * Usage: java HashGen [plainTextPassword]
 */
public class HashGen {
    public static void main(String[] args) {
        String password = args.length > 0 ? args[0] : "admin123";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        System.out.println(encoder.encode(password));
    }
}