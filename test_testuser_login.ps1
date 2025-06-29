$headers = @{'Content-Type' = 'application/json'}
$body = @{username = 'testuser'; password = 'test123'} | ConvertTo-Json
Write-Host 'Testing login with testuser and test123...'
Write-Host "Request body: $body"
try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8082/api/auth/login' -Method POST -Headers $headers -Body $body
    Write-Host 'Login successful!'
    Write-Host "Token: $($response.token)"
} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    }
}