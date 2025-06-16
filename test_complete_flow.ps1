# 完整的登录和API测试流程

Write-Host "=== 开始完整的API测试流程 ==="

# 步骤1: 测试登录API
Write-Host "\n步骤1: 测试登录API"
try {
    $loginBody = '{"username":"admin","password":"admin123"}'
    Write-Host "登录请求体: $loginBody"
    
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    
    Write-Host "登录成功!"
    Write-Host "用户角色: $($loginResponse.role)"
    Write-Host "用户名: $($loginResponse.user.username)"
    $token = $loginResponse.token
    Write-Host "获取到Token: $($token.Substring(0, 50))..."
    
} catch {
    Write-Host "登录失败: $($_.Exception.Message)"
    exit 1
}

# 步骤2: 使用Token测试用户列表API
Write-Host "\n步骤2: 测试用户列表API"
try {
    $headers = @{
        'Authorization' = "Bearer $token"
        'Content-Type' = 'application/json'
    }
    
    Write-Host "使用Authorization头: Bearer $($token.Substring(0, 50))..."
    
    $usersResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/admin/users" -Method GET -Headers $headers
    
    Write-Host "用户列表获取成功!"
    Write-Host "用户数量: $($usersResponse.Count)"
    foreach ($user in $usersResponse) {
        Write-Host "- 用户: $($user.username), 角色: $($user.role), ID: $($user.id)"
    }
    
} catch {
    Write-Host "用户列表获取失败: $($_.Exception.Message)"
    Write-Host "状态码: $($_.Exception.Response.StatusCode)"
}

Write-Host "\n=== API测试流程完成 ==="