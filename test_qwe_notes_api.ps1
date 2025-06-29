# 测试qwe用户登录并访问笔记API
$loginData = @{
    username = "qwe"
    password = "qwe123"
} | ConvertTo-Json

Write-Host "=== 步骤1: 登录获取JWT令牌 ==="
try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    Write-Host "登录成功!"
    $token = $loginResponse.token
    Write-Host "获得JWT令牌: $($token.Substring(0, 50))..."
    
    Write-Host ""
    Write-Host "=== 步骤2: 使用JWT令牌访问笔记API ==="
    
    # 设置Authorization头
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    # 访问笔记API
    $notesResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/notes" -Method GET -Headers $headers
    Write-Host "笔记API访问成功!"
    Write-Host "笔记数据: $($notesResponse | ConvertTo-Json -Depth 3)"
    
} catch {
    Write-Host "操作失败!"
    Write-Host "错误信息: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "响应状态码: $($_.Exception.Response.StatusCode)"
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "响应内容: $responseBody"
    }
}