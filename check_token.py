import json
import base64
from datetime import datetime
import time

token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzQ5NzMzMzgxLCJleHAiOjE3NDk4MTk3ODF9.1KTTT-Qfxk8-TwBhQbrHXt3k2NoB8uVAysaDc8GWTzU'

# 解析JWT payload
payload = json.loads(base64.b64decode(token.split('.')[1] + '==').decode())

print('Token payload:', payload)
print('Issued at:', datetime.fromtimestamp(payload['iat']))
print('Expires at:', datetime.fromtimestamp(payload['exp']))
print('Current time:', datetime.now())
print('Is expired:', time.time() > payload['exp'])