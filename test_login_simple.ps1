# 测试登录API
$uri = "http://127.0.0.1:8082/api/auth/login"
$headers = @{
    'Content-Type' = 'application/json'
}

# 测试testuser用户登录
$body = @{
    username = "testuser"
    password = "test123"
} | ConvertTo-Json

Write-Host "Testing login API at: $uri"
Write-Host "Request body: $body"

try {
    $response = Invoke-WebRequest -Uri $uri -Method POST -Headers $headers -Body $body
    Write-Host "SUCCESS - Status Code: $($response.StatusCode)"
    Write-Host "Response: $($response.Content)"
}
catch {
    Write-Host "ERROR: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody"
    }
}

# 测试其他密码
Write-Host "`n--- Testing with password 'admin' ---"
$body2 = @{
    username = "admin"
    password = "admin"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $uri -Method POST -Headers $headers -Body $body2
    Write-Host "SUCCESS - Status Code: $($response.StatusCode)"
    Write-Host "Response: $($response.Content)"
}
catch {
    Write-Host "ERROR: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody"
    }
}