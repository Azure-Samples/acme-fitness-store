#!/bin/bash

set -euxo pipefail

: "${RESOURCE_GROUP:?'must be set'}"
: "${REGION:?'must be set'}"
: "${SPRING_CLOUD_SERVICE:?'must be set'}"

main() {
  local length

  length=$(az spring-cloud list \
    --resource-group "$RESOURCE_GROUP" \
    --query "[?name=='$SPRING_CLOUD_SERVICE'] | length(@)")

  if [[ "$length" == "0" ]]; then
    az spring-cloud create \
      --name "$SPRING_CLOUD_SERVICE" \
      --resource-group "$RESOURCE_GROUP" \
      --location "$REGION" \
      --sku Enterprise \
      --enable-application-configuration-service \
      --enable-service-registry \
      --enable-gateway \
      --enable-api-portal

      az spring-cloud gateway update \
        --resource-group "$RESOURCE_GROUP" \
        --service "$SPRING_CLOUD_SERVICE" \
        --assign-endpoint true

      az spring-cloud api-portal update \
        --resource-group "$RESOURCE_GROUP" \
        --service "$SPRING_CLOUD_SERVICE" \
        --assign-endpoint true
  else
    echo "Azure Spring Service is already created."
  fi
}

main
