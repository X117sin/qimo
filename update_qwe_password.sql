-- 更新qwe用户的密码为qwe123对应的BCrypt哈希
UPDATE users SET password = '$2a$10$4RuU2jkvA0ZOstAtO7AA3eNGiKTjWHlXyu7oPq4cJhQaopzsb47Mu' WHERE username = 'qwe';

-- 查询验证更新结果
SELECT id, username, password, role FROM users WHERE username = 'qwe';