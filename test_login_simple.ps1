# 简单的登录测试脚本
$uri = "http://localhost:8082/api/auth/login"
$headers = @{
    "Content-Type" = "application/json"
}
$body = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Write-Host "请求URL: $uri"
Write-Host "请求体: $body"

try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -Headers $headers -Body $body
    Write-Host "登录成功!"
    Write-Host "响应: $($response | ConvertTo-Json -Depth 3)"
    
    # 提取token
    $token = $response.token
    Write-Host "JWT Token (前50字符): $($token.Substring(0, [Math]::Min(50, $token.Length)))..."
    Write-Host "Token长度: $($token.Length)"
    
    # 测试受保护的接口
    $authHeaders = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    Write-Host "\n测试受保护的接口..."
    $userResponse = Invoke-RestMethod -Uri "http://localhost:8082/users/me" -Method GET -Headers $authHeaders
    Write-Host "用户信息: $($userResponse | ConvertTo-Json -Depth 3)"
    
} catch {
    Write-Host "错误: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode
        Write-Host "状态码: $statusCode"
        
        try {
            $errorStream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($errorStream)
            $errorBody = $reader.ReadToEnd()
            Write-Host "错误响应: $errorBody"
        } catch {
            Write-Host "无法读取错误响应"
        }
    }
}