# 测试管理员数据API脚本

# 1. 登录获取token
Write-Host "=== 步骤1: 管理员登录 ===" -ForegroundColor Green
$loginData = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    Write-Host "登录成功!" -ForegroundColor Green
    Write-Host "Token: $($loginResponse.token.Substring(0, 20))..." -ForegroundColor Yellow
    Write-Host "用户信息: $($loginResponse.user | ConvertTo-Json)" -ForegroundColor Cyan
    
    $token = $loginResponse.token
    $headers = @{
        'Authorization' = "Bearer $token"
        'Content-Type' = 'application/json'
    }
    
    # 2. 测试用户列表API
    Write-Host "`n=== 步骤2: 测试用户列表API ===" -ForegroundColor Green
    try {
        $usersResponse = Invoke-RestMethod -Uri "http://localhost:8082/admin/users" -Method GET -Headers $headers
        Write-Host "用户列表API调用成功!" -ForegroundColor Green
        Write-Host "用户数量: $($usersResponse.totalElements)" -ForegroundColor Yellow
        Write-Host "用户列表: $($usersResponse.content | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    } catch {
        Write-Host "用户列表API调用失败: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
    
    # 3. 测试条文列表API
    Write-Host "`n=== 步骤3: 测试条文列表API ===" -ForegroundColor Green
    try {
        $passagesResponse = Invoke-RestMethod -Uri "http://localhost:8082/admin/passages" -Method GET -Headers $headers
        Write-Host "条文列表API调用成功!" -ForegroundColor Green
        Write-Host "条文数量: $($passagesResponse.totalElements)" -ForegroundColor Yellow
        Write-Host "条文列表: $($passagesResponse.content | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    } catch {
        Write-Host "条文列表API调用失败: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
    
    # 4. 测试统计数据API
    Write-Host "`n=== 步骤4: 测试统计数据API ===" -ForegroundColor Green
    try {
        $statsResponse = Invoke-RestMethod -Uri "http://localhost:8082/admin/statistics" -Method GET -Headers $headers
        Write-Host "统计数据API调用成功!" -ForegroundColor Green
        Write-Host "统计数据: $($statsResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    } catch {
        Write-Host "统计数据API调用失败: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "登录失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green