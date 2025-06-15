@echo off
echo Testing login with curl...
curl -X POST http://localhost:8082/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}" ^
  -v
echo.
echo Test completed.
pause