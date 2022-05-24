#!/bin/bash

set -euxo pipefail

: "${RESOURCE_GROUP:?'must be set'}"
: "${SPRING_APPS_SERVICE:?'must be set'}"
: "${IDENTITY_SERVICE_APP:?'must be set'}"
: "${CART_SERVICE_APP:?'must be set'}"
: "${ORDER_SERVICE_APP:?'must be set'}"
: "${PAYMENT_SERVICE_APP:?'must be set'}"
: "${CATALOG_SERVICE_APP:?'must be set'}"
: "${FRONTEND_APP:?'must be set'}"

create_app_if_not_exist() {
  local app_names=$1
  local app=$2
  local is_found

  is_found=$(echo "$app_names" | jq --arg name "$app" 'any(.[]; . == $name)')
  if [[ "$is_found" = false ]]; then
    az spring app create --name "$app" --instance-count 1 --memory 1Gi
  else
    echo "Application '$app' has been already created."
  fi
}

main() {
  local app_names

  az configure --defaults group="$RESOURCE_GROUP" spring="$SPRING_APPS_SERVICE"

  app_names=$(az spring app list --query '[].name')

  create_app_if_not_exist "$app_names" "$IDENTITY_SERVICE_APP"
  create_app_if_not_exist "$app_names" "$CART_SERVICE_APP"
  create_app_if_not_exist "$app_names" "$ORDER_SERVICE_APP"
  create_app_if_not_exist "$app_names" "$PAYMENT_SERVICE_APP"
  create_app_if_not_exist "$app_names" "$CATALOG_SERVICE_APP"
  create_app_if_not_exist "$app_names" "$FRONTEND_APP"
}

main
