# 测试登录API
$uri = "http://127.0.0.1:8082/api/auth/login"
$headers = @{
    'Content-Type' = 'application/json'
}
$body = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Write-Host "Testing login API at: $uri"
Write-Host "Request body: $body"

try {
    $response = Invoke-WebRequest -Uri $uri -Method POST -Headers $headers -Body $body
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Response: $($response.Content)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody"
    }
}

# 也测试一下简单的GET请求
Write-Host "`nTesting simple GET request..."
try {
    $getResponse = Invoke-WebRequest -Uri "http://127.0.0.1:8082/api/public/info" -Method GET
    Write-Host "GET Status Code: $($getResponse.StatusCode)"
    Write-Host "GET Response: $($getResponse.Content)"
} catch {
    Write-Host "GET Error: $($_.Exception.Message)"
}