@echo off
echo === Testing Admin Login Flow ===

echo 1. Check if app is running on port 8082...
netstat -an | findstr :8082

echo.
echo 2. Test simple GET request...
curl http://localhost:8082/login.html -I

echo.
echo 3. Test admin login API...
echo Sending login request...
curl -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" http://localhost:8082/api/auth/login -w "\nHTTP Status: %%{http_code}\n" -s

echo.
echo === Test Complete ===
pause