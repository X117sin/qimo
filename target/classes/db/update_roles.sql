-- 更新所有用户的role字段为大写
UPDATE users SET role = UPPER(role);