In this section we are going to deploy the backend apps for acme-fitness application. We also updates the rules for these backend apps in Spring Cloud Gateway.

This diagram below shows the final result once this section is complete:
![diagram](images/scg-frontend-backend.png)

Below are the different steps that we configure/create to successfully deploy the services/apps
- [1. Create backend apps](#1-create-backend-apps)
- [2. Deploy backend apps](#2-deploy-backend-apps)
- [3. Create  routing rules for the backend apps:](#3-create--routing-rules-for-the-backend-apps)



## 1. Create backend apps

First step is to create an application for each service:

```shell
az spring app create --name ${CART_SERVICE_APP} --instance-count 1 --memory 1Gi &
az spring app create --name ${ORDER_SERVICE_APP} --instance-count 1 --memory 1Gi &
az spring app create --name ${PAYMENT_SERVICE_APP} --instance-count 1 --memory 1Gi &
az spring app create --name ${CATALOG_SERVICE_APP} --instance-count 1 --memory 1Gi &
wait
```
If the above step is successfully complete, you should see all the backend apps listed in your ASA-E instance as below..

![all-apps](./images/all-apps.png)

## 2. Deploy backend apps

Now that all the required apps are created, the next step is to go ahead and deploy the services/apps. For this we need access to the source code for the services. 

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
```

So far in this section we were able to successfully create and deploy the apps into an existing azure spring apps instance. 

## 3. Create  routing rules for the backend apps:

Routing rules bind endpoints in the request to the backend applications. For example in the Cart route below, the routing rule indicates any requests to /cart/** endpoint gets routed to backend Cart App.

```shell
az spring gateway route-config create \
    --name ${CART_SERVICE_APP} \
    --app-name ${CART_SERVICE_APP} \
    --routes-file ./azure-spring-apps-enterprise/resources/json/routes/cart-service.json
    
az spring gateway route-config create \
    --name ${ORDER_SERVICE_APP} \
    --app-name ${ORDER_SERVICE_APP} \
    --routes-file ./azure-spring-apps-enterprise/resources/json/routes/order-service.json

az spring gateway route-config create \
    --name ${CATALOG_SERVICE_APP} \
    --app-name ${CATALOG_SERVICE_APP} \
    --routes-file ./azure-spring-apps-enterprise/resources/json/routes/catalog-service.json

```

This completes successful deployments of all the backend apps and updating the rules for these apps in SCG.

⬅️ Previous guide: [08 - Hands On Lab 2 - Deploy Frontend app](../08-hol-2-deploy-frontend-app/README.md)

➡️ Next guide: [10 - Hands On Lab 3.2 - Bind Apps to ACS and Service Registry](../10-hol-3.2-bind-apps-to-acs-service-reg/README.md)
