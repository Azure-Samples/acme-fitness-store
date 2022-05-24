#!/bin/bash

set -euxo pipefail

: "${RESOURCE_GROUP:?'must be set'}"
: "${REGION:?'must be set'}"
: "${SPRING_APPS_SERVICE:?'must be set'}"

main() {
  local length

  length=$(az spring list \
    --resource-group "$RESOURCE_GROUP" \
    --query "[?name=='$SPRING_APPS_SERVICE'] | length(@)")

  if [[ "$length" == "0" ]]; then
    az spring create \
      --name "$SPRING_APPS_SERVICE" \
      --resource-group "$RESOURCE_GROUP" \
      --location "$REGION" \
      --sku Enterprise \
      --enable-application-configuration-service \
      --enable-service-registry \
      --enable-gateway \
      --enable-api-portal

      az spring gateway update \
        --resource-group "$RESOURCE_GROUP" \
        --service "$SPRING_APPS_SERVICE" \
        --assign-endpoint true

      az spring api-portal update \
        --resource-group "$RESOURCE_GROUP" \
        --service "$SPRING_APPS_SERVICE" \
        --assign-endpoint true
  else
    echo "Azure Spring Service is already created."
  fi
}

main
