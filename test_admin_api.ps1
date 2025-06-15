# 测试管理员API访问
$token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlJPTEVfQURNSU4iLCJvcmlnaW5hbFJvbGUiOiJBRE1JTiIsImlhdCI6MTc0OTk5NzM4NSwiZXhwIjoxNzU1MTgxMzg1fQ.zqA_9ywJIcsHD34VDW-m3cYFVc2E_NRDH8x01Fxr7iI"
$headers = @{
    'Authorization' = "Bearer $token"
    'Content-Type' = 'application/json'
}

Write-Host "=== 测试管理员API访问 ==="
Write-Host "Token: $($token.Substring(0, 50))..."

try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8082/api/admin/users' -Method GET -Headers $headers
    Write-Host "成功! 状态码: $($response.StatusCode)"
    Write-Host "响应内容: $($response.Content)"
} catch {
    Write-Host "错误: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "状态码: $($_.Exception.Response.StatusCode)"
        Write-Host "状态描述: $($_.Exception.Response.StatusDescription)"
    }
}

Write-Host "\n=== 测试完成 ==="
Read-Host "按任意键继续"