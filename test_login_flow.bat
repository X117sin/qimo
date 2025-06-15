@echo off
echo Testing admin login flow...
echo.

echo Step 1: Login as admin
curl -X POST http://localhost:8082/api/auth/login ^-H "Content-Type: application/json" ^-d "{\"username\":\"admin\",\"password\":\"admin123\"}" ^-c cookies.txt ^-v

echo.
echo Step 2: Test /api/auth/me endpoint
curl -X GET http://localhost:8082/api/auth/me ^-b cookies.txt ^-v

echo.
echo Step 3: Access admin dashboard page
curl -X GET http://localhost:8082/admin-dashboard.html ^-b cookies.txt ^-v

echo.
echo Test completed.
pause