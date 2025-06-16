# Test study record functionality

# 1. Test login
Write-Host "=== Testing Login ===" -ForegroundColor Cyan
$loginBody = '{"username":"admin","password":"admin123"}'

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Headers @{"Content-Type"="application/json"} -Body $loginBody
    
    if ($loginResponse.token) {
        Write-Host "Login successful!" -ForegroundColor Green
        Write-Host "Token received" -ForegroundColor Yellow
        $token = $loginResponse.token
    } else {
        Write-Host "Login failed: No token received" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Login request failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Test study record update
Write-Host "\n=== Testing Study Record Update ===" -ForegroundColor Cyan
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $updateResponse = Invoke-RestMethod -Uri "http://localhost:8082/study-records/update/1" -Method POST -Headers $headers
    
    Write-Host "Study record update successful!" -ForegroundColor Green
    Write-Host "Response data:" -ForegroundColor Yellow
    $updateResponse | ConvertTo-Json -Depth 3
    
} catch {
    Write-Host "Study record update failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "HTTP Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "Status Description: $($_.Exception.Response.StatusDescription)" -ForegroundColor Red
    }
}

# 3. Test study statistics
Write-Host "\n=== Testing Study Statistics ===" -ForegroundColor Cyan
try {
    $statisticsResponse = Invoke-RestMethod -Uri "http://localhost:8082/study-records/statistics" -Method GET -Headers $headers
    
    Write-Host "Study statistics retrieval successful!" -ForegroundColor Green
    Write-Host "Statistics data:" -ForegroundColor Yellow
    $statisticsResponse | ConvertTo-Json -Depth 3
    
} catch {
    Write-Host "Study statistics retrieval failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "HTTP Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "Status Description: $($_.Exception.Response.StatusDescription)" -ForegroundColor Red
    }
}

Write-Host "\n=== Test Complete ===" -ForegroundColor Cyan