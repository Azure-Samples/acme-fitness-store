本作業では、アプリケーションとは別の外部に、永続的なストア（Postgres、Azure Cache for Redis）を作成し、それらに対してアプリケーションから接続します。[03 - ワークショップ用の環境セットアップ](../03-workshop-environment-setup/acmedeploy.json) で実行した ARM テンプレートにより、Postgres Serverインスタンス と Azure Cache for Redis インスタンスを作成しました。

この作業が終了すると、下記のようなアーキテクチャになります。
![architecture](images/postgres-redis.png) 

## 1. 環境の準備

提供されるテンプレートをコピーして、環境変数の設定を含む bash スクリプトを作成します。

```shell
cd /workspaces/acme-fitness-store/azure-spring-apps-enterprise
cp ./scripts/setup-db-env-variables-template.sh ./scripts/setup-db-env-variables.sh
```

次に、デフォルトの Postgres Server のパスワードを変更するため、新規パスワードを作成します

```
POSTGRE_PASSWORD='!'$(head -c 12 /dev/urandom | base64 | tr -dc '[:alpha:]'| 
fold -w 8 | head -n 1)$RANDOM
echo $POSTGRE_PASSWORD
!NEW_RANDOM_PASSWORD
```

> ご注意： デフォルトのパスワードは ARM テンプレートの administratorLoginPassword パラメーターで設定されており、Azure Portal でデプロイする際に変更することもできます。

`./scripts/setup-db-env-variables.sh` ファイルを開いて、下記の情報を入力します。

```shell
export AZURE_CACHE_NAME=acme-fitness-cache-CHANGE-ME                 # Azure Cache for Redis インスタンス用の一意な名前。CHANGE_ME の箇所を作業 3 の ARM テンプレートで作成した 4 つのユニークな文字で置き換えてください。 [03 - ワークショップ用の環境セットアップ](../03-workshop-environment-setup/README.md)           

export POSTGRES_SERVER=acme-fitness-db-CHANGE-ME                 # Azure Database for PostgreSQL Flexible Server 用の一意な名前。CHANGE_ME の箇所を作業 3 の ARM テンプレートで作成された 4 つのユニークな文字に置き換えてください。

export POSTGRES_SERVER_USER='acme'             # Postgres server への接続ユーザ名を設定します
export POSTGRES_SERVER_PASSWORD='!NEW_RANDOM_PASSWORD'         # Postgres server への接続パスワードを設定します
```

上記の変数値は、ARM テンプレートで作成されたリソース・グループに直接アクセスして確認することもできます。これにより、そのリソース。グループ内の全リソースが表示され、データベースとキャッシュも一覧に表示されます。

次に、環境変数の設定を有効にします。

```shell
source ./scripts/setup-db-env-variables.sh
```

次に上記で設定したパスワードに更新します。

```shell
az postgres flexible-server update --name ${POSTGRES_SERVER} --admin-password ${POSTGRES_SERVER_PASSWORD}
```

### 1.1. Postgresql DB に対し他の Azure リソースから接続許可を付与

下記のコマンドを実行して、Postgresql DB に対し他の Azure リソースからの接続できるようにファイアウォールルールの設定を行います。
さらに、Postgresql DB のテーブル内で UUID を扱えるように `uuid-ossp` 拡張機能を有効にします。

```shell
az postgres flexible-server firewall-rule create --rule-name allAzureIPs \
     --name ${POSTGRES_SERVER} \
     --resource-group ${RESOURCE_GROUP} \
     --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0
     
### Enable the uuid-ossp extension
az postgres flexible-server parameter set \
    --resource-group ${RESOURCE_GROUP} \
    --server-name ${POSTGRES_SERVER} \
    --name azure.extensions --value uuid-ossp
```

## 2. サービス用のデータベースを作成

下記のコマンドを実行し、注文サービス用のデータベースを作成します。

```shell
az postgres flexible-server db create \
  --database-name ${ORDER_SERVICE_DB} \
  --server-name ${POSTGRES_SERVER}
```

下記のコマンドを実行し、カタログサービス用のデータベースを作成します。

```shell
az postgres flexible-server db create \
  --database-name ${CATALOG_SERVICE_DB} \
  --server-name ${POSTGRES_SERVER}
```

次に日本語の環境設定を行います

