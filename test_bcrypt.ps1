# 测试BCrypt密码验证
$uri = "http://localhost:8082/api/auth/login"
$headers = @{
    "Content-Type" = "application/json"
    "Accept" = "application/json"
}

# 尝试不同的密码
$passwords = @("admin123", "password", "admin", "123456", "111", "222", "333", "111@222.com", "tcm", "notes")

foreach ($password in $passwords) {
    $body = @{
        username = "admin"
        password = $password
    } | ConvertTo-Json
    
    Write-Host "Testing password: $password"
    
    try {
        $response = Invoke-RestMethod -Uri $uri -Method POST -Body $body -Headers $headers
        Write-Host "SUCCESS with password: $password" -ForegroundColor Green
        Write-Host "Token: $($response.token.Substring(0, 50))..." -ForegroundColor Green
        break
    }
    catch {
        Write-Host "FAILED with password: $password - $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "Test completed."