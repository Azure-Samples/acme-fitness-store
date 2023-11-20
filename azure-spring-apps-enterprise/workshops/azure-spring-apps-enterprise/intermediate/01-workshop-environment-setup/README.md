#### There are multiple options to work through this workshop.
- [GitHub Codespaces](#github-codespaces)
- [Cloud Shell](#cloud-shell)
- [Git Bash](#git-bash)

Follow the instructions based on the option you choose.

## Github Codespaces
Github Codespaces can be leveraged to provide a development environment for running the instructions. Certainly you can run these instruction from a shell. But for people trying to get familiar with ASA-E for the first time, to rule out any environment related issues we recommend using Github Codespaces.

1. The first step in getting access to github codespaces option for [Azure Samples](https://github.com/Azure-Samples/) is to share your github id with the workshop co-ordinator. They will add you to the organization and assign you permissions that makes the Codespaces option visible.

2. Upon getting the confirmation that you are added to the Org, navigate to https://github.com/Azure-Samples/acme-fitness-store/tree/Azure, click "Code" button. You should be able to "Codespaces" as an option listed. If you do not see that option listed, most probably you are not added to [Azure-Samples](https://github.com/Azure-Samples/) org or your github id is still not active in this org. Please discuss this issue with your workshop coordinator. If invitation was sent but it is not in your mailbox, you can visit https://github.com/Azure-Samples/acme-fitness-store/invitations to accept it directly.

3. Assuming the above steps are successful, you should be able to open a terminal inside VS Code that opens up in Codespaces. Refer to this link to understand more about [Codespaces](https://docs.github.com/codespaces). This Codespaces comes installed with the following software:
   1. * [JDK 17](https://docs.microsoft.com/java/openjdk/download?WT.mc_id=azurespringcloud-github-judubois#openjdk-17)
   2. * The environment variable `JAVA_HOME` should be set to the path of the JDK installation. The directory specified by this path should have `bin`, `jre`, and `lib` among its subdirectories. Further, ensure your `PATH` variable contains the directory `${JAVA_HOME}/bin`. To test, type `which javac` into bash shell ensure the resulting path points to a file inside `${JAVA_HOME}/bin`.
   3. * [Azure CLI version 2.31.0 or higher](https://docs.microsoft.com/cli/azure/install-azure-cli?view=azure-cli-latest) version 2.31.0 or later. You can check the version of your current Azure CLI installation by running:

    ```bash
    az --version
    ```

    #### Update Environment Variables
    ```shell
    cd acme-fitness-store/azure-spring-apps-enterprise
    cp ./scripts/setup-env-variables-template.sh ./scripts/setup-env-variables.sh
    code ./scripts/setup-env-variables.sh # Use the editor of your choice
    ```

    ```shell
    export SUBSCRIPTION=CHANGEME               # replace it with your subscription-id (quote is not required)
    export RESOURCE_GROUP=CHANGEME             # choose a unique name if the moderator doesn't provide it
    export SPRING_APPS_SERVICE=CHANGEME        # choose a unique name if the moderator doesn't provide it
    export LOG_ANALYTICS_WORKSPACE=CHANGEME    # choose a unique name if the moderator doesn't provide it
    export REGION=CHANGEME                     # The region where you are running this workshop
    ```

    - To get the Subscription ID, go to Azure portal, in search bar type subscriptions. The results should display your subscription and its id.
    - RESOURCE_GROUP name will be provided by your workshop moderator
    - SPRING_APPS_SERVICE name will be provided by your workshop moderator

    This env file comes with default values that were provided as part of arm template. It is recommended to leave the values as-is for the purpose of this workshop. If for any reason you updated these default values in the arm template, those values need to be entereted in here.

    Now, set the environment:

    ```shell
    source ./scripts/setup-env-variables.sh
    ``` 

    Verify environment variables are set 
    ```shell
    echo $SUBSCRIPTION
    echo $RESOURCE_GROUP 
    echo $SPRING_APPS_SERVICE 
    echo $REGION 
    echo $CART_SERVICE_APP 
    echo $IDENTITY_SERVICE_APP 
    echo $ORDER_SERVICE_APP 
    echo $PAYMENT_SERVICE_APP 
    echo $CATALOG_SERVICE_APP 
    echo $FRONTEND_APP 
    ```

    > If you exit your Codespaces and reconnect in or get logged out of Cloud Shell, you need to re-run the command `source ./scripts/setup-env-variables.sh` to setup the environment.

    #### Login to Azure and set subscription

    ```shell
    az login --use-device-code # Only for Codespaces and Git Bash
    az account list -o table
    az account set --subscription ${SUBSCRIPTION}
    ```

    Set your default resource group name and cluster name using the following commands:

    ```shell
    az configure --defaults \
        group=${RESOURCE_GROUP} \
        spring=${SPRING_APPS_SERVICE}
    ```
    Verify defaults
    ```shell
    az configure --list-defaults 
    ```

## Cloud Shell

Login to Azure Portal and open Cloud Shell bash prompt

![Alt text](../../../../media/cloudshell.png?raw=true "Optional Title")

Make sure you have Bash Shell selected form the shell type dropdown

![Alt text](../../../../media/bashshell.png?raw=true "Optional Title")

## Git Bash

1. Install Visual Studio code: [Download Visual Studio Code - Mac, Linux, Windows](https://code.visualstudio.com/download)
2. Install Azure CLI: [Install the Azure CLI for Windows | Microsoft Learn](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli-windows?tabs=azure-cli)
3. Install GIT: [Git (git-scm.com)](https://git-scm.com/)
4. Install jq utility: [Download jq (jqlang.github.io)](https://jqlang.github.io/jq/download/)
 Please note that you will have to rename jq-win64.exe to jq.exe and add it to PATH.
5. Install Java 17: [Download Microsoft build of OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-17)
6. The environment variable **JAVA_HOME** should be set to the path of the JDK installation. The directory specified by this path should have bin, jre and lib among its subdirectories. Further, ensure your **PATH** variable contains the directory `${JAVA_HOME}/bin`. To test, type which `javac` into bash shell ensure the resulting path points to a file inside `${JAVA_HOME}/bin`.
7. Install maven: [Maven – Installing Apache Maven](https://maven.apache.org/install.html)
8. Create a folder by any name for this lab, eg: `ase-lab`
9. Open Visual Studio Code > File > Open Folder > select the folder you created in step 6
10. Open Visual Studio Code > Terminal > New Terminal > Click on the + at the lower right and select GitBash

![Alt text](../../../../media/gitbash.png?raw=true "Git Bash in VS Code Terminal")

## Prepare your environment (Codespaces/Cloud Shell/Git Bash) for deployments

These steps should be completed from within the terminal of your VS Code in Github Codespaces or bash shell if you are using Cloud Shell or Git Bash

Execute the following command to clone the repo for the lab (cloud shell/git bash users)

```shell
git clone https://github.com/Azure-Samples/acme-fitness-store.git
```

### Install the Azure CLI Spring Extension (Git Bash and Cloud Shell)

Install the Azure Spring Apps extension for the Azure CLI using the following command

```shell
az extension add --name spring
```

If the extension is already installed, update it with the following command

```shell
az extension update --name spring
```

### Update Environment Variables
```shell
cd acme-fitness-store/azure-spring-apps-enterprise
cp ./scripts/setup-env-variables-template.sh ./scripts/setup-env-variables.sh
code ./scripts/setup-env-variables.sh # Use the editor of your choice
```

```shell
export SUBSCRIPTION=CHANGEME               # replace it with your subscription-id (quote is not required)
export RESOURCE_GROUP=CHANGEME             # choose a unique name if the moderator doesn't provide it
export SPRING_APPS_SERVICE=CHANGEME        # choose a unique name if the moderator doesn't provide it
export LOG_ANALYTICS_WORKSPACE=CHANGEME    # choose a unique name if the moderator doesn't provide it
export REGION=CHANGEME                     # The region where you are running this workshop
```

- To get the Subscription ID, go to Azure portal, in search bar type subscriptions. The results should display your subscription and its id.
- RESOURCE_GROUP name will be provided by your workshop moderator
- SPRING_APPS_SERVICE name will be provided by your workshop moderator

This env file comes with default values that were provided as part of arm template. It is recommended to leave the values as-is for the purpose of this workshop. If for any reason you updated these default values in the arm template, those values need to be entereted in here.

Now, set the environment:

```shell
source ./scripts/setup-env-variables.sh
``` 

Verify environment variables are set 
```shell
echo $SUBSCRIPTION
echo $RESOURCE_GROUP 
echo $SPRING_APPS_SERVICE 
echo $REGION 
echo $CART_SERVICE_APP 
echo $IDENTITY_SERVICE_APP 
echo $ORDER_SERVICE_APP 
echo $PAYMENT_SERVICE_APP 
echo $CATALOG_SERVICE_APP 
echo $FRONTEND_APP 
```

> If you exit your Codespaces and reconnect in or get logged out of Cloud Shell, you need to re-run the command `source ./scripts/setup-env-variables.sh` to setup the environment.

### Login to Azure and set subscription

```shell
az login --use-device-code # Only for Codespaces and Git Bash
az account list -o table
az account set --subscription ${SUBSCRIPTION}
```

Set your default resource group name and cluster name using the following commands:

```shell
az configure --defaults \
    group=${RESOURCE_GROUP} \
    spring=${SPRING_APPS_SERVICE}
```
Verify defaults
```shell
az configure --list-defaults 
```
### Create Environment (optional)

Please consult with the instructor if these resources have already been created

Create Resource Group

```shell
az group create --name ${RESOURCE_GROUP} --location ${REGION} | jq '.properties.provisioningState' 
```

Accept legal terms and privacy statement for Azure Spring Apps Enterprise

```shell
az provider register --namespace Microsoft.SaaS
az term accept --publisher vmware-inc --product azure-spring-cloud-vmware-tanzu-2 --plan asa-ent-hr-mtr 
```
Create an instance of Azure Spring Apps Enterpise

```shell
az spring create --name ${SPRING_APPS_SERVICE} \
    --resource-group ${RESOURCE_GROUP} \
    --location ${REGION} \
    --sku Enterprise \
    --enable-application-configuration-service \
    --enable-service-registry \
    --enable-gateway \
    --enable-api-portal \
    --enable-alv \
    --enable-app-acc \
    --build-pool-size S2 
```


Verify you are successfully logged in with Azure CLI and set the correct environment variables:

```shell
az spring show -n $SPRING_APPS_SERVICE -g $RESOURCE_GROUP --query id
```

Create log analytics workspace 

(Note if the following fails, please create from portal)

```shell
az monitor log-analytics workspace create \
    --workspace-name ${LOG_ANALYTICS_WORKSPACE} \
    --location ${REGION} \
    --resource-group ${RESOURCE_GROUP} 
```

Create from portal

1. Login to Azure Portal > Search for Log Analytics Workspaces in the top search bar 


    ![Alt text](../../../../media/la.png?raw=true "Git Bash in VS Code Terminal")

2. Select Log Analytics workspaces
    Create Resource Group:same resource group that you used for Azure Spring Apps
    Name: any name
    Click Review + Create and create the workspace. 

    ![Alt text](../../../../media/la2.png?raw=true "Git Bash in VS Code Terminal")

Retrieve resource id for the workspace
```shell
export LOG_ANALYTICS_RESOURCE_ID=$(az monitor log-analytics workspace show \
    --resource-group ${RESOURCE_GROUP} \
    --workspace-name ${LOG_ANALYTICS_WORKSPACE} | jq -r '.id') 
```

Verify log analytics resource id is set
```shell
echo $LOG_ANALYTICS_RESOURCE_ID 

export SPRING_APPS_RESOURCE_ID=$(az spring show \
    --name ${SPRING_APPS_SERVICE} \
    --resource-group ${RESOURCE_GROUP} | jq -r '.id') 
```

Verify spring apps resource id is set 

```shell
echo $SPRING_APPS_RESOURCE_ID 
```
 

 **Note:** For Git Bash users, the following is needed because command may fail when resource IDs are misinterpreted as file paths because they begin with /. 

```shell
export MSYS_NO_PATHCONV=1 
```

Configure diagnostics settings
```shell
az monitor diagnostic-settings create --name "send-logs-and-metrics-to-log-analytics" \
    --resource ${SPRING_APPS_RESOURCE_ID} \
    --workspace ${LOG_ANALYTICS_RESOURCE_ID} \
    --logs '[ 
      {
          "category": "ApplicationConsole",
          "enabled": true,
          "retentionPolicy":
          {
              "enabled": false,
              "days": 0
          }
      },
      {
          "category": "SystemLogs",
          "enabled": true,
          "retentionPolicy":
          {
              "enabled": false,
              "days": 0
          }
      },
      {
          "category": "IngressLogs",
          "enabled": true,
          "retentionPolicy":
          {
              "enabled": false,
              "days": 0
          }
      }
    ]' \
    --metrics '[ 
      {
          "category": "AllMetrics",
          "enabled": true,
          "retentionPolicy":
          {
              "enabled": false,
              "days": 0
          }
      }
    ]'


 export MSYS_NO_PATHCONV=0
```

If you completed all the steps till here, you have successfully completed the following steps
* Accessing a dev environment via Github Codespaces
* Required az cli extensions are added and default subscription is set

➡️ Next guide: [02 - HOL 1 Hello World App](../02-hol-1-hello-world-app/README.md)
