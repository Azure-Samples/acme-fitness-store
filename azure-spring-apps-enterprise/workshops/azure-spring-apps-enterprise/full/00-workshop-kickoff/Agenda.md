---
page_type: sample
languages:
- java
products:
- Azure Spring Apps
- Azure Database for PostgresSQL
- Azure Cache for Redis
- Azure Active Directory
description: "Deploy Microservice Apps to Azure"
urlFragment: "acme-fitness-store"
---

# 1. Part 1 - Basics of ASA

## 1.1. Registration Activities
### 1.1.1. Activate Azure Pass

## 1.2. Optional Azure 101 Introduction
### 1.2.1. Azure 101 for Spring Developers
### 1.2.2. Follow along a tour in Azure Portal

## 1.3. Kickoff
### 1.3.1. Logistics
### 1.3.2. High Level Agenda for the day
### 1.3.3. Speaker Introduction

## 1.4. Overview of ASA
### 1.4.1. Slides explaining the basics of ASA 
### 1.4.2. Examples of customers using ASA
### 1.4.3. Why Ops will love ASA
#### 1.4.3.1. Security Patterns
##### 1.4.3.1.1. 	TLS encrypted communication channels
##### 1.4.3.1.2. Secure handling of password / secrets
##### 1.4.3.1.3. Single Sign On 
##### 1.4.3.1.4. Zero-trust network security 
##### 1.4.3.1.5. Least privileges for team members
### 1.4.4. Establish the credibility of ASA and why you should care / pay attention

## 1.5. Demo from JAR to URL
###  1.5.1. Create ASA-E service instance using Azure Portal
###  1.5.2. Deploy hello world app to pre-created instance 
###  1.5.3. Scale app 
###  1.5.4. View app logs 

## 1.6. Lab Setup ASA-E
### 1.6.1. Activate Azure pass if you have not already at registration
### 1.6.2. Create ASA-E instance
### 1.6.3. Run ARM template to create resources required by ACME fitness section

## 1.7. Deploy hello world
### 1.7.1. Launch code space on the workshop repo
### 1.7.2. Configure az cli in code space repo to access azure activated subscription
### 1.7.3. Deploy hello world app
### 1.7.4. View the logs
### 1.7.5. Scale the app

## 1.8. Cloud Native Buildpacks
### 1.8.1. Paketo
### 1.8.2. kpack
### 1.8.3. Tanzu Build Service
### 1.8.4. Make the value prop of TBS and cloud native build packs clear 

## 1.9. Review of what has been learned so far
### 1.9.1. Summary of ASA architecture
### 1.9.2. Summary of value prop

# 2. Part 2 - Typical LOB Workload with polyglot apps
## 2.1. Common LoB App Architecture and Its Challenges 
### 2.1.1. Demo of running ACME fitness App
### 2.1.2. Extract common patterns for Lob app like ACME fitness via a slide showing typical Lob App architecture what the problems that need to be solved 
### 2.1.3. Tour of ACME fitness app deployment on Azure portal
### 2.1.4. Tour of ACME Fitness code base – apps, scripts – NodeJS, backend API controllers, polyglot

## 2.2. Planning for next steps
### 2.2.1. Deploy NodeJS frontend
### 2.2.2. Learn about Spring Cloud Gateway
### 2.2.3. Deploy backend APIs 
### 2.2.4. Connecting to data services, managing credentials in Key Vault
### 2.2.5. Configure SSO - using Spring Cloud Gateway and Azure Active Directory
### 2.2.6. Visualize requests to ACME – using Application Insights

## 2.3. Spring Cloud Gateway
### 2.3.1. Overview – Spring Cloud Gateway
### 2.3.2. Demo – deploy frontend 
### 2.3.3. Demo – configure Spring Cloud Gateway (route traffic to front end)

## 2.4. Hands-on 2
### 2.4.1. Deploy frontend
### 2.4.2. Configure Spring Cloud Gateway
### 2.4.3. Validate the URL

## 2.5. Config Service
### 2.5.1. Overview – Application Configuration Service
### 2.5.2. Demo – deploy Catalog backend API // use H2 at this point // and explain how it uses Application Configuration Service
### 2.5.3. Application Configuration Service vs. Spring Cloud Config Server
#### 2.5.3.1. Why Application Configuration Service? No client level dependencies, Polyglot, bound to K8S native concepts
#### 2.5.3.2. What if I am using Spring Cloud Config today? How to migrate?

## 2.6. Service Registry
### 2.6.1. Overview – app registration and discovery
### 2.6.2. Explain how the apps are bound to the managed Eureka

## 2.7. Azure Key Vault
### 2.7.1. Overview – data services consumed
### 2.7.2. Overview – Azure Key Vault
### 2.7.3. How backend APIs use Azure Key Vault to securely load credentials
### 2.7.4. Demo - configure the Catalog app to securely load credentials

## 2.8. SSO – using Spring Cloud Gateway and Azure Active Directory
### 2.8.1. Overview – how SSO is configured
### 2.8.2. Show Microsoft Entra App and its configuration
### 2.8.3. Demo – deploy Identity app
### 2.8.4. Show end-to-end experience for placing an order

## 2.9. Hands-on 3
### 2.9.1. Deploy all backend APIs
### 2.9.2. Bind them to Config and Service Registry
### 2.9.3. Connect to databases, securely load secrets from Key Vault
### 2.9.4. Configure SSO
### 2.9.5. Place orders on the site – end-to-end experience works 

## 2.10. Review learnings

# 3. Part 3 - Modern end-to-end path to prod with ASA-E – day in the life of App Ops / DevOps  
## 3.1. Observability
### 3.1.1. Observability patterns 
#### 3.1.1.1. Metrics, Traces, Logs, Health Checks
### 3.1.2. Out of the box observability demo for ASA-E 

## 3.2. Hands on 4
### 3.2.1. Navigate to app insight to view the app map, operations executed, performance, end-to-end transactions, exceptions, custom metrics, live metrics, etc. 
### 3.2.2. Run queries to retrieve logs – app logs, exceptions, gateway / config / registry logs 

## 3.3. Automation
### 3.3.1. Pipelines and gates on the path to prod dev/uat/staging … etc.
### 3.3.2. Automation Patterns
#### 3.3.2.1. Blue Green Deployment
#### 3.3.2.2. Automated provisioning
### 3.3.3. Demo GitHub Actions pipelines with blue green deployment of a service

## 3.4. Quality
### 3.4.1. Testing Patterns
#### 3.4.1.1. Pre-commit / pre-merge validation of proposed changes to code
#### 3.4.1.2. Integration testing with test containers
### 3.4.2. Demo of test pattern implementations 1

# 4. Part 4 Local Developer Experience
## 4.1. Patterns for a pleasant local developer experience
### 4.1.1. How to get the apps running locally
### 4.1.2. How to automate creation of a local development environment
### 4.1.3. How to validate those changes did not break anything

## 4.2. Demo of day in the life of the developers of the ACME fitness show what life is like on the dev laptop
### 4.2.1. Ad hoc inner loop
### 4.2.2. Automated tests for the app 
## 4.3. Leveraging Test containers to automate local developer experience














