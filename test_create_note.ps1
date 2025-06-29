# Test create note script
$token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzUwMzI4MjMxLCJleHAiOjE3NTU1MTIyMzF9.4_opNEOK0hf55K4CSGVts9ujmAeKI9tTpbo56dxNZys"

# Create first note
$body1 = '{"passageId": 1, "content": "太阳病的核心特征是脉浮、头项强痛、恶寒。这是表证的典型表现，治疗应以解表为主。桂枝汤或麻黄汤都是常用方剂。"}'

try {
    $response1 = Invoke-RestMethod -Uri "http://localhost:8082/api/notes" -Method POST -Body $body1 -Headers @{"Authorization"="Bearer $token"; "Content-Type"="application/json"}
    Write-Host "Create Note 1 Success:"
    $response1 | ConvertTo-Json
} catch {
    Write-Host "Create Note 1 Failed: $($_.Exception.Message)"
}

# Create second note
$body2 = '{"passageId": 2, "content": "肺痿病机复杂，主要由肺热久恋导致。肺金受邪，清肃失司，痰浊上逆。治疗需要清热润肺，化痰降逆。"}'

try {
    $response2 = Invoke-RestMethod -Uri "http://localhost:8082/api/notes" -Method POST -Body $body2 -Headers @{"Authorization"="Bearer $token"; "Content-Type"="application/json"}
    Write-Host "Create Note 2 Success:"
    $response2 | ConvertTo-Json
} catch {
    Write-Host "Create Note 2 Failed: $($_.Exception.Message)"
}

# Create third note
$body3 = '{"passageId": 3, "content": "温病学说强调温邪上受首先犯肺，与伤寒论的六经辨证不同。温病重视卫气营血辨证和三焦辨证。"}'

try {
    $response3 = Invoke-RestMethod -Uri "http://localhost:8082/api/notes" -Method POST -Body $body3 -Headers @{"Authorization"="Bearer $token"; "Content-Type"="application/json"}
    Write-Host "Create Note 3 Success:"
    $response3 | ConvertTo-Json
} catch {
    Write-Host "Create Note 3 Failed: $($_.Exception.Message)"
}

Write-Host "All notes creation attempts completed."