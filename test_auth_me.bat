@echo off
echo Testing /api/auth/me endpoint...

REM First, get the JWT token
echo Getting JWT token...
for /f "delims=" %%i in ('curl -s -X POST http://localhost:8082/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}"') do set LOGIN_RESPONSE=%%i

echo Login response: %LOGIN_RESPONSE%

REM Extract token (simplified - using the token from previous test)
set TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NDk5ODQ2MTgsImV4cCI6MTc1NTE2ODYxOH0.jRxPBzpPPVMpWSiHekzT99FKRZ70B4rw1KCWOqSwDWk

echo.
echo Testing /api/auth/me with JWT token...
curl -X GET http://localhost:8082/api/auth/me ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -v

echo.
echo Test completed.
pause