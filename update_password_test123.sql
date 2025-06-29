-- Update password to a known BCrypt hash for 'test123'
-- This hash is generated with BCrypt for password 'test123'
UPDATE users SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' WHERE username IN ('testuser', 'testuser2');
SELECT username, password FROM users WHERE username IN ('testuser', 'testuser2');