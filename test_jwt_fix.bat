@echo off
echo Testing JWT token fix...
echo.

echo 1. Testing login API:
curl -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" http://localhost:8082/api/auth/login
echo.
echo.

echo 2. Testing /api/auth/me endpoint:
for /f "tokens=*" %%i in ('curl -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" http://localhost:8082/api/auth/login ^| findstr /r "\"token\""') do set TOKEN_LINE=%%i
for /f "tokens=2 delims=:" %%j in ("%TOKEN_LINE%") do set TOKEN=%%j
set TOKEN=%TOKEN:"=%
set TOKEN=%TOKEN:,=%
set TOKEN=%TOKEN: =%

curl -X GET -H "Authorization: Bearer %TOKEN%" http://localhost:8082/api/auth/me
echo.
echo.

echo Test completed.
pause