USE tcm_notes;
UPDATE users SET password = '$2a$10$ABLiGw0Axuvwbfs.J7tlG.X/50xJnD.XZxzVCeXljcTy0W3SejBES' WHERE username = 'admin';
SELECT username, password, role FROM users WHERE username = 'admin';