# 测试管理员登录和后台访问
$baseUrl = 'http://localhost:8082'
Write-Host '=== 测试管理员登录和后台访问 ===' -ForegroundColor Green

# 1. 测试管理员登录
Write-Host '1. 尝试管理员登录...' -ForegroundColor Yellow
try {
    $loginData = @{
        username = 'admin'
        password = 'admin123'
    } | ConvertTo-Json
    
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginData -ContentType 'application/json'
    Write-Host "✓ 登录成功，获取到token: $($loginResponse.token.Substring(0,20))..." -ForegroundColor Green
    $token = $loginResponse.token
    
    # 2. 测试 /api/auth/me 接口
    Write-Host '2. 测试 /api/auth/me 接口...' -ForegroundColor Yellow
    $headers = @{ 'Authorization' = "Bearer $token" }
    $meResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/me" -Method GET -Headers $headers
    Write-Host "✓ 用户信息: $($meResponse.username), 角色: $($meResponse.role)" -ForegroundColor Green
    
    # 3. 测试管理员后台页面访问
    Write-Host '3. 测试管理员后台页面访问...' -ForegroundColor Yellow
    $dashboardResponse = Invoke-WebRequest -Uri "$baseUrl/admin-dashboard.html" -UseBasicParsing
    Write-Host "✓ 管理员后台页面状态码: $($dashboardResponse.StatusCode)" -ForegroundColor Green
    Write-Host "✓ 页面大小: $($dashboardResponse.Content.Length) 字符" -ForegroundColor Green
    
    # 4. 测试管理员API访问
    Write-Host '4. 测试管理员API访问...' -ForegroundColor Yellow
    try {
        $adminResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/users" -Method GET -Headers $headers
        Write-Host "✓ 管理员API访问成功，用户数量: $($adminResponse.Count)" -ForegroundColor Green
    } catch {
        Write-Host "⚠ 管理员API访问失败: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host '=== 所有测试完成 ===' -ForegroundColor Green
    
} catch {
    Write-Host "✗ 错误: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "详细信息: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

Write-Host '按任意键继续...' -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')