# JWT认证测试脚本

# 登录获取token
$loginBody = @'
{"username":"admin","password":"admin123"}
'@

Write-Host "正在登录..."
try {
    $loginResponse = Invoke-RestMethod -Uri 'http://localhost:8082/api/auth/login' -Method POST -Body $loginBody -ContentType 'application/json'
    Write-Host "登录成功！"
    if ($loginResponse.token) {
        Write-Host "Token长度: $($loginResponse.token.Length)"
        Write-Host "Token前缀: $($loginResponse.token.Substring(0,20))..."
    } else {
        Write-Host "警告: 未获取到token"
        Write-Host "登录响应: $($loginResponse | ConvertTo-Json)"
        return
    }
    
    # 创建笔记
    $noteBody = @'
{"passageId":1,"content":"测试笔记内容"}
'@
    
    $headers = @{
        'Authorization' = "Bearer $($loginResponse.token)"
        'Content-Type' = 'application/json'
    }
    
    Write-Host "正在创建笔记..."
    Write-Host "Authorization头: $($headers['Authorization'].Substring(0,50))..."
    
    $noteResponse = Invoke-RestMethod -Uri 'http://localhost:8082/api/notes' -Method POST -Body $noteBody -Headers $headers
    Write-Host "笔记创建成功！"
    Write-Host "响应: $($noteResponse | ConvertTo-Json)"
    
} catch {
    Write-Host "错误: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "状态码: $($_.Exception.Response.StatusCode)"
    }
}