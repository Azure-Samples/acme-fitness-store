#!/bin/bash

set -euxo pipefail

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

create_or_update_route_config() {
  local config_names=$1
  local config_name=$2
  local config_file=$3
  local is_found

  is_found=$(echo "$config_names" | jq --arg name "$config_name" 'any(.[]; . == $name)')
  if [[ "$is_found" = false ]]; then
    az spring gateway route-config create \
      --name "$config_name" \
      --app-name "$config_name" \
      --routes-file "$config_file"
  else
    az spring gateway route-config update \
      --name "$config_name" \
      --app-name "$config_name" \
      --routes-file "$config_file"
  fi
}

main() {
  local gateway_url config_names

  az configure --defaults group="$RESOURCE_GROUP" spring="$SPRING_APPS_SERVICE"

  gateway_url=$(az spring gateway show | jq -r '.properties.url')

  az spring gateway update \
    --api-description "Acme Fitness Store API" \
    --api-title "Acme Fitness Store" \
    --api-version "v1.0" \
    --server-url "https://$gateway_url" \
    --allowed-origins "*" \
    --client-id "$CLIENT_ID" \
    --client-secret "$CLIENT_SECRET" \
    --scope "$SCOPE" \
    --issuer-uri "$ISSUER_URI"

  config_names=$(az spring gateway route-config list --query '[].name')

  create_or_update_route_config "$config_names" "$IDENTITY_SERVICE_APP" identity-service.json
  create_or_update_route_config "$config_names" "$CART_SERVICE_APP" cart-service.json
  create_or_update_route_config "$config_names" "$ORDER_SERVICE_APP" order-service.json
  create_or_update_route_config "$config_names" "$CATALOG_SERVICE_APP" catalog-service.json
  create_or_update_route_config "$config_names" "$FRONTEND_APP" frontend.json
}

main
