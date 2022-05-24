#!/bin/bash

# Do not use 'set -e' because "az spring application-configuration-service git repo list" returns error when result is empty.
set -uxo pipefail

: "${RESOURCE_GROUP:?'must be set'}"
: "${SPRING_APPS_SERVICE:?'must be set'}"

main() {
  local length

  length=$(az spring application-configuration-service git repo list \
    --resource-group "$RESOURCE_GROUP" \
    --service "$SPRING_APPS_SERVICE" \
    --query "[?name=='acme-fitness-store-config'] | length(@)")

  if [[ -z "$length" || "$length" == "0" ]]; then
    az spring application-configuration-service git repo add \
      --name acme-fitness-store-config \
      --label main \
      --patterns "catalog/default,catalog/key-vault,identity/default,identity/key-vault,payment/default" \
      --resource-group "$RESOURCE_GROUP" \
      --service "$SPRING_APPS_SERVICE" \
      --uri "https://github.com/Azure-Samples/acme-fitness-store-config"
  else
    echo "Application Configuration Service is already configured."
  fi
}

main
