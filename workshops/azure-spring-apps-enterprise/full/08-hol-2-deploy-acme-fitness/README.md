In the previous section we deployed a simple hello-world service to asa-e instance. In this section we are going to deploy the more sophisticated acme-fitness application to the same asa-e instance. 

This diagram below shows the final result once this section is complete:
![diagram](images/just-services.png)

Below are the diffrent steps that we configure/create to successfully deploy the services/apps
- [1. Configure sampling rate for Application Insights](#1-configure-sampling-rate-for-application-insights)
  - [Update Sampling Rate](#update-sampling-rate)
- [2. Create applications in Azure Spring Apps](#2-create-applications-in-azure-spring-apps)
- [3. Create Application Configuration Service](#3-create-application-configuration-service)
  - [3.1. Bind to Application Configuration Service](#31-bind-to-application-configuration-service)
- [4. Bind to Service Registry](#4-bind-to-service-registry)
- [5. Build and Deploy Polyglot Applications](#5-build-and-deploy-polyglot-applications)


## 1. Configure sampling rate for Application Insights

Create a bash script with the key-vault environment varialbe by making a copy of the supplied template:

```shell
cp ./scripts/setup-keyvault-env-variables-template.sh ./scripts/setup-keyvault-env-variables.sh
```

Open `./scripts/setup-keyvault-env-variables.sh` and update the following information:

```shell
export KEY_VAULT=acme-fitness-kv-CHANGE-ME     # Unique name for Azure Key Vault. Replace CHANGE_ME with the 4 unique characters that were created as part of ARM template in Section 3.
```

Then, set the environment:

```shell
source ./scripts/setup-keyvault-env-variables.sh
```

Retrieve the Instrumentation Key for Application Insights and add to Key Vault

```shell
export INSTRUMENTATION_KEY=$(az monitor app-insights component show --app \
    ${APPLICATION_INSIGHTS} | jq -r '.connectionString')

az keyvault secret set --vault-name ${KEY_VAULT} \
    --name "ApplicationInsights--ConnectionString" --value ${INSTRUMENTATION_KEY}
```

### Update Sampling Rate

Increase the sampling rate for the Application Insights binding.

```shell
az spring build-service builder buildpack-binding set \
    --builder-name default \
    --name default \
    --type ApplicationInsights \
    --properties sampling-rate=100 connection_string=${INSTRUMENTATION_KEY}
```

## 2. Create applications in Azure Spring Apps

First step is to create an application for each service:



```shell
az spring app create --name ${CART_SERVICE_APP} --instance-count 1 --memory 1Gi &
az spring app create --name ${ORDER_SERVICE_APP} --instance-count 1 --memory 1Gi &
az spring app create --name ${PAYMENT_SERVICE_APP} --instance-count 1 --memory 1Gi &
az spring app create --name ${CATALOG_SERVICE_APP} --instance-count 1 --memory 1Gi &
az spring app create --name ${FRONTEND_APP} --instance-count 1 --memory 1Gi &
wait
```

Next step is to provide config information for Payment Service and Catalog Service. Remaining services do not need config data stored separately. 
## 3. Create Application Configuration Service

Before we can go ahead and point the services to config stored in an external location, we first need to create an application config instance pointing to that external repo. In this case we are going to create an application config instance that points to a github repo using azure cli.

```shell
az spring application-configuration-service git repo add --name acme-fitness-store-config \
    --label main \
    --patterns "catalog/default,catalog/key-vault,identity/default,identity/key-vault,payment/default" \
    --uri "https://github.com/Azure-Samples/acme-fitness-store-config"
```

### 3.1. Bind to Application Configuration Service

Now the next step is to bind the above created application configuration service instance to the azure apps that use this external config:


```shell
az spring application-configuration-service bind --app ${PAYMENT_SERVICE_APP} &
az spring application-configuration-service bind --app ${CATALOG_SERVICE_APP} &
wait
```

## 4. Bind to Service Registry

Applications need to communicate with each other. As we learnt in [previous section](../07-asa-e-components-overview/service-registry/README.md) ASA-E internally uses Tanzu Service Registry for dynamic service discovery. To achieve this, required services/apps need to be bound to the service registry using the commands below: 

```shell
az spring service-registry bind --app ${PAYMENT_SERVICE_APP}
az spring service-registry bind --app ${CATALOG_SERVICE_APP}
```
## 5. Build and Deploy Polyglot Applications

Now that all the required services are configured, the next step is to go ahead and deploy the services/apps. For this we need access to the source code for the services. 

Now go ahead and create the apps.

```shell
# Deploy Payment Service
az spring app deploy --name ${PAYMENT_SERVICE_APP} \
    --config-file-pattern payment/default \
    --source-path ./apps/acme-payment 

# Deploy Catalog Service
az spring app deploy --name ${CATALOG_SERVICE_APP} \
    --config-file-pattern catalog/default \
    --source-path ./apps/acme-catalog 

# Deploy Order Service
az spring app deploy --name ${ORDER_SERVICE_APP} \
    --source-path ./apps/acme-order 

# Deploy Cart Service 
az spring app deploy --name ${CART_SERVICE_APP} \
    --env "CART_PORT=8080" \
    --source-path ./apps/acme-cart 

# Deploy Frontend App
az spring app deploy --name ${FRONTEND_APP} \
    --source-path ./apps/acme-shopping 
```

You will notice that we need to supply a custom builder for frontend, cart service and order service as these are not Java based apps. Also to note is config-file-pattern for payment and catalog services. As you might recall in previous step, we configured aplication config service for payment and catalog services. This argument is providing the file pattern. More details about this pattern can be found in the previous section [application configuration sevice](../07-asa-e-components-overview/application-config-service/README.md)

After completing the above steps all the required applications should be successfully deployed. You can check for the status of these apps using the command below:

So far in this section we were able to successfully create and deploy the apps into an existing azure spring apps instance. 


⬅️ Previous guide: [07 - ASA-E components Overview](../07-asa-e-components-overview/README.md)

➡️ Next guide: [09 - Hands On Lab 3 - Spring Cloud Gateway Configuration](../09-hol-3-configure-spring-cloud-gateway/README.md)