@echo off
echo === 测试管理员登录和角色验证 ===

echo 1. 测试管理员登录...
curl -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" http://localhost:8082/api/auth/login > login_result.json

echo.
echo 2. 显示登录结果:
type login_result.json

echo.
echo === 测试完成 ===
pause