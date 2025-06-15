# 详细的JWT认证调试脚本

# 1. 登录获取token
Write-Host "=== 步骤1: 登录获取token ==="
$loginData = @{ 
    username = 'admin'
    password = 'admin123' 
} | ConvertTo-Json

Write-Host "登录请求数据: $loginData"

try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8082/api/auth/login' -Method POST -Body $loginData -ContentType 'application/json'
    Write-Host "登录成功!"
    Write-Host "完整响应:"
    $response | ConvertTo-Json -Depth 3
    
    $token = $response.token
    Write-Host "提取的token: $token"
    Write-Host "Token长度: $($token.Length)"
    
    # 2. 测试/api/auth/me接口
    Write-Host "`n=== 步骤2: 测试/api/auth/me接口 ==="
    $headers = @{ 
        'Authorization' = "Bearer $token"
        'Content-Type' = 'application/json'
    }
    
    Write-Host "请求头:"
    $headers | ConvertTo-Json
    
    try {
        $userInfo = Invoke-RestMethod -Uri 'http://localhost:8082/api/auth/me' -Method GET -Headers $headers
        Write-Host "获取用户信息成功!"
        Write-Host "用户信息:"
        $userInfo | ConvertTo-Json -Depth 3
    } catch {
        Write-Host "获取用户信息失败!"
        Write-Host "错误信息: $($_.Exception.Message)"
        Write-Host "HTTP状态码: $($_.Exception.Response.StatusCode)"
        
        # 尝试获取详细错误信息
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "响应体: $responseBody"
        }
    }
    
} catch {
    Write-Host "登录失败!"
    Write-Host "错误信息: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "响应体: $responseBody"
    }
}

Write-Host "`n=== 调试完成 ==="