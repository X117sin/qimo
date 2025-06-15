# 详细的JWT认证调试脚本

Write-Host "=== 详细的JWT认证调试 ==="

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
    Write-Host "完整Token: $token"
    Write-Host "Token长度: $($token.Length)"
    
} catch {
    Write-Host "登录失败: $($_.Exception.Message)"
    exit 1
}

# 步骤2: 测试Authorization头格式
Write-Host "\n步骤2: 测试Authorization头格式"
$authHeader = "Bearer $token"
Write-Host "Authorization头: $authHeader"
Write-Host "Authorization头长度: $($authHeader.Length)"

# 步骤3: 使用详细的WebRequest测试
Write-Host "\n步骤3: 使用WebRequest进行详细测试"
try {
    $request = [System.Net.WebRequest]::Create("http://localhost:8082/api/admin/users")
    $request.Method = "GET"
    $request.Headers.Add("Authorization", $authHeader)
    $request.ContentType = "application/json"
    
    Write-Host "请求URL: $($request.RequestUri)"
    Write-Host "请求方法: $($request.Method)"
    Write-Host "Content-Type: $($request.ContentType)"
    Write-Host "Authorization头: $($request.Headers['Authorization'])"
    
    $response = $request.GetResponse()
    $stream = $response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $responseText = $reader.ReadToEnd()
    
    Write-Host "响应状态: $($response.StatusCode)"
    Write-Host "响应内容: $responseText"
    
    $reader.Close()
    $stream.Close()
    $response.Close()
    
} catch [System.Net.WebException] {
    $errorResponse = $_.Exception.Response
    if ($errorResponse) {
        Write-Host "错误状态码: $($errorResponse.StatusCode)"
        Write-Host "错误状态描述: $($errorResponse.StatusDescription)"
        
        $errorStream = $errorResponse.GetResponseStream()
        $errorReader = New-Object System.IO.StreamReader($errorStream)
        $errorText = $errorReader.ReadToEnd()
        Write-Host "错误响应体: $errorText"
        
        $errorReader.Close()
        $errorStream.Close()
    }
    Write-Host "异常消息: $($_.Exception.Message)"
} catch {
    Write-Host "其他错误: $($_.Exception.Message)"
}

Write-Host "\n=== 调试完成 ==="