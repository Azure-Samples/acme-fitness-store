ここでは、アプリケーションが健全な状態かを把握するために、ライブでアプリケーションのメトリクスを取得したり、ログに対してクエリを行います。

### Key Vault に Instrumentation Key を追加

Application Insights の Instrumentation Key を Java 以外のアプリケーションに提供する必要があります。

> ご注意: 将来のバージョンでは、Java 以外のアプリケーション用のビルドパックも Application Insights のバインディングをサポートし、このステップは不要になる予定です。

Application Insights　のインストルメンテーション・キーを取得し、Key Vault に追加する

```shell
export INSTRUMENTATION_KEY=$(az monitor app-insights component show --app ${APPLICATION_INSIGHTS} | jq -r '.connectionString')

az keyvault secret set --vault-name ${KEY_VAULT} \
    --name "ApplicationInsights--ConnectionString" --value ${INSTRUMENTATION_KEY}
```

### サンプリングレートの更新

Application Insights バインドのサンプリング・レートを増加します。

```shell
az spring build-service builder buildpack-binding set \
    --builder-name default \
    --name default \
    --type ApplicationInsights \
    --properties sampling-rate=100 connection_string=${INSTRUMENTATION_KEY}
```

### アプリケーションのリロード

アプリケーションを再起動し設定をリロードします。Java アプリケーションでは、これにより新しいサンプリング・レートが有効になります。Java 以外のアプリケーションの場合、これにより、Key Vault からインストルメンテーション キーにアクセスできるようになります。

```shell
az spring app restart -n ${CART_SERVICE_APP}
az spring app restart -n ${ORDER_SERVICE_APP}
az spring app restart -n ${IDENTITY_SERVICE_APP}
az spring app restart -n ${CATALOG_SERVICE_APP}
az spring app restart -n ${PAYMENT_SERVICE_APP}
```

### アプリケーションのログ・ストリームを取得

下記のコマンドを使用して、カタログ・サービスから最新の 100 行のアプリ・コンソール・ログを取得します

```shell
az spring app logs \
    -n ${CATALOG_SERVICE_APP} \
    --lines 100
```

`-f`　のパラメーターを追加することで、リアルタイムでアプリのログ・ストリーミングを取得できます。ストリーミングで、カタログ・サービスのログを表示してください


```shell
az spring app logs \
    -n ${CATALOG_SERVICE_APP} \
    -f
```

`az spring app logs -h` を使用して、より多くのパラメーターとログ・ストリーム機能を調べることができます。

### トラフィックの生成

ACME Fitness Shop アプリケーションに対してトラフィックを生成します。アプリケーション内の画面を繊維し、カタログを表示したり、注文を行ったりします。

トラフィックを継続的に生成するには、トラフィックジェネレーターを使用してください。

```shell
cd /workspaces/acme-fitness-store/azure-spring-apps-enterprise/load-test/traffic-generator
GATEWAY_URL=https://${GATEWAY_URL} 
./gradlew gatlingRun-com.vmware.acme.simulation.GuestSimulation
cd -
```

トラフィックジェネレーターが動作している間、次の作業に進んでください。

### Application Insights でアプリの依存関係の確認と監視

Azure Spring Apps に紐づく Application Insights を開き、Spring Boot アプリケーションの監視を行います。
Application Insights は、Azure Spring Apps サービスインスタンスを作成したリソースグループと同じ所にあります。

`Application Map`タブに移動します:

![An image showing the Application Map of Azure Application Insights](../../../../../../media/fitness-store-application-map.jpg)

`Performance`タブに移動します:

![An image showing the Performance Blade of Azure Application Insights](../../../../../../media/performance.jpg)

`Performance/Dependencies` タブに移動します - ここでは、特に SQL 呼び出しにおけるパフォーマンスを確認できます:

![An image showing the Dependencies section of the Performance Blade of Azure Application Insights](../../../../../../media/performance_dependencies.jpg)

`Performance/Roles` タブに移動します - ここでは、個々のインスタンスやロールのパフォーマンス・メトリクスを確認できます:

![An image showing the Roles section of the Performance Blade of Azure Application Insights](../../../../../../media/fitness-store-roles-in-performance-blade.jpg)

SQL 呼び出しをクリックすると、エンドツーエンドのトランザクションを確認できます:

![An image showing the end-to-end transaction of a SQL call](../../../../../../media/fitness-store-end-to-end-transaction-details.jpg)

`Failures` で `Exceptions` パネルに移動します - ここでは、例外の一覧を確認できます:

![An image showing application failures graphed](../../../../../../media/fitness-store-exceptions.jpg)

`Metrics` タブに移動します - ここでは Spring Boot アプリ、Spring Cloud モジュール、および依存関係によって提供されるメトリクスを確認できます。以下のチャートは、`http_server_requests` と `Heap Memory Used` を表示しています。

![An image showing metrics over time](../../../../../../media/metrics.jpg)

Spring Boot は多くのコア・メトリクスを登録します: JVM、CPU、Tomcat、Logbackなど...
Spring Boot　の auto-configuration は、Spring MVC で処理するリクエストの計測を可能にします。
REST コントローラーの `ProductController` と `PaymentController` は、クラスレベルで指定するMicrometer の `@Timed` アノテーションで計測します。

* `acme-catalog` アプリケーションは下記のカスタム・メトリクスを有効にしています:
  * @Timed: `store.products`
* `acem-payment`アプリケーションは下記のカスタム・メトリクスを有効にしています:
  * @Timed: `store.payment`

これらのカスタム・メトリクスは `Metrics` タブで確認できます:

![An image showing custom metrics instrumented by Micrometer](../../../../../../media/fitness-store-custom-metrics-with-payments-2.jpg)

`Live Metrics` タブに移動します - ここでは、遅延が 1 秒未満のライブ・メトリクスを画面上で確認できます:"

![An image showing the live metrics of all applications](../../../../../../media/live-metrics.jpg)

⬅️ 前の作業: [13 - ハンズオン・ラボ 3.5 - Azure KeyVault の設定](../13-hol-3.5-configure-azure-keyvault/README.md)

➡️ 次の作業: [15 - ハンズオン・ラボ 4.2 - ログ](../15-hol-4.2-logging/README.md)