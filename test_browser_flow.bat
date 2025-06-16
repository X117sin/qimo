@echo off
echo Testing browser flow simulation...
echo.

echo === Step 1: Login and get token ===
curl -X POST "http://localhost:8082/api/auth/login" ^
     -H "Content-Type: application/json" ^
     -d "{\"username\":\"admin\",\"password\":\"admin123\"}" ^
     -o login_response.json

echo.
echo Login response saved to login_response.json
type login_response.json
echo.

echo === Step 2: Extract token and test /api/auth/me ===
for /f "tokens=2 delims=:\"" %%a in ('findstr "token" login_response.json') do set TOKEN=%%a
echo Token extracted: %TOKEN%
echo.

curl -X GET "http://localhost:8082/api/auth/me" ^
     -H "Authorization: Bearer %TOKEN%" ^
     -H "Content-Type: application/json"

echo.
echo === Step 3: Test admin dashboard page access ===
curl -X GET "http://localhost:8082/admin-dashboard.html" -I

echo.
echo === Test completed ===
pause