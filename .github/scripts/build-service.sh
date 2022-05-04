#!/bin/bash

# Do not use 'set -e' because "az spring-cloud build-service builder show" returns error when builder not found.
set -uxo pipefail

: "${RESOURCE_GROUP:?'must be set'}"
: "${SPRING_CLOUD_SERVICE:?'must be set'}"
: "${CUSTOM_BUILDER:?'must be set'}"

main() {
  local builder

  builder=$(az spring-cloud build-service builder show \
    --resource-group "$RESOURCE_GROUP" \
    --service "$SPRING_CLOUD_SERVICE" \
    --name "$CUSTOM_BUILDER")

  if [[ -z "$builder" ]]; then
    az spring-cloud build-service builder create \
      --name "$CUSTOM_BUILDER" \
      --builder-file builder.json \
      --resource-group "$RESOURCE_GROUP" \
      --service "$SPRING_CLOUD_SERVICE" \
      --no-wait
  else
    az spring-cloud build-service builder update \
      --name "$CUSTOM_BUILDER" \
      --builder-file builder.json \
      --resource-group "$RESOURCE_GROUP" \
      --service "$SPRING_CLOUD_SERVICE" \
      --no-wait
  fi
}

main
