# Test login script
$body = @{
    username = 'admin'
    password = 'admin123'
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8082/api/auth/login' -Method POST -Body $body -ContentType 'application/json'
    Write-Host 'Login Success:'
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host 'Login Failed:'
    $_.Exception.Message
}