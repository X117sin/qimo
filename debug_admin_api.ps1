# 详细分析管理员API访问问题
$baseUrl = 'http://localhost:8082'
Write-Host '=== 详细分析管理员API访问问题 ===' -ForegroundColor Green

try {
    Write-Host '1. 管理员登录获取token...' -ForegroundColor Yellow
    $loginData = @{
        username = 'admin'
        password = 'admin123'
    } | ConvertTo-Json
    
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginData -ContentType 'application/json'
    $token = $loginResponse.token
    Write-Host "✓ 登录成功，token长度: $($token.Length)" -ForegroundColor Green
    Write-Host "Token前20字符: $($token.Substring(0,20))..." -ForegroundColor Gray
    
    Write-Host '2. 测试 /api/auth/me 接口...' -ForegroundColor Yellow
    $headers = @{ 'Authorization' = "Bearer $token" }
    $meResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/me" -Method GET -Headers $headers
    Write-Host "✓ 用户: $($meResponse.username), 角色: $($meResponse.role)" -ForegroundColor Green
    
    Write-Host '3. 测试管理员API /api/admin/users...' -ForegroundColor Yellow
    try {
        $adminResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/users" -Method GET -Headers $headers
        Write-Host "✓ 管理员API成功，返回用户数: $($adminResponse.Count)" -ForegroundColor Green
        Write-Host "用户列表: $($adminResponse | ConvertTo-Json -Depth 2)" -ForegroundColor Gray
    } catch {
        Write-Host "✗ 管理员API失败: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            Write-Host "状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        }
        if ($_.ErrorDetails.Message) {
            Write-Host "响应内容: $($_.ErrorDetails.Message)" -ForegroundColor Red
        }
        
        # 尝试获取更详细的错误信息
        try {
            $errorResponse = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($errorResponse)
            $errorContent = $reader.ReadToEnd()
            Write-Host "详细错误内容: $errorContent" -ForegroundColor Red
        } catch {
            Write-Host "无法获取详细错误信息" -ForegroundColor Red
        }
    }
    
    Write-Host '4. 测试管理员API /api/admin/statistics...' -ForegroundColor Yellow
    try {
        $statsResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/statistics" -Method GET -Headers $headers
        Write-Host "✓ 统计API成功" -ForegroundColor Green
        Write-Host "统计信息: $($statsResponse | ConvertTo-Json)" -ForegroundColor Gray
    } catch {
        Write-Host "✗ 统计API失败: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            Write-Host "状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        }
    }
    
    Write-Host '5. 验证token格式和内容...' -ForegroundColor Yellow
    Write-Host "完整token: $token" -ForegroundColor Gray
    Write-Host "Authorization头: Bearer $token" -ForegroundColor Gray
    
} catch {
    Write-Host "✗ 登录失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "详细信息: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

Write-Host '=== 分析完成 ===' -ForegroundColor Green
Write-Host '按任意键继续...' -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')