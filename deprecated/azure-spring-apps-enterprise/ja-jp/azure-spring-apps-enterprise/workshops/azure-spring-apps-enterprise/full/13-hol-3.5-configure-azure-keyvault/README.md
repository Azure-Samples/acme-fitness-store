ここでは、Azure Key Vault を使用して Azure サービスに接続するための機密情報を安全に保存し、読み込みます。

このセクションが完了すると、アーキテクチャは下記のようになります：
![architecture](images/key-vault.png) 

## 1. 環境を準備

Key Vault の URI を取得します。

```shell
export KEYVAULT_URI=$(az keyvault show --name ${KEY_VAULT} -g $RESOURCE_GROUP | jq -r '.properties.vaultUri')
echo $KEYVAULT_URI
```
## 2. Key Vault　にデータベース接続用の接続情報を保存

```shell
export POSTGRES_SERVER_FULL_NAME="${POSTGRES_SERVER}.postgres.database.azure.com"

az keyvault secret set --vault-name ${KEY_VAULT} \
    --name "POSTGRES-SERVER-NAME" --value ${POSTGRES_SERVER_FULL_NAME}

az keyvault secret set --vault-name ${KEY_VAULT} \
    --name "ConnectionStrings--OrderContext" \
    --value "Server=${POSTGRES_SERVER_FULL_NAME}; \
             Database=${ORDER_SERVICE_DB};Port=5432; \
             SSL Mode=Require;Trust Server Certificate=true; \
             User ID=${POSTGRES_SERVER_USER}; \
             Password=${POSTGRES_SERVER_PASSWORD}"
    
az keyvault secret set --vault-name ${KEY_VAULT} \
    --name "CATALOG-DATABASE-NAME" --value ${CATALOG_SERVICE_DB}
    
az keyvault secret set --vault-name ${KEY_VAULT} \
    --name "POSTGRES-LOGIN-NAME" --value ${POSTGRES_SERVER_USER}
    
az keyvault secret set --vault-name ${KEY_VAULT} \
    --name "POSTGRES-LOGIN-PASSWORD" --value ${POSTGRES_SERVER_PASSWORD}
```

## 3. Key Vault　に　Redis 接続用の接続情報を保存

```shell
az redis show -n ${AZURE_CACHE_NAME} > redis.json
export REDIS_HOST=$(cat redis.json | jq -r '.hostName')
export REDIS_PORT=$(cat redis.json | jq -r '.sslPort')

export REDIS_PRIMARY_KEY=$(az redis list-keys -n ${AZURE_CACHE_NAME} | jq -r '.primaryKey')

az keyvault secret set --vault-name ${KEY_VAULT} \
  --name "CART-REDIS-CONNECTION-STRING" --value "rediss://:${REDIS_PRIMARY_KEY}@${REDIS_HOST}:${REDIS_PORT}/0"  
```

## 4. Key Vault　に　SSO の機密情報を保存

```shell
az keyvault secret set --vault-name ${KEY_VAULT} \
    --name "SSO-PROVIDER-JWK-URI" --value ${JWK_SET_URI}
```

> ご注意：シングル・サイン・オンを構成していない場合、SSO-PROVIDER-JWK-URI の作成はスキップできます。

## 5. アプリケーションに対しシステム割り当ての Managed ID を有効化し、ID　を環境変数にエクスポート

```shell
az spring app identity assign --name ${CART_SERVICE_APP}
export CART_SERVICE_APP_IDENTITY=$(az spring app show --name ${CART_SERVICE_APP} | jq -r '.identity.principalId')

az spring app identity assign --name ${ORDER_SERVICE_APP}
export ORDER_SERVICE_APP_IDENTITY=$(az spring app show --name ${ORDER_SERVICE_APP} | jq -r '.identity.principalId')

az spring app identity assign --name ${CATALOG_SERVICE_APP}
export CATALOG_SERVICE_APP_IDENTITY=$(az spring app show --name ${CATALOG_SERVICE_APP} | jq -r '.identity.principalId')

az spring app identity assign --name ${IDENTITY_SERVICE_APP}
export IDENTITY_SERVICE_APP_IDENTITY=$(az spring app show --name ${IDENTITY_SERVICE_APP} | jq -r '.identity.principalId')
```

