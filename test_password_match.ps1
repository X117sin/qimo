# 测试admin123密码是否与数据库中的BCrypt哈希值匹配
Write-Host "=== 测试密码匹配 ===" -ForegroundColor Green

# 数据库中的BCrypt哈希值
$dbHash = '$2a$10$rDkPvvAFV7nGQ59R.S5NcOjQ0UbLVJjsRJXHPUyAzXCOFcQwAFjxe'
Write-Host "数据库中的密码哈希: $dbHash"

# 测试密码
$testPassword = 'admin123'
Write-Host "测试密码: $testPassword"

# 使用在线BCrypt验证器或者直接测试登录
Write-Host "\n正在测试登录..." -ForegroundColor Yellow

# 创建临时JSON文件
$jsonData = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

$jsonData | Out-File -FilePath "temp_login.json" -Encoding UTF8

# 使用curl测试登录
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method Post -ContentType "application/json" -Body $jsonData
    Write-Host "登录成功!" -ForegroundColor Green
    Write-Host "响应: $($response | ConvertTo-Json)"
} catch {
    Write-Host "登录失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "详细错误: $($_.ErrorDetails.Message)"
}

# 清理临时文件
Remove-Item "temp_login.json" -ErrorAction SilentlyContinue

Write-Host "\n=== 测试完成 ===" -ForegroundColor Green