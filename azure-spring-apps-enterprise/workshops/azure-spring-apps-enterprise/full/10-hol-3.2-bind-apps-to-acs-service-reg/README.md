In this section we are going to bind the backend apps that were deployed in the previous section to Application Config Service and Service Registry.


Below are the different steps to bind apps to Application Configuration Service and Service Registry.
- [1. Create Application Configuration Service](#1-create-application-configuration-service)
  - [1.1. Configure apps to Application Configuration Service](#11-configure-apps-to-application-configuration-service)
- [2. Bind apps to Service Registry](#2-bind-apps-to-service-registry)


## 1. Create Application Configuration Service

Before we can go ahead and point the services to config stored in an external location, we first need to create an application config instance pointing to that external repo. In this case we are going to create an application config instance that points to a github repo using azure cli.

```shell
az spring application-configuration-service git repo add --name acme-fitness-store-config \
    --label main \
    --patterns "catalog/default,catalog/key-vault,identity/default,identity/key-vault,payment/default" \
    --uri "https://github.com/Azure-Samples/acme-fitness-store-config"
```

### 1.1. Configure apps to Application Configuration Service

Now the next step is to bind the above created application configuration service instance to the azure apps that use this external config:


```shell
az spring application-configuration-service bind --app ${PAYMENT_SERVICE_APP} &
az spring application-configuration-service bind --app ${CATALOG_SERVICE_APP} &
wait
```

## 2. Bind apps to Service Registry

Applications need to communicate with each other. ASA-E internally uses Tanzu Service Registry for dynamic service discovery. For details, please reference [Use Tanzu Service Registry](https://learn.microsoft.com/azure/spring-apps/how-to-enterprise-service-registry). To achieve this, required services/apps need to be bound to the service registry using the commands below: 

```shell
az spring service-registry bind --app ${PAYMENT_SERVICE_APP}
az spring service-registry bind --app ${CATALOG_SERVICE_APP}
```

So far in this section we were able to successfully bind backend apps to Application Config Service and Service Registry. 


⬅️ Previous guide: [09 - Hands On lab 3.1 - Deploy Backend apps](../09-hol-3.1-deploy-backend-apps/README.md)

➡️ Next guide: [11 - Hands On Lab 3.3 - Configure Database and Cache](../11-hol-3.3-configure-database-cache/README.md)