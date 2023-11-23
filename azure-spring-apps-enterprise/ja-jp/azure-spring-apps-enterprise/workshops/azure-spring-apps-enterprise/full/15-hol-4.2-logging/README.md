### アプリケーションのログストリームを取得

下記のコマンドを実行して、アプリコンソールからカタログ・サービスの最新 100 行のログの取得します。

```shell
az spring app logs \
    -n ${CATALOG_SERVICE_APP} \
    --lines 100
```

`-f` の引数を追加することで、アプリからリアルタイムのログストリーミングを取得できます。カタログ・サービスのログストリーミングも試してください。

```shell
az spring app logs \
    -n ${CATALOG_SERVICE_APP} \
    -f
```

`az spring app logs -h` を実行すると、より多くの引数やログストリームの機能を調べる事ができます。

### ACME Fitness Store　のログとメトリクスを　Azure Log Analytics　で監視

Log Analyticsを開きます - Azure Spring Apps と同じリソースグループで Log Analytics を見つけることができます。

Log Analyticsページで、`Logs` を選択し、以下に記載するサンプルのクエリを実行してください。

下記の Kusto クエリを実行して、アプリケーション・ログを表示します:

```sql
    AppPlatformLogsforSpring 
    | where TimeGenerated > ago(1h)
    | limit 500
    | sort by TimeGenerated
    | project TimeGenerated, AppName, Log
```

![Example output from all application logs query](../../../../../../media/all-app-logs-in-log-analytics.jpg)

下記の Kusto クエリを実行して、`catalog-service` アプリケーションのログを表示します:

```sql
    AppPlatformLogsforSpring 
    | where AppName has "catalog-service"
    | limit 500
    | sort by TimeGenerated
    | project TimeGenerated, AppName, Log
```

![Example output from catalog service logs](../../../../../../media/catalog-app-logs-in-log-analytics.jpg)

下記の Kusto クエリを実行し、各アプリでスローされたエラーや例外を表示します:

```sql
    AppPlatformLogsforSpring 
    | where Log contains "error" or Log contains "exception"
    | extend FullAppName = strcat(ServiceName, "/", AppName)
    | summarize count_per_app = count() by FullAppName, ServiceName, AppName, _ResourceId
    | sort by count_per_app desc 
    | render piechart
```

![An example output from the Ingress Logs](../../../../../../media/ingress-logs-in-log-analytics.jpg)


下記の Kusto クエリを入力し実行して、Azure Spring Apps に対するすべてのインバウンド呼び出しを表示します:

```sql
    AppPlatformIngressLogs
    | project TimeGenerated, RemoteAddr, Host, Request, Status, BodyBytesSent, RequestTime, ReqId, RequestHeaders
    | sort by TimeGenerated
```

下記の Kusto クエリを実行し、Azure Spring Apps で管理する Spring Cloud Gateway からの全ログを表示します:

```sql
    AppPlatformSystemLogs
    | where LogType contains "SpringCloudGateway"
    | project TimeGenerated,Log
```

![An example out from the Spring Cloud Gateway Logs](../../../../../../media/spring-cloud-gateway-logs-in-log-analytics.jpg)

下記の Kusto クエリを実行し、Azure Spring Apps で管理する Spring Cloud Service Registry からの全ログを表示します:"

```sql
    AppPlatformSystemLogs
    | where LogType contains "ServiceRegistry"
    | project TimeGenerated, Log
```

![An example output from service registry logs](../../../../../../media/service-registry-logs-in-log-analytics.jpg)

⬅️ 前の作業: [14 - ハンズオン・ラボ 4.1 - End-End Observability](../14-hol-4.1-end-to-end-observability/README.md)

➡️ 次の作業: [16 - まとめ](../16-Conclusion/README.md)