```shell
# Azure PostgreSQL Flexible Server DB の日本語設定    
az postgres flexible-server parameter set \
    --server-name ${POSTGRES_SERVER} \
    --name lc_monetary --value "ja_JP.utf-8"
# Azure PostgreSQL Flexible Server DB の日本語設定    
az postgres flexible-server parameter set \
    --server-name ${POSTGRES_SERVER}  \
    --name lc_numeric --value "ja_JP.utf-8"
# Azure PostgreSQL Flexible Server DB のタイムゾーン設定    
az postgres flexible-server parameter set \
    --server-name ${POSTGRES_SERVER}\
    --name timezone --value "Asia/Tokyo"  
```

> ご注意：上記の全ての処理が完了した後、以降の処理を行なってください。

## 3. サービスコネクターの作成

注文サービスとカタログサービスは Azure Database for Postgres で作成した DB を使用します。そこで、これらのアプリケーションに対して Postgres 用のサービスコネクターを作成します。

```shell
# Postgres に注文サービスをバインド
az spring connection create postgres-flexible \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --connection ${ORDER_SERVICE_DB_CONNECTION} \
    --app ${ORDER_SERVICE_APP} \
    --deployment default \
    --tg ${RESOURCE_GROUP} \
    --server ${POSTGRES_SERVER} \
    --database ${ORDER_SERVICE_DB} \
    --secret name=${POSTGRES_SERVER_USER} secret=${POSTGRES_SERVER_PASSWORD} \
    --client-type dotnet
    

# Postgres にカタログ・サービスをバインド
az spring connection create postgres-flexible \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --connection ${CATALOG_SERVICE_DB_CONNECTION} \
    --app ${CATALOG_SERVICE_APP} \
    --deployment default \
    --tg ${RESOURCE_GROUP} \
    --server ${POSTGRES_SERVER} \
    --database ${CATALOG_SERVICE_DB} \
    --secret name=${POSTGRES_SERVER_USER} secret=${POSTGRES_SERVER_PASSWORD} \
    --client-type springboot
```

次に、カート・サービスは Azure Cache for Redis に接続します。そこで、Redis 用のサービスコネクターを作成します。

```shell
az spring connection create redis \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --connection $CART_SERVICE_CACHE_CONNECTION \
    --app ${CART_SERVICE_APP} \
    --deployment default \
    --tg ${RESOURCE_GROUP} \
    --server ${AZURE_CACHE_NAME} \
    --database 0 \
    --client-type java 
```

> ご注意：現在、Azure Spring Apps CLI 拡張機能は Java、Spring Boot、.NET のクライアント・タイプのみをサポートしています。
> カート・サービスは、Python で実装していますが、Python と Java の接続文字列は同一のため、今回は例外的に Java のクライアントの接続タイプとして使用しています。
> Azure CLI で追加のオプションが利用可能になった際、これは変更される可能性があります。

## 4. アプリケーションの更新

次に、アプリケーションから新規作成したデータベースや Redis キャッシュを使用できるようにするため、該当するアプリケーションを更新します。

サービスコネクターを有効化するために、カタログサービスを再起動します：

```shell
az spring app restart --name ${CATALOG_SERVICE_APP}
```

PostgreSQL の接続文字列を取得し、注文サービスを更新します：

```shell
POSTGRES_CONNECTION_STR=$(az spring connection show \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --deployment default \
    --connection ${ORDER_SERVICE_DB_CONNECTION} \
    --app ${ORDER_SERVICE_APP} | jq '.configurations[0].value' -r)

az spring app update \
    --name order-service \
    --env "DatabaseProvider=Postgres" "ConnectionStrings__OrderContext=${POSTGRES_CONNECTION_STR}" "AcmeServiceSettings__AuthUrl=https://${GATEWAY_URL}"
```

Redis の接続文字列を取得し、カートサービスを更新します：

```shell
REDIS_CONN_STR=$(az spring connection show \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --deployment default \
    --app ${CART_SERVICE_APP} \
    --connection ${CART_SERVICE_CACHE_CONNECTION} | jq -r '.configurations[0].value')

az spring app update \
    --name cart-service \
    --env "CART_PORT=8080" "REDIS_CONNECTIONSTRING=${REDIS_CONN_STR}" "AUTH_URL=https://${GATEWAY_URL}"
```

⬅️ 前の作業: [10 - ハンズオン・ラボ 3.2 - Azure Config Service と Service Registry にアプリをバインド](../10-hol-3.2-bind-apps-to-acs-service-reg/README.md)

➡️ 次の作業: [12 - ハンズオン・ラボ 3.4 シングル・サイン・オンの設定](../12-hol-3.4-configure-single-signon/README.md)