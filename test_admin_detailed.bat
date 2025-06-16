@echo off
echo === 详细测试管理员登录流程 ===

echo 1. 检查应用是否运行在8082端口...
netstat -an | findstr :8082

echo.
echo 2. 测试简单的GET请求...
curl http://localhost:8082/login.html -I

echo.
echo 3. 测试管理员登录API...
echo 正在发送登录请求...
curl -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" http://localhost:8082/api/auth/login -w "\nHTTP状态码: %%{http_code}\n" -s

echo.
echo === 测试完成 ===
pause