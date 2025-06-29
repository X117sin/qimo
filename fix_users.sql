-- Update existing users with correct password hash for 'test123'
UPDATE users SET password = '$2a$10$dLKbqNx5LoRNZsryZlSYhuEKQy9ZlEdLcXV6A8s1iP43mqcInOgSi' WHERE username = 'testuser';

-- Verify the update
SELECT id, username, email, password FROM users WHERE username = 'testuser';