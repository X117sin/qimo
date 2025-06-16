# 测试注册API
$uri = "http://127.0.0.1:8082/api/auth/register"
$headers = @{
    'Content-Type' = 'application/json'
}
$body = @{
    username = "testuser"
    password = "test123"
    email = "test@example.com"
} | ConvertTo-Json

Write-Host "Testing register API at: $uri"
Write-Host "Request body: $body"

try {
    $response = Invoke-WebRequest -Uri $uri -Method POST -Headers $headers -Body $body
    Write-Host "SUCCESS - Status Code: $($response.StatusCode)"
    Write-Host "Response: $($response.Content)"
} catch {
    Write-Host "ERROR: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "Response Body: $responseBody"
        } catch {
            Write-Host "Could not read response body"
        }
    }
}