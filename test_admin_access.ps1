# 测试管理员仪表板访问问题
Write-Host "=== 测试管理员仪表板访问 ===" -ForegroundColor Green

# 1. 测试登录API
Write-Host "\n1. 测试管理员登录..." -ForegroundColor Yellow
$loginData = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    Write-Host "登录成功!" -ForegroundColor Green
    Write-Host "用户信息: $($loginResponse | ConvertTo-Json)" -ForegroundColor Cyan
    
    $token = $loginResponse.token
    Write-Host "Token: $token" -ForegroundColor Cyan
    
    # 2. 测试 /api/auth/me 端点
    Write-Host "\n2. 测试用户信息获取..." -ForegroundColor Yellow
    $headers = @{
        "Authorization" = "Bearer $token"
    }
    
    $userInfo = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/me" -Method GET -Headers $headers
    Write-Host "用户信息获取成功!" -ForegroundColor Green
    Write-Host "用户详情: $($userInfo | ConvertTo-Json)" -ForegroundColor Cyan
    
    # 3. 测试管理员统计API
    Write-Host "\n3. 测试管理员统计API..." -ForegroundColor Yellow
    try {
        $statsResponse = Invoke-RestMethod -Uri "http://localhost:8082/admin/statistics" -Method GET -Headers $headers
        Write-Host "统计数据获取成功!" -ForegroundColor Green
        Write-Host "统计数据: $($statsResponse | ConvertTo-Json)" -ForegroundColor Cyan
    } catch {
        Write-Host "统计数据获取失败: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "响应状态: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
    
    # 4. 测试用户列表API
    Write-Host "\n4. 测试用户列表API..." -ForegroundColor Yellow
    try {
        $usersResponse = Invoke-RestMethod -Uri "http://localhost:8082/admin/users?page=1&size=10" -Method GET -Headers $headers
        Write-Host "用户列表获取成功!" -ForegroundColor Green
        Write-Host "用户数量: $($usersResponse.content.Count)" -ForegroundColor Cyan
    } catch {
        Write-Host "用户列表获取失败: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "响应状态: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "登录失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "响应状态: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host "\n=== 测试完成 ===" -ForegroundColor Green