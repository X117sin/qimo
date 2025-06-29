$headers = @{'Content-Type' = 'application/json'}
$body = @{username = 'qwe'; password = 'qwe123'} | ConvertTo-Json
Write-Host 'Testing login with qwe and qwe123...'
Write-Host "Request body: $body"
try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8082/api/auth/login' -Method POST -Headers $headers -Body $body
    Write-Host 'Login successful!'
    Write-Host "Token: $($response.token)"
    
    # Test notes API with the token
    $noteHeaders = @{'Authorization' = "Bearer $($response.token)"; 'Content-Type' = 'application/json'}
    Write-Host 'Testing notes API with JWT token...'
    $notesResponse = Invoke-RestMethod -Uri 'http://localhost:8082/api/notes' -Method GET -Headers $noteHeaders
    Write-Host 'Notes API successful!'
    Write-Host "Notes response: $($notesResponse | ConvertTo-Json)"
} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    }
}