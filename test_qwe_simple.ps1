# 简单测试qwe用户登录
$loginData = @{
    username = "qwe"
    password = "qwe123"
} | ConvertTo-Json

Write-Host "发送登录请求..."
Write-Host "请求数据: $loginData"

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8082/api/auth/login" -Method POST -Body $loginData -ContentType "application/json" -Verbose
    Write-Host "登录成功!"
    Write-Host "响应: $($response | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "登录失败!"
    Write-Host "错误信息: $($_.Exception.Message)"
    Write-Host "响应状态码: $($_.Exception.Response.StatusCode)"
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "响应内容: $responseBody"
    }
}