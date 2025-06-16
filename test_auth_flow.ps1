# Test Authentication Flow

Write-Host "=== Testing Authentication Flow ==="

# Step 1: Test login API
Write-Host "`nStep 1: Test login API"
try {
    $loginBody = '{"username":"admin","password":"admin123"}'
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    
    Write-Host "Login successful!"
    $token = $loginResponse.token
    Write-Host "Token: $($token.Substring(0, 50))..."
    
} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
    exit 1
}

# Step 2: Test a user endpoint (requires authentication but not ADMIN role)
Write-Host "`nStep 2: Test user endpoint with token"
try {
    $headers = @{
        'Authorization' = "Bearer $token"
    }
    
    Write-Host "Sending request to: http://localhost:8082/users/me"
    Write-Host "Authorization header: Bearer $($token.Substring(0, 20))..."
    
    $response = Invoke-WebRequest -Uri "http://localhost:8082/users/me" -Method GET -Headers $headers -UseBasicParsing
    
    Write-Host "Success response: $($response.StatusCode)"
    Write-Host "Response content: $($response.Content)"
    
} catch {
    Write-Host "Request failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status code: $($_.Exception.Response.StatusCode)"
    }
}

# Step 3: Test admin endpoint (requires ADMIN role)
Write-Host "`nStep 3: Test admin endpoint with token"
try {
    $headers = @{
        'Authorization' = "Bearer $token"
    }
    
    Write-Host "Sending request to: http://localhost:8082/api/admin/users"
    Write-Host "Authorization header: Bearer $($token.Substring(0, 20))..."
    
    $response = Invoke-WebRequest -Uri "http://localhost:8082/api/admin/users" -Method GET -Headers $headers -UseBasicParsing
    
    Write-Host "Success response: $($response.StatusCode)"
    Write-Host "Response content: $($response.Content)"
    
} catch {
    Write-Host "Request failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status code: $($_.Exception.Response.StatusCode)"
    }
}

Write-Host "`n=== Test completed ==="