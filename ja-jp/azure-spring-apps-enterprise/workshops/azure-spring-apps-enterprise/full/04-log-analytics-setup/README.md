
### Log Analytics workspace の設定

作成した Azure Spring Apps サービスと Log Analytics ワークスペースのリソース ID を取得します。

```shell
export LOG_ANALYTICS_RESOURCE_ID=$(az monitor log-analytics workspace show \
    --resource-group ${RESOURCE_GROUP} \
    --workspace-name ${LOG_ANALYTICS_WORKSPACE} | jq -r '.id')

export SPRING_APPS_RESOURCE_ID=$(az spring show \
    --name ${SPRING_APPS_SERVICE} \
    --resource-group ${RESOURCE_GROUP} | jq -r '.id')
```

Azure Spring Apps サービス用モニタリングの診断設定を設定します。

```shell
az monitor diagnostic-settings create --name "send-logs-and-metrics-to-log-analytics" \
    --resource ${SPRING_APPS_RESOURCE_ID} \
    --workspace ${LOG_ANALYTICS_RESOURCE_ID} \
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
```

前のワークショップで作成した Azure Spring Apps Enterprise のインスタンスで、上記のコマンドを実行します。
このインスタンスは、以降のワークショップで全サービス/アプリをデプロイするコンテナーとして機能します。

⬅️ 前のワークショップ: [03 - ワークショップ用の環境セットアップ](../03-workshop-environment-setup/README.md)

➡️ 次のワークショップ: [05 - Hello World の単純な Spring Boot アプリのデプロイ](../05-hol-1-hello-world-app/README.md)