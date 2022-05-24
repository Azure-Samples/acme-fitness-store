#!/bin/bash

set -uxo pipefail

: "${RESOURCE_GROUP:?'must be set'}"
: "${SPRING_APPS_SERVICE:?'must be set'}"
: "${WORKSPACE_NAME:?'must be set'}"

main() {
  local log_analytics_resource_id spring_cloud_resource_id

  log_analytics_resource_id=$(az monitor log-analytics workspace show \
    --resource-group "$RESOURCE_GROUP" \
    --workspace-name "$WORKSPACE_NAME" | jq -r '.id')

  spring_cloud_resource_id=$(az spring show \
    --name "$SPRING_APPS_SERVICE" \
    --resource-group "$RESOURCE_GROUP" | jq -r '.id')

  az monitor diagnostic-settings create --name "send-logs-and-metrics-to-log-analytics" \
    --resource "$spring_cloud_resource_id" \
    --workspace "$log_analytics_resource_id" \
    --logs '[
              {
                "category": "ApplicationConsole",
                "enabled": true,
                "retentionPolicy": {
                  "enabled": false,
                  "days": 0
                }
              },
              {
                "category": "SystemLogs",
                "enabled": true,
                "retentionPolicy": {
                  "enabled": false,
                  "days": 0
                }
              },
              {
                "category": "IngressLogs",
                "enabled": true,
                "retentionPolicy": {
                  "enabled": false,
                  "days": 0
                }
              }
           ]' \
    --metrics '[
                 {
                    "category": "AllMetrics",
                    "enabled": true,
                    "retentionPolicy": {
                      "enabled": false,
                      "days": 0
                    }
                 }
               ]'
}

main
