@echo off
echo Testing protected API endpoint...

REM Test accessing a protected endpoint without token
echo.
echo Testing without JWT token (should get 401)...
curl -X GET http://localhost:8082/api/test/hello ^
  -H "Content-Type: application/json" ^
  -v

echo.
echo.
echo Testing with JWT token...
curl -X GET http://localhost:8082/api/test/hello ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NDk5ODQ2MTgsImV4cCI6MTc1NTE2ODYxOH0.jRxPBzpPPVMpWSiHekzT99FKRZ70B4rw1KCWOqSwDWk" ^
  -v

echo.
echo Test completed.
pause