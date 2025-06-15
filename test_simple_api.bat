@echo off
echo Testing simple API endpoint...
curl -X GET http://localhost:8082/api/test/hello -v
echo.
echo Test completed.
pause