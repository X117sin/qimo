$token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI3Iiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3NTExNjY3ODMsImV4cCI6MTc1NjM1MDc4M30.8P-Nhbtb4ehzROytmfmqGeaBWrW57OVcyjr9gpT25ig'
$headers = @{'Authorization' = "Bearer $token"; 'Content-Type' = 'application/json'}
Write-Host 'Testing notes API with JWT token...'
try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8082/api/notes' -Method GET -Headers $headers
    Write-Host 'Notes API successful!'
    Write-Host "Response: $($response | ConvertTo-Json)"
} catch {
    Write-Host "Notes API failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    }
}