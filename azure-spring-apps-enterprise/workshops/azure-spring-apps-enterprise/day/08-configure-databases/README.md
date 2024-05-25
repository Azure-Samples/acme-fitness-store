# 08 - Configure Databases - OPTIONAL

This is an optional lab that you can skip if you are running out of time in this workshop.

By default, several services use in-memory data storage. In this lab, we will create persistent storage outside the applications and connect the apps.

## Prepare Database Scripts

* Make sure you are operating under `labs/acme-fitness-store/azure-spring-apps-enterprise/scripts` folder, e.g.

```shell
cd labs/acme-fitness-store/azure-spring-apps-enterprise/scripts
```

* You can see the current directory with `pwd`, e.g.

```shell
pwd
```

```text
/home/asa-student/labs/acme-fitness-store/azure-spring-apps-enterprise/scripts
```

* Create a bash script with database environment variables by making a copy of the existing template, e.g. 

```shell
cp ./setup-db-env-variables-template.sh ./setup-db-env-variables.sh
```

* Edit the file, use your favourite editor, or just built-in `code` in Azure Cloud Shell, e.g.

```shell
code ./setup-db-env-variables.sh
```

![Editing setup-db-env-variables.sh file in VS Code in Azure Cloud Shell](./images/setup-db-env-variables.png)

* Enter the following information, e.g.

```text
export AZURE_CACHE_NAME='asae-student01'                   # Unique name for Azure Cache for Redis Instance
export POSTGRES_SERVER='asae-student01'                    # Unique name for Azure Database for PostgreSQL Flexible Server
export POSTGRES_SERVER_USER='asae_student01'               # Postgres server username to be created in next steps
export POSTGRES_SERVER_PASSWORD='ASAEstudent01'            # Postgres server password to be created in next steps

export CART_SERVICE_CACHE_CONNECTION='cart_service_cache'
export ORDER_SERVICE_DB='acmefit_order'
export ORDER_SERVICE_DB_CONNECTION='order_service_db'
export CATALOG_SERVICE_DB='acmefit_catalog'
export CATALOG_SERVICE_DB_CONNECTION='catalog_service_db'

echo $AZURE_CACHE_NAME
echo $POSTGRES_SERVER
echo $POSTGRES_SERVER_USER
echo $POSTGRES_SERVER_PASSWORD
```

* Load the db environment variables, e.g.

```shell
source ./setup-db-env-variables.sh
```

## Create Azure SQL for PostgreSQL database server

* First, we need to create Azure Database for PostgreSQL server (this will take 5+ minutes to complete), e.g.

```shell
az postgres flexible-server create \
    --name ${POSTGRES_SERVER} \
    --resource-group ${RESOURCE_GROUP} \
    --location ${REGION} \
    --admin-user ${POSTGRES_SERVER_USER} \
    --admin-password ${POSTGRES_SERVER_PASSWORD} \
    --public-access 0.0.0.0 \
    --tier Burstable \
    --sku-name Standard_B1ms \
    --version 14 \
    --storage-size 32 \
    --yes \
    --verbose
```
* Observe the output and connection string, e.g.

```text
Checking the existence of the resource group 'asae-student01'...
Resource group 'asae-student01' exists ? : True 
Creating PostgreSQL Server 'asae-student01' in group 'asae-student01'...
Your server 'asae-student01' is using sku 'Standard_B1ms' (Paid Tier). Please refer to https://aka.ms/postgres-pricing for pricing details
Configuring server firewall rule, 'azure-access', to accept connections from all Azure resources...
Creating PostgreSQL database 'flexibleserverdb'...
Make a note of your password. If you forget, you would have to reset your password with "az postgres flexible-server update -n asae-student01 -g asae-student01 -p <new-password>".
Try using 'az postgres flexible-server connect' command to test out connection.

{
  "connectionString": "postgresql://asae_student01:ASAEstudent01@asae-student01.postgres.database.azure.com/flexibleserverdb?sslmode=require",
  "databaseName": "flexibleserverdb",
  "firewallName": "AllowAllAzureServicesAndResourcesWithinAzureIps_2024-5-20_19-24-31",
  "host": "asae-student01.postgres.database.azure.com",
  "id": "/subscriptions/00001111-aaaa-bbbb-cccc-ddddeeeeffff/resourceGroups/asae-student01/providers/Microsoft.DBforPostgreSQL/flexibleServers/asae-student01",
  "location": "West Europe",
  "password": "ASAEstudent01",
  "resourceGroup": "asae-student01",
  "skuname": "Standard_B1ms",
  "username": "asae_student01",
  "version": "16"
}

Command ran in 317.476 seconds (init: 0.227, invoke: 317.249)
```

