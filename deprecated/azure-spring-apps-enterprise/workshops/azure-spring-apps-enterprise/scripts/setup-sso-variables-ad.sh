export SCOPE=openid,profile,email
export CLIENT_ID=$(cat sso.json | jq -r '.appId')
export CLIENT_SECRET=$(cat sso.json | jq -r '.password')
export TENANT_ID=$(cat sso.json | jq -r '.tenant')
export ISSUER_URI=https://login.microsoftonline.com/${TENANT_ID}/v2.0
export JWK_SET_URI=https://login.microsoftonline.com/${TENANT_ID}/discovery/v2.0/keys