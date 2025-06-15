# Simple login test
$body = '{"username":"admin","password":"admin123"}'
$headers = @{"Content-Type"="application/json"}

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Headers $headers -Body $body
    Write-Host "Login successful!"
    Write-Host "Token: $($response.token)"
    
    # Test study record update with token
    $authHeaders = @{
        "Authorization" = "Bearer $($response.token)"
        "Content-Type" = "application/json"
    }
    
    Write-Host "Testing study record update..."
    $updateResponse = Invoke-RestMethod -Uri "http://localhost:8082/study-records/update/1" -Method POST -Headers $authHeaders
    Write-Host "Study record update successful!"
    $updateResponse | ConvertTo-Json
    
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)"
    }
}