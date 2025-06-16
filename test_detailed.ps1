# 详细测试登录功能
$uri = "http://localhost:8082/api/auth/login"
$headers = @{
    "Content-Type" = "application/json"
    "Accept" = "application/json"
}

# 测试密码
$password = "111"
$body = @{
    username = "admin"
    password = $password
} | ConvertTo-Json

Write-Host "Testing login with password: $password" -ForegroundColor Yellow
Write-Host "Request URL: $uri" -ForegroundColor Cyan
Write-Host "Request Body: $body" -ForegroundColor Cyan
Write-Host "Request Headers: $($headers | ConvertTo-Json)" -ForegroundColor Cyan

try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $body -Headers $headers -Verbose
    Write-Host "SUCCESS!" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json)" -ForegroundColor Green
}
catch {
    Write-Host "FAILED!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "Status Description: $($_.Exception.Response.StatusDescription)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody" -ForegroundColor Red
    }
}

Write-Host "Test completed." -ForegroundColor Yellow