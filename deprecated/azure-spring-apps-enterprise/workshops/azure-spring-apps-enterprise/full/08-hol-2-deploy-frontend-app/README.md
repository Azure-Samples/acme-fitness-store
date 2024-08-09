In the previous section we deployed a simple hello-world service to asa-e instance. In this section we are going to deploy the frontend of acme-fitness, configure that with Spring Cloud Gateway and validate that we are able to access the frontend. 

This diagram below shows the final result once this section is complete:
![diagram](images/frontend.png)

Below are the different steps that we configure/create to successfully deploy the services/apps
- [1. Configure sampling rate for Application Insights](#1-configure-sampling-rate-for-application-insights)
  - [1.1. Update Sampling Rate](#11-update-sampling-rate)
- [2. Create and Deploy frontend application in Azure Spring Apps](#2-create-and-deploy-frontend-application-in-azure-spring-apps)
- [3. Configure Spring Cloud Gateway](#3-configure-spring-cloud-gateway)
  - [3.1. Create  routing rules for the applications:](#31-create--routing-rules-for-the-applications)
- [4. Access the Application through Spring Cloud Gateway](#4-access-the-application-through-spring-cloud-gateway)
- [5. Explore the API using API Portal](#5-explore-the-api-using-api-portal)


## 1. Configure sampling rate for Application Insights

This is a preliminary step that is not related to deploying the frontend app. But this is here to make sure that Application Insights is configured with all the required information before deploying any apps. More details about Application Insights will be discussed in section 14.

Create a bash script with the key-vault environment varialbe by making a copy of the supplied template:

```shell
cp ./scripts/setup-keyvault-env-variables-template.sh ./scripts/setup-keyvault-env-variables.sh -i
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
export INSTRUMENTATION_KEY=$(az monitor app-insights component show --app ${SPRING_APPS_SERVICE} | jq -r '.connectionString')

az keyvault secret set --vault-name ${KEY_VAULT} \
    --name "ApplicationInsights--ConnectionString" --value ${INSTRUMENTATION_KEY}
```

### 1.1. Update Sampling Rate

Before you run the below command, please make sure that there are no existing running applications. These running application causes a conflict updating the buildpacks builder. If you have existing applications, they can be deleted from Azure Portal, going to Azure Spring Apps Instance and going to Apps section. 

Increase the sampling rate for the Application Insights binding.

```shell
az spring build-service builder buildpack-binding set \
    --builder-name default \
    --name default \
    --type ApplicationInsights \
    --properties sampling-rate=100 connection_string=${INSTRUMENTATION_KEY}
```

## 2. Create and Deploy frontend application in Azure Spring Apps

First step is to create an application for each service:



```shell
az spring app create --name ${FRONTEND_APP} --instance-count 1 --memory 1Gi &
wait
```

Once the above step is complete, we need to deploy the app.
```shell
az spring app deploy --name ${FRONTEND_APP} \
    --source-path ./apps/acme-shopping 
```

## 3. Configure Spring Cloud Gateway

Assign a public endpoint and update the Spring Cloud Gateway configuration with API
information:

```shell
az spring gateway update --assign-endpoint true
export GATEWAY_URL=$(az spring gateway show | jq -r '.properties.url')
```
The assign-endpoint argument with a value of true creates a publicly accessible endpoint for the gateway.

```shell
az spring gateway update \
    --api-description "Acme Fitness Store API" \
    --api-title "Acme Fitness Store" \
    --api-version "v1.0" \
    --server-url "https://${GATEWAY_URL}" \
    --allowed-origins "*" \
    --no-wait
```

### 3.1. Create  routing rules for the applications:

Routing rules bind endpoints in the request to the backend applications. In the step below we are creating a rule in SCG to the frontend app.

```shell

az spring gateway route-config create \
    --name ${FRONTEND_APP} \
    --app-name ${FRONTEND_APP} \
    --routes-file ./azure-spring-apps-enterprise/resources/json/routes/frontend.json

```

## 4. Access the Application through Spring Cloud Gateway

Retrieve the URL for Spring Cloud Gateway and open it in a browser:

```shell
echo "https://${GATEWAY_URL}"
```

If you see acme-fitness home page displayed as below, then congratulations. Your frontend app and its corresponding route in SCG are configured correctly and deployed successfully. Explore the application, but notice that not everything is functioning yet. Continue on to next section to configure the rest of the functionality.

![acme-fitness home page](./images/acme-fitness-homepage.png)
## 5. Explore the API using API Portal

Assign an endpoint to API Portal and open it in a browser:

```shell
az spring api-portal update --assign-endpoint true
export PORTAL_URL=$(az spring api-portal show | jq -r '.properties.url')

echo "https://${PORTAL_URL}"
```


⬅️ Previous guide: [07 - ASA-E components Overview](../07-asa-e-components-overview/README.md)

➡️ Next guide: [09 - Hands On Lab 3.1 - Deploy backend apps](../09-hol-3.1-deploy-backend-apps/README.md)