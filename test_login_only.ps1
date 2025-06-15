$uri = "http://localhost:8082/api/auth/login"
$headers = @{
    "Content-Type" = "application/json"
}
$body = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Write-Host "Testing login..."
Write-Host "URI: $uri"
Write-Host "Body: $body"

try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -Headers $headers -Body $body
    Write-Host "Login successful!"
    Write-Host "Token (first 50 chars): $($response.token.Substring(0, 50))..."
    Write-Host "User: $($response.user.username)"
    Write-Host "Role: $($response.user.role)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)"
    }
}