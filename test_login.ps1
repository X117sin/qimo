$headers = @{
    'Content-Type' = 'application/json'
}

$body = @{
    username = 'testuser'
    password = 'admin123'
} | ConvertTo-Json

Write-Host "Testing login with testuser and admin123..."
Write-Host "Request body: $body"

try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method POST -Headers $headers -Body $body
    Write-Host "Login successful!"
    Write-Host "Response: $($response | ConvertTo-Json)"
} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody"
    }
}