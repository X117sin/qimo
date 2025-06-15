-- 检查admin用户的角色
SELECT id, username, role FROM users WHERE username = 'admin';

-- 如果admin用户不存在，创建一个
INSERT IGNORE INTO users (username, password, role) 
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN');

-- 确保admin用户的角色是ADMIN
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';

-- 再次检查
SELECT id, username, role FROM users WHERE username = 'admin';