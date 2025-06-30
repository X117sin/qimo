# 测试testuser用户登录和笔记API访问
$loginData = @{
    username = "testuser"
    password = "test123"
} | ConvertTo-Json

Write-Host "=== 步骤1: testuser登录获取JWT令牌 ==="
try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    Write-Host "testuser登录成功!"
    $token = $loginResponse.token
    Write-Host "获得JWT令牌: $($token.Substring(0, 50))..."
    
    Write-Host ""
    Write-Host "=== 步骤2: testuser使用JWT令牌访问笔记API ==="
    
    # 设置Authorization头
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    # 访问笔记API
    $notesResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/notes" -Method GET -Headers $headers
    Write-Host "testuser笔记API访问成功!"
    Write-Host "笔记数据: $($notesResponse | ConvertTo-Json -Depth 3)"
    
} catch {
    Write-Host "testuser操作失败!"
    Write-Host "错误信息: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "状态码: $statusCode"
        
        try {
            $errorStream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($errorStream)
            $errorBody = $reader.ReadToEnd()
            Write-Host "响应内容: $errorBody"
        } catch {
            Write-Host "无法读取错误响应内容"
        }
    }
}