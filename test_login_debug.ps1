# 测试登录API
try {
    Write-Host "正在测试登录API..."
    
    $body = '{"username":"admin","password":"admin123"}'
    Write-Host "请求体: $body"
    
    $response = Invoke-WebRequest -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing
    
    Write-Host "成功响应:"
    Write-Host "状态码: $($response.StatusCode)"
    Write-Host "响应体: $($response.Content)"
    
} catch {
    Write-Host "错误信息:"
    Write-Host "状态码: $($_.Exception.Response.StatusCode)"
    Write-Host "错误消息: $($_.Exception.Message)"
}