### Configure UUID-OSSP extension

* We need to enable `uuid-ossp` extension on PostgreSQL server, e.g.

```shell
az postgres flexible-server parameter set \
    --resource-group ${RESOURCE_GROUP} \
    --server-name ${POSTGRES_SERVER} \
    --name azure.extensions --value uuid-ossp \
    --verbose
```

### Create PostgreSQL databases

* We need to create two databases on the PostgreSQL server for two applications `Order` and `Catalog`, e.g.

```shell
az postgres flexible-server db create \
    --server-name ${POSTGRES_SERVER} \
    --database-name ${ORDER_SERVICE_DB} \
    --verbose
```

```shell
az postgres flexible-server db create \
    --server-name ${POSTGRES_SERVER} \
    --database-name ${CATALOG_SERVICE_DB} \
    --verbose
```

> Note: each database should take only 10+ seconds to create.


## Create Azure Cache for Redis database

* First, we need to create Azure Cache for Redis server (this will take 5+ minutes to complete), e.g.

```shell
az redis create \
    --name ${AZURE_CACHE_NAME} \
    --resource-group ${RESOURCE_GROUP} \
    --location ${REGION} \
    --sku Basic \
    --vm-size c0 \
    --verbose
```

* Observe the output and connection string, e.g.

```text
Configured default 'asae-student01' for arg resource_group_name
Configured default 'westeurope' for arg location
 - Running ..
{
  "accessKeys": null,
  "enableNonSslPort": false,
  "hostName": "asae-student01.redis.cache.windows.net",
  "id": "/subscriptions/00001111-aaaa-bbbb-cccc-ddddeeeeffff/resourceGroups/asae-student01/providers/Microsoft.Cache/Redis/asae-student01",
  "identity": null,
  "instances": [
    {
      "isMaster": true,
      "isPrimary": true,
      "nonSslPort": null,
      "shardId": null,
      "sslPort": 15000,
      "zone": null
    }
  ],
  "linkedServers": [],
  "location": "West Europe",
  "minimumTlsVersion": null,
  "name": "asae-student01",
  "port": 6379,
  "privateEndpointConnections": null,
  "provisioningState": "Succeeded",
  "publicNetworkAccess": "Enabled",
  "redisConfiguration": {
    "aadEnabled": null,
    "additionalProperties": null,
    "aofBackupEnabled": null,
    "aofStorageConnectionString0": null,
    "aofStorageConnectionString1": null,
    "authnotrequired": null,
    "maxclients": "256",
    "maxfragmentationmemoryReserved": "30",
    "maxmemoryDelta": "30",
    "maxmemoryPolicy": null,
    "maxmemoryReserved": "30",
    "preferredDataArchiveAuthMethod": null,
    "preferredDataPersistenceAuthMethod": null,
    "rdbBackupEnabled": null,
    "rdbBackupFrequency": null,
    "rdbBackupMaxSnapshotCount": null,
    "rdbStorageConnectionString": null,
    "storageSubscriptionId": null,
    "zonalConfiguration": null
  },
  "redisVersion": "6.0",
  "replicasPerMaster": null,
  "replicasPerPrimary": null,
  "resourceGroup": "asae-student01",
  "shardCount": null,
  "sku": {
    "capacity": 0,
    "family": "C",
    "name": "Basic"
  },
  "sslPort": 6380,
  "staticIp": null,
  "subnetId": null,
  "tags": {},
  "tenantSettings": {},
  "type": "Microsoft.Cache/Redis",
  "updateChannel": "Stable",
  "zones": null
}
Command ran in 968.595 seconds (init: 0.219, invoke: 968.376)
```

