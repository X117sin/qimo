USE tcm_notes;
-- Update admin user password with correct BCrypt hash for 'admin123'
UPDATE users SET password = '$2a$10$ABLiGw0Axuvwbfs.J7tlG.X/50xJnD.XZxzVCeXljcTy0W3SejBES' WHERE username = 'admin';
-- Verify the update
SELECT username, password, role FROM users WHERE username = 'admin';