#!/bin/bash

# Do not use 'set -e' because "az ad app update" does not have permission yet.
set -xuo pipefail

: "${RESOURCE_GROUP:?'must be set'}"
: "${SPRING_APPS_SERVICE:?'must be set'}"
: "${IDENTITY_SERVICE_APP:?'must be set'}"
: "${CART_SERVICE_APP:?'must be set'}"
: "${ORDER_SERVICE_APP:?'must be set'}"
: "${CATALOG_SERVICE_APP:?'must be set'}"
: "${FRONTEND_APP:?'must be set'}"
: "${CLIENT_ID:?'must be set'}"
: "${CLIENT_SECRET:?'must be set'}"
: "${SCOPE:?'must be set'}"
: "${ISSUER_URI:?'must be set'}"
: "${KEY_VAULT:?'must be set'}"

set_keyvault_policy() {
    local app_name=$1
    local principal_id

    principal_id=$(az spring app identity show --name "$app_name"| jq -r '.principalId')
    if [ -z "$principal_id" ]; then
      principal_id=$(az spring app identity assign --name "$app_name" | jq -r '.identity.principalId')
    fi

    az keyvault set-policy --name "$KEY_VAULT" --object-id "$principal_id" --secret-permissions get list
}

main() {
  local gateway_url portal_url

  az configure --defaults group="$RESOURCE_GROUP" spring="$SPRING_APPS_SERVICE"

  gateway_url=$(az spring gateway show | jq -r '.properties.url')

  portal_url=$(az spring api-portal show | jq -r '.properties.url')

  az ad app update \
    --id "$CLIENT_ID" \
    --reply-urls "https://$gateway_url/login/oauth2/code/sso" "https://$portal_url/oauth2-redirect.html" "https://$portal_url/login/oauth2/code/sso"

  az spring api-portal update \
    --client-id "$CLIENT_ID" \
    --client-secret "$CLIENT_SECRET"\
    --scope "openid,profile,email" \
    --issuer-uri "$ISSUER_URI"

  set_keyvault_policy "$IDENTITY_SERVICE_APP"
  set_keyvault_policy "$CART_SERVICE_APP"
  set_keyvault_policy "$ORDER_SERVICE_APP"
  set_keyvault_policy "$CATALOG_SERVICE_APP"
}

main
