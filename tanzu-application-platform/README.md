
# Deploy to Tanzu Application Platform (TAP)

ACME fitness store supports configuration to build and deploy to Tanzu Application Platform (TAP).  The following instructions describe an end-to-end 
process for configuring, building, and deploying the ACME Fitness Store to a TAP `full` profile cluster.  Using additional configuration, the process can
be split across multiple cluster profiles (`build, view, and run`).

## Prerequisites

These instructions assume that you have a TAP 1.4.x or greater `full` profile clusterup and running with the following packages installed and [kubectl](https://kubernetes.io/docs/tasks/tools/) and the Tanzu CLI installed and configured to access your TAP cluster:

* YTT
* Tanzu TAP GUI
* Tanzu Build Services
* Tanzu Cloud Native Runtimes
* Tanzu Out of the Box Supply Chains
* Tanzu Out of the Box Templates
* Tanzu Source Controller
* Tanzu AppSSO
* Certificate Manager
* [Spring Cloud Gateway For Kubernetes](https://docs.vmware.com/en/VMware-Spring-Cloud-Gateway-for-Kubernetes/index.html)

## Installation/Deployment Process

You should first determine the full URL where you intend to deploy the application.  Optimally, you should have a domain name registered with a 
DNS registrar and create an appropriate DNA entry for the hostname of the application.  This documentation assumes you will use the hostname `acme-fitness`.  The full URL 
in this case would be:

```
acme-fitness.<your domain name>
```

Make a note of this URL as you will need it through various steps of the following doc.

It is assumed that you have cloned the Acme Fitness Store Git repository to your workstation.  All of the `ytt` commands below should be run from the
`tap` directory in the cloned repository.

### Testing Pipeline Deployment

If you have a supply chain configured that includes testing, you will need to install the testing pipeline used when a build is performed.  Run the following command 
to install the pipeline replacing the following placeholder:

- **<workloadNamespace>** – Namespace where the application will be deployed

```
kubectl apply -f testingPipeline.yaml -n <workloadNamespace>
```

For example:

```
kubectl apply -f testingPipeline.yaml -n workloads
```

### Let's Encrypt CA Issuer

Acme Fitness Store enables TLS connections for security and reliable transport and utilizes the Let's Encrypt production CA. Install the Let's Encrypt
CA issuer into your cluster by running the following command and replacing the following place holder:

- **<email>** – The email address of the subject responsible for the issued certificates.
 
```
ytt -f caIssuer.yaml -v email=<email> | kubectl apply -f-
```

For example:

```
ytt -f caIssuer.yaml -v email=joe@joesgarage.com | kubectl apply -f-
```



### AppSSO Deployment

ACME requires the use of an AppSSO authorization server and client registration resource. 

Deploy the authorization server instance by running the following commands and replacing these placeholders:

- **<workloadNamespace>** – Namespace where the application will be deployed
- **<devDefaultAccountUsername>** – Username for authentication
- **<devDefaultAccountPassword>** – Password for authentication

```
ytt -f appSSOInstance.yaml -v workloadNamespace=<workloadNamespace> -v devDefaultAccountUsername=<devDefaultAccountUsername> -v devDefaultAccountPassword=<devDefaultAccountPassword> | kubectl apply -f-
```

For example:

```
ytt -f appSSOInstance.yaml -v workloadNamespace=workloads -v devDefaultAccountUsername=acme -v devDefaultAccountPassword=fitness | kubectl apply -f-
```

Next, create a ClientRegistration resource by running the following command and replacing these placeholders:

- **<workloadNamespace>** – Namespace where the application will be deployed
- **<appSSORedirectURI>** - Public URI that the authorization server will redirect to after a successful login.  This will include the full URL of your application as described earlier followed by `/login/oauth2/code/sso`

```
ytt -f clientRegistrationResourceClaim.yaml.yaml -v workloadNamespace=<workloadNamespace> -v appSSORedirectURI=<appSSORedirectURI> | kubectl apply –f-
```

For example:

```
ytt -f clientRegistrationResourceClaim.yaml -v workloadNamespace=workloads -v appSSORedirectURI=acme-fitness.perfect300rock.com/login/oauth2/code/sso | kubectl apply -f-
```

Next, obtain the appSSO issuerURI by running the following command replacing <workloadNamespace> with the name of the namespace where the application will be deployed:

```
kubectl get authserver -n <workloadNamespace>
```

Save the Issuer URI as you will need it in the `workload build` section.  Also, you may need to create a new entry in your DNS registrar for this URL; this will likely be
necessary if you are NOT using wildcard records in your DNS registrar.  DNS is also required in order for the TLS certificate to be issued for the auth server.


### Redis Deployment

A Redis instance is needed for caching the Acme fitness store cart service. To deploy the Redis instance, run the command below, replacing the <workloadNamespace> placeholder 
with the namespace where the application will be deployed and the <redisPassword> placeholder to an arbitrary password.

```
ytt -f redis.yaml -v workloadNamespace=<workloadNamespace> -v redisPassword=<redisPassword> | kb apply -f-
```

For example:

```
ytt -f redis.yaml -v workloadNamespace=workloads -v redisPassword=fitness | kubectl apply -f-
```

### Workload Build And Deployment

To build the application services, execute the following command to apply the workload resources to your cluster while replacing these placeholders: Modify the `<workloadNamespace>` placeholder with the namespace where the application will be deployed, and the <appSSOIssuerURI> placeholder for the URL of the AppSSO authorization server that you deployed in the `AppSSO Deployment` step; you will use the Issuer URI that you saved off in that step.

- **<workloadNamespace>** – Namespace where the application will be deployed
- **<appSSOIssuerURI>** – The URL of the AppSSO authorization server that you deployed in the `AppSSO Deployment` step; you will use the Issuer URI that you saved off in that step
- **<appDomainName>** – The application’s DNS domain (the domain name you chose at the beginning of these install steps).
- **<sourceRepo>** – The Git repository of the Acme Fitness source.  This will likely be the same repository that you cloned at the beginning of these install steps.
- **<sourceRepoBranch>** – The Git repository branch. 

```
ytt -f workloads.yaml -v workloadNamespace=<workloadNamespace> -v appSSOIssuerURI=<appSSOIssuerURL> -v appDomainName=<appDomainName> -v sourceRepo=<sourceRepo> -sourceRepoBranch=<sourceRepoBranch> | kubectl apply -f-
```

For example:

```
ytt -f workloads.yaml -v workloadNamespace=workloads -v appSSOIssuerURI=https://appsso-acme-fitness.workloads.perfect300rock.com  -v appDomainName=perfect300rock.com -v sourceRepo=https://github.com/gm2552-commercial/acme-fitness-store -v sourceRepoBranch=Azure  | kubectl apply -f-
```


### Spring Cloud Gateway Deployment

Spring Cloud Gateway is used a "front door" for all requestions in the Acme Fitness application.  To deploy the gateway along with applicable routes, run the following commands 
replacing the <workloadNamespace> placeholder with the namespace where the application will be deployed.

**NOTE** It is optimal (but not necessary) if the workload build and deployment steps above have completed successfully before deploying the gateway routes.

```
ytt -f scgInstance.yaml -v workloadNamespace=<workloadNamespace>

ytt -f scgRoutes.yaml -v workloadNamespace=<workloadNamespace>
```

For example:

```
ytt -f scgInstance.yaml -v workloadNamespace=workloads | kubectl apply -f-

ytt -f scgRoutes.yaml -v workloadNamespace=workloads | kubectl apply -f-
```

### Ingress Deployment

The Ingress resources utilizes Contour and will route traffic to the Spring Cloud Gateway.  Run the following command to create the Istio ingress resources replacing 
`<workloadNamespace>` and `<appDomain>` placeholders with the namespace where the application will be deployed and the application’s DNS domain (the domain name you chose at the beginning of these steps).  This will create an Ingress object that uses the full URL of the application (again, the URL you chose at the beginning of these steps).

```
ytt -f ingress.yaml -v workloadNamespace=<workloadNamespace> -v appDomainName=<appDomain> | kubectl apply -f-
```

For example:

```
ytt -f ingress.yaml -v workloadNamespace=workloads -v appDomainName=perfect300rock.com | kubectl apply -f-
```

## Application Catalog

To view the runtime resources and application live view, you will need to install the catalog-info.yaml file that contains catalog info for each component.  
To install the catalog, navigate to the TAP GUI home page and click the `Register Entity` button.  In the URL, enter in the URL of the catalog file.  For example:

```
https://github.com/spring-cloud-services-samples/acme-fitness-store/blob/main/tanzu-application-platform/yaml/catalog-info.yaml
```