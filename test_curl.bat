@echo off
curl -X POST ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}" ^
  http://localhost:8082/api/auth/login ^
  -v
pause