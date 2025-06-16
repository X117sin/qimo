# 测试admin登录功能
Write-Host "=== 测试admin登录功能 ===" -ForegroundColor Green

# 1. 测试登录接口
Write-Host "1. 测试登录接口..." -ForegroundColor Yellow
$loginResponse = curl -s -X POST -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}' http://localhost:8082/api/auth/login
Write-Host "登录响应: $loginResponse"

# 解析JWT token
$token = ($loginResponse | ConvertFrom-Json).token
if ($token) {
    Write-Host "JWT Token获取成功: $($token.Substring(0,50))..." -ForegroundColor Green
} else {
    Write-Host "JWT Token获取失败" -ForegroundColor Red
    exit 1
}

# 2. 测试/api/auth/me接口
Write-Host "\n2. 测试/api/auth/me接口..." -ForegroundColor Yellow
$authMeResponse = curl -s -H "Authorization: Bearer $token" http://localhost:8082/api/auth/me
Write-Host "用户信息响应: $authMeResponse"

# 3. 测试管理员统计接口
Write-Host "\n3. 测试管理员统计接口..." -ForegroundColor Yellow
$statsResponse = curl -s -H "Authorization: Bearer $token" http://localhost:8082/admin/statistics
Write-Host "统计信息响应: $statsResponse"

# 4. 测试访问admin-dashboard.html页面
Write-Host "\n4. 测试访问admin-dashboard.html页面..." -ForegroundColor Yellow
$dashboardResponse = curl -s http://localhost:8082/admin-dashboard.html
if ($dashboardResponse -match "管理员后台") {
    Write-Host "admin-dashboard.html页面访问成功" -ForegroundColor Green
} else {
    Write-Host "admin-dashboard.html页面访问可能有问题" -ForegroundColor Red
}

Write-Host "\n=== 测试完成 ===" -ForegroundColor Green