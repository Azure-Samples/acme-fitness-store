# Setup Azure Container Apps using `az spring export`

## Introduction

When many applications are running on the Azure Spring Apps service, you need to set up multiple resources and configure many settings. This document explains how to use Azure CLI command `az spring export` to generate Bicep files from an existing Azure Spring Apps instance.

## Prerequisites

- Azure CLI is available locally and the spring extension version > `1.27`. (Ensure the `az spring export` command is available)
- Docker tools with WSL are available locally.
- The Fitness Store source code [repository](https://github.com/Azure-Samples/acme-fitness-store.git) is accessible.
- The Fitness Store has already been deployed on an Azure Spring Apps instance successfully. Refer to this [document](https://github.com/Azure-Samples/acme-fitness-store/tree/Azure/azure-spring-apps-enterprise) for guidance on setting up the Azure Spring Apps instance.

## Prepare resources

### 1. Create resource group for target
```shell
RESOURCE_GROUP='<migrate-to-resource-group>'
SUBSCRIPTION='<subscription-id>'
LOCATION='<location>'

az group create -n $RESOURCE_GROUP --subscription $SUBSCRIPTION --location $LOCATION
```
### 2. Create Azure Container Registry resource
```shell
# ACR and image tags
PREFIX='<prefix>'    
ACR_NAME=${PREFIX}acr

# Create ACR
az acr create \
    -g ${RESOURCE_GROUP} \
    -n ${ACR_NAME} \
    --subscription ${SUBSCRIPTION} \
    --admin-enabled \
    --sku Premium 
```

## Prepare Fitness Store images
### 1. Get source code
```shell
git clone https://github.com/Azure-Samples/acme-fitness-store.git
cd acme-fitness-store
```

### 2. Change configurations
**Changes in project `acme-catalog`**

- **Update configuration file**: Modify `apps/acme-catalog/src/main/resources/application.yaml` to add config-server support.
```yaml
 spring:
+  config:
+    import: optional:configserver:http://config-server:8888
+  cloud:
+    config:
+      name: catalog
   datasource:
     url: jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
   jpa:
```

- **Update dependencies**: Modify `apps/acme-catalog/build.gradle` to include the Spring Cloud Config starter dependency.
```yaml
        implementation 'org.flywaydb:flyway-core'

        implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
   +    implementation 'org.springframework.cloud:spring-cloud-starter-config'

        implementation 'com.azure.spring:spring-cloud-azure-starter-keyvault-secrets'
```

**Changes in project `acme-payment`**

- **Update configuration file**: Modify `/apps/acme-payment/src/main/resources/application.yml` to add config-server support.
```yaml
+spring:
+  config:
+    import: optional:configserver:http://config-server:8888
+  cloud:
+    config:
+      name: payment
 management:
   endpoints:
     web:
```

- **Update dependencies**: Modify `apps/acme-payment/build.gradle` to include the Spring Cloud Config starter dependency.
```yaml
        implementation 'org.springframework.boot:spring-boot-starter-webflux'

        implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
   +    implementation 'org.springframework.cloud:spring-cloud-starter-config'

        runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
```


### 3. Install Pack tools (on Ubuntu)
```shell
sudo add-apt-repository ppa:cncf-buildpacks/pack-cli
sudo apt-get update
sudo apt-get install pack-cli
```
Refer to [Pack · Cloud Native Buildpacks](https://buildpacks.io/docs/for-platform-operators/how-to/integrate-ci/pack/) for other platform

### 4. Build Images on Local
> Note: Make sure the configurations in source code have been changed before building image.
```shell
ACR_LOGIN_SERVER=${ACR_NAME}.azurecr.io
APP_IMAGE_TAG="latest"

CATALOG_SERVICE_APP='acme-catalog'
PAYMENT_SERVICE_APP='acme-payment'
ORDER_SERVICE_APP='acme-order'
CART_SERVICE_APP='acme-cart'
FRONTEND_APP='frontend'
IDENTITY_SERVICE_APP='acme-identity'

# Build Catalog Service
pack build ${ACR_LOGIN_SERVER}/${CATALOG_SERVICE_APP}:${APP_IMAGE_TAG} \
    --path apps/acme-catalog \
    --builder paketobuildpacks/builder-jammy-base \
    -e BP_JVM_VERSION=17

# Build Payment Service
pack build ${ACR_LOGIN_SERVER}/${PAYMENT_SERVICE_APP}:${APP_IMAGE_TAG} \
    --path apps/acme-payment \
    --builder paketobuildpacks/builder-jammy-base \
    -e BP_JVM_VERSION=17

# Build Order Service
pack build ${ACR_LOGIN_SERVER}/${ORDER_SERVICE_APP}:${APP_IMAGE_TAG} \
    --path apps/acme-order \
    --builder paketobuildpacks/builder-jammy-base

# Build Cart Service
pack build ${ACR_LOGIN_SERVER}/${CART_SERVICE_APP}:${APP_IMAGE_TAG} \
    --path apps/acme-cart \
    --builder paketobuildpacks/builder-jammy-base

# Build Frontend App
pack build ${ACR_LOGIN_SERVER}/${FRONTEND_APP}:${APP_IMAGE_TAG} \
    --path apps/acme-shopping \
    --builder paketobuildpacks/builder-jammy-base

# Build Identity Service
pack build ${ACR_LOGIN_SERVER}/${IDENTITY_SERVICE_APP}:${APP_IMAGE_TAG} \
    --path apps/acme-identity \
    --builder paketobuildpacks/builder-jammy-base \
    -e BP_JVM_VERSION=17
```
### 5. Push images to ACR
```shell
# Login ACR
az acr login \
    -n ${ACR_NAME} \
    --subscription ${SUBSCRIPTION} \
    -g ${RESOURCE_GROUP}

# Push Images to ACR
docker push ${ACR_LOGIN_SERVER}/${CATALOG_SERVICE_APP}:${APP_IMAGE_TAG}
docker push ${ACR_LOGIN_SERVER}/${PAYMENT_SERVICE_APP}:${APP_IMAGE_TAG}
docker push ${ACR_LOGIN_SERVER}/${ORDER_SERVICE_APP}:${APP_IMAGE_TAG}
docker push ${ACR_LOGIN_SERVER}/${CART_SERVICE_APP}:${APP_IMAGE_TAG}
docker push ${ACR_LOGIN_SERVER}/${FRONTEND_APP}:${APP_IMAGE_TAG}
docker push ${ACR_LOGIN_SERVER}/${IDENTITY_SERVICE_APP}:${APP_IMAGE_TAG}
```

## Migrate Fitness Store from Azure Spring Apps to Azure Container Apps
### 1. Generate Migrate bicep script through CLI
```shell
SOURCE_ASA_NAME=fitness-store
SOURCE_ASA_RESOURCE_GROUP=fitness-store
SOURCE_SUBSCRIPTION=d51e3ffe-6b84-49cd-b426-0dc4ec660356
OUTPUT_FOLDER='.\output\fitness-store'

az spring export \
       --service $SOURCE_ASA_NAME \
       --resource-group $SOURCE_ASA_RESOURCE_GROUP \
       --subscription $SOURCE_SUBSCRIPTION \
       --output-folder $OUTPUT_FOLDER --verbose --debug
```
### 2. Run script to generate Azure Container Apps resource
```shell
az deployment group create \
        --resource-group $RESOURCE_GROUP \
        --subscription $SUBSCRIPTION \
        --template-file ${OUTPUT_FOLDER}\main.bicep \
        --parameters ${OUTPUT_FOLDER}\param.bicepparam
```
> Note: Just re-run the script once you get return code:`JavaComponentOperationError` with message `Failed to create config map external-auth-config-map for JavaComponent '' in k8se-system namespace.` This is a known limitation of Azure Container Apps due to some incompatible status issue. Check out `README.md` file in generated script for more guidance on further steps.

### 3. Update image URL with target port

Update image URL from `mcr.microsoft.com/azuredocs/containerapps-helloworld:latest` to the corresponding image URL in ACR we created.
Get the ACR password from the page `Settings` > `Access keys` on portal.
```shell
ACR_PASSWORD = '<ACR access key>'
CATALOG_SERVICE='catalog-service'
PAYMENT_SERVICE='payment-service'
ORDER_SERVICE='order-service'
CART_SERVICE='cart-service'
FRONTEND='frontend'
IDENTITY_SERVICE='identity-service'
az containerapp up \
        --name ${CATALOG_SERVICE} \
        --environment ${SOURCE_ASA_NAME} \
        --image ${ACR_LOGIN_SERVER}/${CATALOG_SERVICE_APP}:${APP_IMAGE_TAG} \
        --resource-group ${RESOURCE_GROUP} \
        --subscription ${SUBSCRIPTION} \
        --registry-server ${ACR_LOGIN_SERVER} \
        --registry-username ${ACR_NAME} \
        --registry-password ${ACR_PASSWORD} \
        --ingress internal \
        --target-port 8080
az containerapp up \
        --name ${PAYMENT_SERVICE} \
        --environment ${SOURCE_ASA_NAME} \
        --image ${ACR_LOGIN_SERVER}/${PAYMENT_SERVICE_APP}:${APP_IMAGE_TAG} \
        --resource-group ${RESOURCE_GROUP} \
        --subscription ${SUBSCRIPTION} \
        --registry-server ${ACR_LOGIN_SERVER} \
        --registry-username ${ACR_NAME} \
        --registry-password ${ACR_PASSWORD} \
        --ingress internal \
        --target-port 8080
az containerapp up \
        --name ${ORDER_SERVICE} \
        --environment ${SOURCE_ASA_NAME} \
        --image ${ACR_LOGIN_SERVER}/${ORDER_SERVICE_APP}:${APP_IMAGE_TAG} \
        --resource-group ${RESOURCE_GROUP} \
        --subscription ${SUBSCRIPTION} \
        --registry-server ${ACR_LOGIN_SERVER} \
        --registry-username ${ACR_NAME} \
        --registry-password ${ACR_PASSWORD} \
        --ingress internal \
        --target-port 8080
az containerapp up \
        --name ${CART_SERVICE} \
        --environment ${SOURCE_ASA_NAME} \
        --image ${ACR_LOGIN_SERVER}/${CART_SERVICE_APP}:${APP_IMAGE_TAG} \
        --resource-group ${RESOURCE_GROUP} \
        --subscription ${SUBSCRIPTION} \
        --registry-server ${ACR_LOGIN_SERVER} \
        --registry-username ${ACR_NAME} \
        --registry-password ${ACR_PASSWORD} \
        --ingress internal \
        --target-port 8080
az containerapp up \
        --name ${FRONTEND} \
        --environment ${SOURCE_ASA_NAME} \
        --image ${ACR_LOGIN_SERVER}/${FRONTEND_APP}:${APP_IMAGE_TAG} \
        --resource-group ${RESOURCE_GROUP} \
        --subscription ${SUBSCRIPTION} \
        --registry-server ${ACR_LOGIN_SERVER} \
        --registry-username ${ACR_NAME} \
        --registry-password ${ACR_PASSWORD} \
        --ingress internal \
        --target-port 8080
az containerapp up \
        --name ${IDENTITY_SERVICE} \
        --environment ${SOURCE_ASA_NAME} \
        --image ${ACR_LOGIN_SERVER}/${IDENTITY_SERVICE_APP}:${APP_IMAGE_TAG} \
        --resource-group ${RESOURCE_GROUP} \
        --subscription ${SUBSCRIPTION} \
        --registry-server ${ACR_LOGIN_SERVER} \
        --registry-username ${ACR_NAME} \
        --registry-password ${ACR_PASSWORD} \
        --ingress internal \
        --target-port 8080
```

### 4. Correct the probe port
Change the health probe (liveness and readiness) port of following container apps from `80` to `8080` on the portal page `Application` > `Containers`.
- catalog-service
- payment-service
- order-service
- cart-service
- frontend
- identity-service
> Note: Currently, there is no CLI command available to enable the health probe for existing container apps. Health probe configurations require manual updates via the Azure portal.

## Verify the migrated Azure Container Apps resources
### Retrieve the Gateway URL
Use the following Azure CLI command to obtain the gateway URL:
```shell
az containerapp env java-component gateway-for-spring show \
        --environment ${SOURCE_ASA_NAME} \
        --resource-group ${RESOURCE_GROUP} \
        --name gateway \
        --subscription ${SUBSCRIPTION} \
        --query properties.ingress.fqdn \
        --output tsv
```
The command returns a URL in the following format:
```
gateway-azure-java.<random-value>.<region>.azurecontainerapps.io
```
> **Note:** The gateway URL is not displayed in the Azure Container Apps portal. You must use the CLI command above to retrieve it.

### Access the Fitness Store Application
Open your browser and navigate to the URL obtained above:
```
https://gateway-azure-java.<random-value>.<region>.azurecontainerapps.io
```
Verify that the Fitness Store application loads correctly.