## 6. Azure Key Vault にアクセスポリシーを設定し、Managed Identities から機密情報を読み取ることを許可

```shell
az keyvault set-policy --name ${KEY_VAULT} \
    --object-id ${CART_SERVICE_APP_IDENTITY} --secret-permissions get list
    
az keyvault set-policy --name ${KEY_VAULT} \
    --object-id ${ORDER_SERVICE_APP_IDENTITY} --secret-permissions get list

az keyvault set-policy --name ${KEY_VAULT} \
    --object-id ${CATALOG_SERVICE_APP_IDENTITY} --secret-permissions get list

az keyvault set-policy --name ${KEY_VAULT} \
    --object-id ${IDENTITY_SERVICE_APP_IDENTITY} --secret-permissions get list
```

> ご注意：[12 - ハンズオン・ラボ 3.4 シングル・サイン・オンの設定](../12-hol-3.4-configure-single-signon/README.md)を完了していない場合、Identity Service は存在していません。シングル・サイン・オンを設定していない場合、この設定をスキップしてください。

## 7. Azure Key Vault から機密情報を読み込むためのアプリケーション設定

Service Connectors の設定を削除し、Azure Key Vault から環境変数を使用してアプリケーションの機密情報を読み込むようにします。

```shell
az spring connection delete \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --connection ${ORDER_SERVICE_DB_CONNECTION} \
    --app ${ORDER_SERVICE_APP} \
    --deployment default \
    --yes 

az spring connection delete \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --connection ${CATALOG_SERVICE_DB_CONNECTION} \
    --app ${CATALOG_SERVICE_APP} \
    --deployment default \
    --yes 

az spring connection delete \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --connection ${CART_SERVICE_CACHE_CONNECTION} \
    --app ${CART_SERVICE_APP} \
    --deployment default \
    --yes 
    
az spring app update --name ${ORDER_SERVICE_APP} \
    --env "ConnectionStrings__KeyVaultUri=${KEYVAULT_URI}" "AcmeServiceSettings__AuthUrl=https://${GATEWAY_URL}" "DatabaseProvider=Postgres"

az spring app update --name ${CATALOG_SERVICE_APP} \
    --config-file-pattern catalog/default,catalog/key-vault \
    --env "SPRING_CLOUD_AZURE_KEYVAULT_SECRET_PROPERTY_SOURCES_0_ENDPOINT=${KEYVAULT_URI}" "SPRING_CLOUD_AZURE_KEYVAULT_SECRET_PROPERTY_SOURCES_0_NAME='acme-fitness-store-vault'"  "SPRING_PROFILES_ACTIVE=default,key-vault"
    
az spring app update --name ${IDENTITY_SERVICE_APP} \
    --config-file-pattern identity/default,identity/key-vault \
    --env "SPRING_CLOUD_AZURE_KEYVAULT_SECRET_PROPERTY_SOURCES_0_ENDPOINT=${KEYVAULT_URI}" "SPRING_CLOUD_AZURE_KEYVAULT_SECRET_PROPERTY_SOURCES_0_NAME='acme-fitness-store-vault'" "SPRING_PROFILES_ACTIVE=default,key-vault"
    
az spring app update --name ${CART_SERVICE_APP} \
    --env "CART_PORT=8080" "KEYVAULT_URI=${KEYVAULT_URI}" "AUTH_URL=https://${GATEWAY_URL}"
```

## 8. アプリケーションのテスト

アプリケーションにアクセス、カートにアイテムを追加し、注文を行い、すべてが期待通りに動作していることを確認してください。

⬅️ 前の作業: [12 - ハンズオン・ラボ 3.4 シングル・サイン・オンの設定](../12-hol-3.4-configure-single-signon/README.md)

➡️ 次の作業: [14 - ハンズオン・ラボ 4.1 - End-End Observability](../14-hol-4.1-end-to-end-observability/README.md)