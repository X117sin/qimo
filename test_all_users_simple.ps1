# Test all users login and notes API access

Write-Host "Testing qwe user..."
try {
    $loginData = '{"username":"qwe","password":"qwe123"}'
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    $token = $loginResponse.token
    $headers = @{"Authorization" = "Bearer $token"; "Content-Type" = "application/json"}
    $notesResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/notes" -Method GET -Headers $headers
    Write-Host "qwe user: SUCCESS" -ForegroundColor Green
} catch {
    Write-Host "qwe user: FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "Testing testuser..."
try {
    $loginData = '{"username":"testuser","password":"test123"}'
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    $token = $loginResponse.token
    $headers = @{"Authorization" = "Bearer $token"; "Content-Type" = "application/json"}
    $notesResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/notes" -Method GET -Headers $headers
    Write-Host "testuser: SUCCESS" -ForegroundColor Green
} catch {
    Write-Host "testuser: FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "Testing testuser2..."
try {
    $loginData = '{"username":"testuser2","password":"test123"}'
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    $token = $loginResponse.token
    $headers = @{"Authorization" = "Bearer $token"; "Content-Type" = "application/json"}
    $notesResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/notes" -Method GET -Headers $headers
    Write-Host "testuser2: SUCCESS" -ForegroundColor Green
} catch {
    Write-Host "testuser2: FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "All users testing completed." -ForegroundColor Yellow