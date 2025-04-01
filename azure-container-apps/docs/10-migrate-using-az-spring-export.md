# Set Up Azure Container Apps Using `az spring export`

## Introduction

When managing multiple applications on Azure Spring Apps, configuring resources and settings manually can be complex. This guide demonstrates how to simplify this process by using the Azure CLI command `az spring export` to generate Bicep files from an existing Azure Spring Apps instance, facilitating migration to Azure Container Apps.

## Prerequisites

Ensure the following prerequisites are met before proceeding:

- Azure CLI installed locally with the `spring` extension version `> 1.27` (confirm availability of the `az spring export` command).
- Docker tools and Windows Subsystem for Linux (WSL) installed locally.
- Access to the Fitness Store source code [repository](https://github.com/Azure-Samples/acme-fitness-store.git).
- A successfully deployed Fitness Store application on Azure Spring Apps. Refer to [this guide](https://github.com/Azure-Samples/acme-fitness-store/tree/Azure/azure-spring-apps-enterprise) for deployment instructions.

## Prepare Azure Resources

### 1. Create a Resource Group

```shell
RESOURCE_GROUP='<migrate-to-resource-group>'
SUBSCRIPTION='<subscription-id>'
LOCATION='<location>'

az group create -n $RESOURCE_GROUP --subscription $SUBSCRIPTION --location $LOCATION
```

### 2. Create an Azure Container Registry (ACR)

```shell
PREFIX='<prefix>'
ACR_NAME=${PREFIX}acr

az acr create \
        -g ${RESOURCE_GROUP} \
        -n ${ACR_NAME} \
        --subscription ${SUBSCRIPTION} \
        --admin-enabled \
        --sku Premium
```

## Prepare Fitness Store Images

### 1. Clone the Source Code

```shell
git clone https://github.com/Azure-Samples/acme-fitness-store.git
cd acme-fitness-store
```

### 2. Update Application Configurations

Make the following changes to enable Config Server support:

**`acme-catalog` application:**

- Update `apps/acme-catalog/src/main/resources/application.yaml`:

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

- Update dependencies in `apps/acme-catalog/build.gradle`:

```gradle
  implementation 'org.flywaydb:flyway-core'
  implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
+ implementation 'org.springframework.cloud:spring-cloud-starter-config'
  implementation 'com.azure.spring:spring-cloud-azure-starter-keyvault-secrets'
```

**`acme-payment` application:**

- Update `apps/acme-payment/src/main/resources/application.yml`:

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

- Update dependencies in `apps/acme-payment/build.gradle`:

```gradle
  implementation 'org.springframework.boot:spring-boot-starter-webflux'
  implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
+ implementation 'org.springframework.cloud:spring-cloud-starter-config'
  runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
```

### 3. Install Pack CLI (Ubuntu)

```shell
sudo add-apt-repository ppa:cncf-buildpacks/pack-cli
sudo apt-get update
sudo apt-get install pack-cli
```

For other platforms, refer to [Pack CLI documentation](https://buildpacks.io/docs/for-platform-operators/how-to/integrate-ci/pack/).

### 4. Build Container Images Locally

Ensure configuration changes are complete before building images:

```shell
ACR_LOGIN_SERVER=${ACR_NAME}.azurecr.io
APP_IMAGE_TAG="latest"

declare -A APPS=(
  ["acme-catalog"]="apps/acme-catalog"
  ["acme-payment"]="apps/acme-payment"
  ["acme-order"]="apps/acme-order"
  ["acme-cart"]="apps/acme-cart"
  ["frontend"]="apps/acme-shopping"
  ["acme-identity"]="apps/acme-identity"
)

for APP in "${!APPS[@]}"; do
  pack build ${ACR_LOGIN_SERVER}/${APP}:${APP_IMAGE_TAG} \
        --path ${APPS[$APP]} \
        --builder paketobuildpacks/builder-jammy-base \
        -e BP_JVM_VERSION=17
done
```

### 5. Push Images to ACR

```shell
az acr login -n ${ACR_NAME} --subscription ${SUBSCRIPTION} -g ${RESOURCE_GROUP}

for APP in "${!APPS[@]}"; do
  docker push ${ACR_LOGIN_SERVER}/${APP}:${APP_IMAGE_TAG}
done
```

## Migrate to Azure Container Apps

### 1. Generate Migration Bicep Files

```shell
SOURCE_ASA_NAME=fitness-store
SOURCE_ASA_RESOURCE_GROUP=fitness-store
SOURCE_SUBSCRIPTION='<source-subscription-id>'
OUTPUT_FOLDER='./output/fitness-store'

az spring export \
  --service $SOURCE_ASA_NAME \
  --resource-group $SOURCE_ASA_RESOURCE_GROUP \
  --subscription $SOURCE_SUBSCRIPTION \
  --output-folder $OUTPUT_FOLDER \
  --verbose --debug
```

### 2. Deploy Azure Container Apps Resources

```shell
az deployment group create \
  --resource-group $RESOURCE_GROUP \
  --subscription $SUBSCRIPTION \
  --template-file ${OUTPUT_FOLDER}/main.bicep \
  --parameters ${OUTPUT_FOLDER}/param.bicepparam
```

> **Note:** If you encounter a `JavaComponentOperationError` with message "Failed to create config map external-auth-config-map for JavaComponent '' in k8se-system namespace"., re-run the deployment command. This is a known limitation of Azure Container Apps due to some incompatible status issue. Refer to the generated `README.md` for additional guidance.

### 3. Update Container Apps with Correct Images

Replace default images with your ACR images. Retrieve your ACR password from Azure Portal (`Settings` > `Access keys`):

```shell
ACR_PASSWORD='<ACR access key>'
ENVIRONMENT=${SOURCE_ASA_NAME}

for APP in "${!APPS[@]}"; do
  az containerapp up \
        --name ${APP}-service \
        --environment ${ENVIRONMENT} \
        --image ${ACR_LOGIN_SERVER}/${APP}:${APP_IMAGE_TAG} \
        --resource-group ${RESOURCE_GROUP} \
        --subscription ${SUBSCRIPTION} \
        --registry-server ${ACR_LOGIN_SERVER} \
        --registry-username ${ACR_NAME} \
        --registry-password ${ACR_PASSWORD} \
        --ingress internal \
        --target-port 8080
done
```

### 4. Update Health Probe Ports

Manually update health probe (liveness/readiness) ports from `80` to `8080` via Azure Portal (`Application` > `Containers`) for each container app.

## Verify Migration

> **Important:** The Azure Container Apps Gateway component is currently in **preview**. For production scenarios, we recommend using a self-hosted gateway solution until the managed gateway component reaches general availability. Refer to the [Azure Container Apps documentation](https://learn.microsoft.com/azure/container-apps/) for more information.

### Retrieve Gateway URL

```shell
az containerapp env java-component gateway-for-spring show \
  --environment ${ENVIRONMENT} \
  --resource-group ${RESOURCE_GROUP} \
  --name gateway \
  --subscription ${SUBSCRIPTION} \
  --query properties.ingress.fqdn \
  --output tsv
```

### Access the Application

Navigate to the retrieved URL in your browser:

```
https://gateway-azure-java.<random-value>.<region>.azurecontainerapps.io
```

Confirm the Fitness Store application loads correctly.
