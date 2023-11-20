---
page_type: sample
languages:
- java
products:
- Azure Spring Apps
- Azure Database for PostgreSQL
- Azure Cache for Redis
- Azure Active Directory
description: "Deploy Microservice Apps to Azure"
urlFragment: "acme-fitness-store"
---
# ACME Fitness Store
![Check Markdown Links](https://github.com/Azure-Samples/acme-fitness-store/actions/workflows/markdown-link-check.yml/badge.svg)

ACME Fitness store is a fictional online retailer selling sporting goods. This repo contains the source code and deployment scripts for the ACME Fitness store application.

## High Level Architecture
![An image showing the services involved in the ACME Fitness Store. It depicts the applications and their dependencies](./azure-spring-apps-enterprise/media/acme-fitness-store-architecture.jpg)

This application is composed of several services:

* 4 Java Spring Boot applications:
  * A catalog service for fetching available products. 
  * A payment service for processing and approving payments for users' orders
  * An identity service for referencing the authenticated user
  * An assist service for infusing AI into fitness store

* 1 Python application:
  * A cart service for managing a users' items that have been selected for purchase

* 1 ASP.NET Core applications:
  * An order service for placing orders to buy products that are in the users' carts

* 1 NodeJS and static HTML Application
  * A frontend shopping application

The sample can be deployed to Azure Spring Apps Enterprise or Tanzu Application Platform. 

## Repo Organization

| Directory                                                        | Purpose |
| ---------------------------------------------------------------- | ------------- |
| [apps/](./apps)                                                   | source code for the services  |
| [azure-spring-apps-enterprise/](./azure-spring-apps-enterprise)   | documentation and scripts for deploying to Azure Spring Apps Enterprise |
| [tanzu-application-platform/](./tanzu-application-platform)       | documentation and scripts for deploying to Tanzu Application Platform|

## Deploy Apps to Azure Spring Apps Enterprise (ASA-E)

Azure Spring Apps Enterprise enables you to easily run Spring Boot and 
polyglot applications on Azure. The quickstart guide in 
[azure-spring-apps-enterprise/README.md](./azure-spring-apps-enterprise/README.md) 
shows you how to deploy the ACME Fitness store application to Azure Spring Apps
 Enterprise.

* [Deploy Applications to Azure Spring Apps](./azure-spring-apps-enterprise/README.md#deploy-spring-boot-apps-to-azure-spring-apps-enterprise)
  * [What will you experience](./azure-spring-apps-enterprise/#what-will-you-experience)
  * [What you will need](./azure-spring-apps-enterprise/#what-you-will-need)
  * [Install the Azure CLI extension](./azure-spring-apps-enterprise/#install-the-azure-cli-extension)
  * [Clone the repo](./azure-spring-apps-enterprise/#clone-the-repo)
  * [Unit 0 - Prepare Environment](./azure-spring-apps-enterprise/#unit-0---prepare-environment)  
  * [Unit 1 - Deploy and Build Applications](./azure-spring-apps-enterprise/#unit-1---deploy-and-build-applications)
  * [Unit 2 - Configure Single Sign On](./azure-spring-apps-enterprise/#unit-2---configure-single-sign-on)
  * [Unit 3 - Integrate with Azure Database for PostgreSQL and Azure Cache for Redis](./azure-spring-apps-enterprise/#unit-3---integrate-with-azure-database-for-postgresql-and-azure-cache-for-redis)
  * [Unit 4 - Securely Load Application Secrets](./azure-spring-apps-enterprise/#unit-4---securely-load-application-secrets)
  * [Unit 5 - Monitor End-to-End](./azure-spring-apps-enterprise/#unit-5---monitor-end-to-end)
  * [Unit 6 - Set Request Rate Limits](./azure-spring-apps-enterprise/#unit-6---set-request-rate-limits)
  * [Unit 7 - Automate from idea to production](./azure-spring-apps-enterprise/#unit-7---automate-from-idea-to-production)
  * [Unit 8 - Infuse AI into Fitness Store](./azure-spring-apps-enterprise/#unit-8---infuse-ai-into-fitness-store)

## Deploy Apps to Tanzu Application Platform (TAP)

Tanzu Application Platform (TAP) enables you to easily build & run polyglot 
applications on CNCF Compliant Kubernetes clusters. Follow the instructions
in [tanzu-application-platform/README.md](./tanzu-application-platform/README.md)
to deploy the application to TAP.


