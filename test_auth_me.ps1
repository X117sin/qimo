# Test /api/auth/me endpoint
Write-Host "Testing /api/auth/me endpoint..."

# First get a fresh token
$loginBody = '{"username":"admin","password":"admin123"}'
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"

Write-Host "Login successful, token received"
Write-Host "User role: $($loginResponse.role)"

# Test /api/auth/me with the token
$headers = @{
    "Authorization" = "Bearer $($loginResponse.token)"
    "Content-Type" = "application/json"
}

try {
    $meResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/me" -Method Get -Headers $headers
    Write-Host "\n/api/auth/me response:"
    Write-Host "Username: $($meResponse.username)"
    Write-Host "Role: $($meResponse.role)"
    Write-Host "Email: $($meResponse.email)"
    Write-Host "ID: $($meResponse.id)"
} catch {
    Write-Host "Error calling /api/auth/me: $($_.Exception.Message)"
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
}

Write-Host "\nTest completed."
Read-Host "Press Enter to continue"