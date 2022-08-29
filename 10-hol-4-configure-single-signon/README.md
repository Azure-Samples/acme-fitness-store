In this Unit, you will configure Single Sign-On for Spring Cloud Gateway using Azure Active Directory or an existing Identity Provider.

### Register Application with Azure AD

The following section steps through creating a Single Sign On Provider using Azure AD.
To use an existing provider, skip ahead to [Using an Existing Identity Provider](#using-an-existing-sso-identity-provider)

Choose a unique display name for your Application Registration.

```shell
export AD_DISPLAY_NAME=change-me    # unique application display name
```

Create an Application registration with Azure AD and save the output.

```shell
az ad app create --display-name ${AD_DISPLAY_NAME} > ad.json
```

Retrieve the Application ID and collect the client secret:

```shell
export APPLICATION_ID=$(cat ad.json | jq -r '.appId')

az ad app credential reset --id ${APPLICATION_ID} --append > sso.json
```

Assign a Service Principal to the Application Registration

```shell
az ad sp create --id ${APPLICATION_ID}
```

More detailed instructions on Application Registrations can be found [here](https://docs.microsoft.com/en-us/azure/active-directory/develop/quickstart-register-app)

### Prepare your environment for SSO Deployments

Set the environment using the provided script and verify the environment variables are set:

```shell
source ./azure/setup-sso-variables-ad.sh

echo ${CLIENT_ID}
echo ${CLIENT_SECRET}
echo ${TENANT_ID}
echo ${ISSUER_URI}
echo ${JWK_SET_URI}
```

The `ISSUER_URI` should take the form `https://login.microsoftonline.com/${TENANT_ID}/v2.0`
The `JWK_SET_URI` should take the form `https://login.microsoftonline.com/${TENANT_ID}/discovery/v2.0/keys`

Add the necessary web redirect URIs to the Azure AD Application Registration:

```shell
az ad app update --id ${APPLICATION_ID} \
    --web-redirect-uris "https://${GATEWAY_URL}/login/oauth2/code/sso" "https://${PORTAL_URL}/oauth2-redirect.html" "https://${PORTAL_URL}/login/oauth2/code/sso"
```

Detailed information about redirect URIs can be found [here](https://docs.microsoft.com/en-us/azure/active-directory/develop/quickstart-register-app#add-a-redirect-uri).

### Using an Existing SSO Identity Provider

> Note: Continue on to [Configure Spring Cloud Gateway with SSO](#configure-spring-cloud-gateway-with-sso) if you 
> just created an Azure AD Application Registration

To use an existing SSO Identity Provider, copy the existing template

```shell
cp ./azure/setup-sso-variables-template.sh ./azure/setup-sso-variables.sh
```

Open `./azure/setup-sso-variables.sh` and provide the required information.

```shell
export CLIENT_ID=change-me        # Your SSO Provider Client ID
export CLIENT_SECRET=change-me    # Your SSO Provider Client Secret
export ISSUER_URI=change-me       # Your SSO Provider Issuer URI
export JWK_SET_URI=change-me      # Your SSO Provider Json Web Token URI
```

The `issuer-uri` configuration should follow Spring Boot convention, as described in the official Spring Boot documentation:
The provider needs to be configured with an issuer-uri which is the URI that the it asserts as its Issuer Identifier. For example, if the issuer-uri provided is "https://example.com", then an OpenID Provider Configuration Request will be made to "https://example.com/.well-known/openid-configuration". The result is expected to be an OpenID Provider Configuration Response.
Note that only authorization servers supporting OpenID Connect Discovery protocol can be used

The `JWK_SET_URI` typically takes the form `${ISSUER_URI}/$VERSION/keys`

Set the environment:

```shell
source ./azure/setup-sso-variables.sh
```

Add the following to your SSO provider's list of approved redirect URIs:

```shell
echo "https://${GATEWAY_URL}/login/oauth2/code/sso"
echo "https://${PORTAL_URL}/oauth2-redirect.html" 
echo "https://${PORTAL_URL}/login/oauth2/code/sso"
```

### Configure Spring Cloud Gateway with SSO

Configure Spring Cloud Gateway with SSO enabled:

```shell
az spring gateway update \
    --client-id ${CLIENT_ID} \
    --client-secret ${CLIENT_SECRET} \
    --scope ${SCOPE} \
    --issuer-uri ${ISSUER_URI} \
    --no-wait
```

### Deploy the Identity Service Application

Create the identity service application

```shell
az spring app create --name ${IDENTITY_SERVICE_APP} --instance-count 1 --memory 1Gi
```

Bind the identity service to Application Configuration Service

```shell
az spring application-configuration-service bind --app ${IDENTITY_SERVICE_APP}
```

Bind the identity service to Service Registry.

```shell
az spring service-registry bind --app ${IDENTITY_SERVICE_APP}
```

Create routing rules for the identity service application

```shell
az spring gateway route-config create \
    --name ${IDENTITY_SERVICE_APP} \
    --app-name ${IDENTITY_SERVICE_APP} \
    --routes-file azure/routes/identity-service.json
```

Deploy the Identity Service:

```shell
az spring app deploy --name ${IDENTITY_SERVICE_APP} \
    --env "JWK_URI=${JWK_SET_URI}" \
    --config-file-pattern identity/default \
    --source-path apps/acme-identity
```

> Note: The application will take around 3-5 minutes to deploy.

### Update Existing Applications

Update the existing applications to use authorization information from Spring Cloud Gateway:

```shell
# Update the Cart Service
az spring app update --name ${CART_SERVICE_APP} \
    --env "AUTH_URL=https://${GATEWAY_URL}" "CART_PORT=8080" 
    
# Update the Order Service
az spring app  update --name ${ORDER_SERVICE_APP} \
    --env "AcmeServiceSettings__AuthUrl=https://${GATEWAY_URL}" 
```

### Login to the Application through Spring Cloud Gateway

Retrieve the URL for Spring Cloud Gateway and open it in a browser:

```shell
open "https://${GATEWAY_URL}"
```

If using Azure Cloud Shell or Windows, open the output from the following command in a browser:

```shell
echo "https://${GATEWAY_URL}"
```

You should see the ACME Fitness Store Application, and be able to log in using your
SSO Credentials. Once logged in, the remaining functionality of the application will
be available. This includes adding items to the cart and placing an order.

### Configure SSO for API Portal

Configure API Portal with SSO enabled:

```shell
export PORTAL_URL=$(az spring api-portal show | jq -r '.properties.url')

az spring api-portal update \
    --client-id ${CLIENT_ID} \
    --client-secret ${CLIENT_SECRET}\
    --scope "openid,profile,email" \
    --issuer-uri ${ISSUER_URI}
```

### Explore the API using API Portal

Open API Portal in a browser, this will redirect you to log in now:

```shell
open "https://${PORTAL_URL}"
```

If using Azure Cloud Shell or Windows, open the output from the following command in a browser:

```shell
echo "https://${PORTAL_URL}"
```

To access the protected APIs, click Authorize and follow the steps that match your
SSO provider. Learn more about API Authorization with API Portal [here](https://docs.vmware.com/en/API-portal-for-VMware-Tanzu/1.0/api-portal/GUID-api-viewer.html#api-authorization)