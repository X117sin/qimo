-- Generate a new BCrypt hash for password 'test123'
-- We'll use a simple approach: create a user with known password and copy the hash
INSERT INTO users (username, password, role) VALUES ('tempuser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER');
SELECT id, username, password FROM users WHERE username = 'tempuser';