## Create Azure Database for PostgreSQL Service Connector

* The `Order` and `Catalog` microservices use Azure Database for PostgreSQL. We need to create a Service Connector for these two applications.

* Let's connect `Order` service (.NET) first, e.g.

```shell
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
    --client-type dotnet \
    --verbose
```

> Note: database connection creation will take 30-40s to complete.

* Next, we will connect `Catalog` service (Spring Boot), that uses Microsoft Entra authentication to connect to PostgreSQL, so it is not required to include the password, e.g.

```shell
az spring connection create postgres-flexible \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --connection ${CATALOG_SERVICE_DB_CONNECTION} \
    --app ${CATALOG_SERVICE_APP} \
    --deployment default \
    --tg ${RESOURCE_GROUP} \
    --server ${POSTGRES_SERVER} \
    --database ${CATALOG_SERVICE_DB} \
    --client-type springboot \
    --system-identity \
    --verbose
```

* Observe the [output](output-create-postgres-connection-passwordless.txt). The above command might take 5-6 minutes to complete.

> Note: If `serviceconnector-passwordless` extension has not already installed, it will be installed next.
> After executing above command, the Azure Spring App application enables System assigned managed identity.
> PostgreSQL database user will be created and assigned to the managed identity and permissions will be granted to that user.

## Create Azure Cache for Redis Service Connector

* The `Cart` microservice use Azure Cache for Redis. We need to create a Service Connector for this application, e.g.

```shell
az spring connection create redis \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --connection ${CART_SERVICE_DB_CONNECTION} \
    --app ${CART_SERVICE_APP} \
    --deployment default \
    --tg ${RESOURCE_GROUP} \
    --server ${AZURE_CACHE_NAME} \
    --database 0 \
    --client-type python \
    --verbose
```

> Note: Currently, the Azure Spring Apps CLI extension only allows for client types of Java, springboot, or dotnet.
> The cart service uses a client connection of type Java because the connection strings are the same for python and Java.
> This will be changed when additional options become available in the CLI.

## Update Applications

* Now that we configured all three database connections, we need to restart the applications.

### Restart Catalog Application

```shell
az spring app restart --name ${CATALOG_SERVICE_APP}
```

### Configure Order Service with PostgreSQL details

* First need to get the PostgreSQL connection string, e.g.

```shell
export POSTGRES_CONNECTION_STR=$(az spring connection show \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --deployment default \
    --connection ${ORDER_SERVICE_DB_CONNECTION} \
    --app ${ORDER_SERVICE_APP} \
    --query configurations[].value \
    -o tsv)"Trust Server Certificate=true;"
```

* Then, update `Order` service application environment variables, e.g.

```
az spring app update \
    --name order-service \
    --env "DatabaseProvider=Postgres" \
    "ConnectionStrings__OrderContext=${POSTGRES_CONNECTION_STR}" \
    "AcmeServiceSettings__AuthUrl=https://${GATEWAY_URL}" \
    --verbose
```

### Configure Cart Service with Redis details

* First need to get the Redis connection string, e.g.

```shell
export REDIS_CONN_STR=$(az spring connection show \
    --resource-group ${RESOURCE_GROUP} \
    --service ${SPRING_APPS_SERVICE} \
    --deployment default \
    --connection ${CART_SERVICE_CACHE_CONNECTION} \
    --app ${CART_SERVICE_APP} \
    --query configurations[0].value \
    -o tsv)
```

* Then, update `Cart` service application enviornment variables, e.g.

```shell
az spring app update \
    --name cart-service \
    --env "CART_PORT=8080" \
    "REDIS_CONNECTIONSTRING=${REDIS_CONN_STR}" \
    "AUTH_URL=https://${GATEWAY_URL}" \
    --verbose
```

### Restart Cart Application

```shell
az spring app restart --name ${CART_SERVICE_APP}
```

### Restart Order Application 

```shell
az spring app restart --name ${ORDER_SERVICE_APP}
```

## Next Guide

Next guide - [09 - Configure Azure OpenAI Services](../09-configure-azure-openai-services/README.md)
