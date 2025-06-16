# Simple API Test

Write-Host "=== Simple API Test ==="

# Test login first
Write-Host "`nTesting login..."
try {
    $loginBody = '{"username":"admin","password":"admin123"}'
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    
    Write-Host "Login successful!"
    $token = $loginResponse.token
    Write-Host "Token received: $($token.Substring(0, 30))..."
    
    # Test admin users endpoint
    Write-Host "`nTesting admin users endpoint..."
    $headers = @{
        'Authorization' = "Bearer $token"
        'Content-Type' = 'application/json'
    }
    
    $adminResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/admin/users" -Method GET -Headers $headers
    Write-Host "Admin endpoint success! Users count: $($adminResponse.totalElements)"
    
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)"
        Write-Host "Response: $($_.Exception.Response | ConvertTo-Json)"
    }
}

Write-Host "`n=== Test completed ==="