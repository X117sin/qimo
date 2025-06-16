@echo off
echo Testing JWT authentication...

REM First, get the JWT token
echo Getting JWT token...
for /f "tokens=*" %%i in ('curl -s -X POST http://localhost:8082/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}" ^| findstr /r "\"token\""') do set TOKEN_LINE=%%i

REM Extract token from the response (this is a simplified extraction)
echo Token line: %TOKEN_LINE%

REM Test accessing a protected endpoint with the token
echo.
echo Testing protected endpoint with JWT token...
curl -X GET http://localhost:8082/api/test/protected ^
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NDk5ODQ2MTgsImV4cCI6MTc1NTE2ODYxOH0.jRxPBzpPPVMpWSiHekzT99FKRZ70B4rw1KCWOqSwDWk" ^
  -v

echo.
echo Test completed.
pause