UPDATE users SET password = '$2a$10$0fZCfW7MgoRkLgYcw8BDNuoEqFiemOiH7T9JpZSVLZ6xu2F7DL69e' WHERE username = 'testuser2';
SELECT username, password FROM users WHERE username = 'testuser2';