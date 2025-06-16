@echo off
echo Testing simple GET request to /api/auth/login...
curl -X GET http://localhost:8082/api/auth/login -v
echo.
echo Test completed.
pause