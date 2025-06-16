Write-Host "Testing JWT token fix..." -ForegroundColor Green
Write-Host ""

# Test login API
Write-Host "1. Testing login API:" -ForegroundColor Yellow
try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"admin123"}'
    Write-Host "Login successful!" -ForegroundColor Green
    Write-Host "Token: $($loginResponse.token.Substring(0,50))..." -ForegroundColor Cyan
    Write-Host "Role: $($loginResponse.role)" -ForegroundColor Cyan
    Write-Host "User Role: $($loginResponse.user.role)" -ForegroundColor Cyan
    
    # Test /api/auth/me endpoint
    Write-Host ""
    Write-Host "2. Testing /api/auth/me endpoint:" -ForegroundColor Yellow
    $headers = @{ "Authorization" = "Bearer $($loginResponse.token)" }
    $meResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/me" -Method GET -Headers $headers
    Write-Host "Me endpoint successful!" -ForegroundColor Green
    Write-Host "Username: $($meResponse.username)" -ForegroundColor Cyan
    Write-Host "Role: $($meResponse.role)" -ForegroundColor Cyan
    
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "Test completed." -ForegroundColor Green