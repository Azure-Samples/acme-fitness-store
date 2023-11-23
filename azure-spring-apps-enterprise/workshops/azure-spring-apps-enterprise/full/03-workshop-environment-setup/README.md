There are two things that will be taken care of in this section. 
 - Running pre-built ARM template to launch the required resources for this workshop
 - Launching pre-configured Github Codespaces 

## Create Azure Resources using ARM template

As we had already noted in the prior sections and also as we go to next sections, there are quite a few number of resources that need to be in place to execute this workshop. As the goal of this workshop is to focus more on the app/service related tasks and less on the underlying infrastructure tasks, we are providing an Azure ARM template that will provision the required resources.

 - Resource Group
 - Azure Cache for Redis
 - Azure SQL for Postgres
 - Azure Key Vault
 - Log Analytics workspace
 - Application Insights workspace
 

Please right click on the below button and choose the Open in new tab option. The reason is there are quite a number of fields that need to be populated in that form and we are providing guidance on the values to populate with.

[![Deploy to Azure](images/deploybutton.svg)](https://portal.azure.com/#create/Microsoft.Template/uri/https%3a%2f%2fraw.githubusercontent.com%2fAzure-Samples%2facme-fitness-store%2fAzure%2fazure-spring-apps-enterprise%2fworkshops%2fazure-spring-apps-enterprise%2ffull%2f03-workshop-environment-setup%2facmedeploy.json)

To know about the description of the fields, click on the little info icon next to every field. For the fields where default value is populated, the recommendation is to use the default for the first time use. The only field that needs to be populated in here is the ``ObjectId``. To get the value for this field, perform the below steps

- In Services tab, search for Azure Active Directory
- On the left side, find ``Users`` link and click on that.
- In the search bar, search for your name and click on your name
- Copy the Object ID from the list of properties 

Once you copied the value for Object ID, paste this in the arm template value for Object ID field.

Click **Save** go to next screen. Click **Create** on next screen

The above deployment takes a while (25-30 mins) to complete. You do not have to wait for this step to complete. You can go ahead with the steps in next section. However it will be useful to check the completion of this step in Azure portal.

After successful completion of this step verify that the resource group and all the relevant resources are created in the resource group. Navigate to Home button in Azure portal, click on your subscription and on the left click on "Resource Groups" link. If you left all the default values in the ARM template, this page should look like the screenshot below

[Resource Group](images/arm-resourcegroup.png)

## Launch Github Codspaces
This workshop leverages Github Codespaces to provide a development environment for running the instructions. Certainly you can run these instruction from a shell. But for people trying to get familiar with ASA-E for the first time, to rule out any environment related issues we recommend using Github Codespaces.

1. The first step in getting access to github codespaces option for [Azure Samples](https://github.com/Azure-Samples/) is to share your github id with the workshop co-ordinator. They will add you to the organization and assign you permissions that makes the Codespaces option visible.

2. Upon getting the confirmation that you are added to the Org, navigate to https://github.com/Azure-Samples/acme-fitness-store/tree/Azure, click "Code" button. You should be able to "Codespaces" as an option listed. If you do not see that option listed, most probably you are not added to [Azure-Samples](https://github.com/Azure-Samples/) org or your github id is still not active in this org. Please discuss this issue with your workshop co-ordinator.

3. Assuming the above steps are successful, you should be able to open a terminal inside VS Code that opens up in Codespaces. Refer to this link to understand more about [Codespaces](https://docs.github.com/codespaces). This Codespace comes installed with the following software:
   1. * [JDK 17](https://docs.microsoft.com/java/openjdk/download?WT.mc_id=azurespringcloud-github-judubois#openjdk-17)
   2. * The environment variable `JAVA_HOME` should be set to the path of the JDK installation. The directory specified by this path should have `bin`, `jre`, and `lib` among its subdirectories. Further, ensure your `PATH` variable contains the directory `${JAVA_HOME}/bin`. To test, type `which javac` into bash shell ensure the resulting path points to a file inside `${JAVA_HOME}/bin`.
   3. * [Azure CLI version 2.31.0 or higher](https://docs.microsoft.com/cli/azure/install-azure-cli?view=azure-cli-latest) version 2.31.0 or later. You can check the version of your current Azure CLI installation by running:

    ```bash
    az --version
    ```

### Prepare your environment for deployments

This step should be completed only after the successful completion of the above step 1. However as step 1 takes 25-30 minutes to fully complete, but some resources like resource-group and within that key-vault, log-analytics and operational-insights should be completed within 4-5 mins. You can use these completed resources as a reference to complete the steps below.

This and following steps should be completed from within the terminal of your VS Code in Github Codespaces.

Create a bash script with environment variables by making a copy of the supplied template:

Open `./scripts/setup-env-variables.sh` and update the following variables:

```shell
export SUBSCRIPTION=CHANGE-ME                 # replace it with your subscription-id
export LOG_ANALYTICS_WORKSPACE=acme-fitness-la-CHANGE-ME   # By replacing CHANGE-ME with the last 4 characters you see in Log Analytics workspace name in Azure portal.
```

- To get the Subscription ID, go to Azure portal, in search bar type subscriptions. The results should display your subscription and its id.

This env file comes with default values that were provided as part of arm template. It is recommended to leave the values as-is for the purpose of this workshop. If for any reason you updated these default values in the arm template, those values need to be entered in here.

Now, set the environment:

```shell
source ./scripts/setup-env-variables.sh
``` 

### Login to Azure

Login to the Azure CLI and choose your active subscription. In the terminal of VS Code in Codespace, run the below commands

```shell
az login --use-device-code
az account list -o table
az account set --subscription ${SUBSCRIPTION}
```

Accept the legal terms and privacy statements for the Enterprise tier.

> Note: This step is necessary only if your subscription has never been used to create an Enterprise tier instance of Azure Spring Apps.

```shell
az provider register --namespace Microsoft.SaaS
az term accept --publisher vmware-inc --product azure-spring-cloud-vmware-tanzu-2 --plan asa-ent-hr-mtr
```

## Create ASA-E instance

In this section we are going to create an ASA-E instance using azure cli.


Prepare a name for your Azure Spring Apps service.  The name must be between 4 and 32 characters long and can contain only lowercase letters, numbers, and hyphens.  The first character of the service name must be a letter and the last character must be either a letter or a number. 

This name is stored as an environment variable, SPRING_APPS_SERVICE in ```./scripts/setup-env-variables.sh```. It is defaulted to acme-fitness. It is recommended to leave the default value for this workshop purpose. If you are changing the default name, please note that the name cannot exceed 32 characters.

### Create an instance of Azure Spring Apps Enterprise.

```shell
az spring create --name ${SPRING_APPS_SERVICE} \
    --resource-group ${RESOURCE_GROUP} \
    --location ${REGION} \
    --sku Enterprise \
    --enable-application-configuration-service \
    --enable-service-registry \
    --enable-gateway \
    --enable-api-portal \
    --build-pool-size S2 
```
> Note: The service instance will take around 10-15 minutes to deploy. You will notice in the above command have arguments to enable application-configuration-service, service-registry, gateway and api-portal. The significance of these services will be discussed in later sections when we introduce a demo microservices application. For now please go ahead and run the above command.

Please wait till the above command is complete before you start running any of the below commands.

Set your default resource group name and cluster name using the following commands:

```shell
az configure --defaults \
    group=${RESOURCE_GROUP} \
    location=${REGION} \
    spring=${SPRING_APPS_SERVICE}
```


If you completed all the steps till here, you have successfully created/installed the following resources
* Accessing a dev environment via Github Codespaces
* All the dependent resources required for the workshop are installed via an arm template.
* Required az cli extensions are added and default subscription is set

⬅️ Previous guide: [02 - ASA-E Introduction](../02-asa-e-introduction/README.md)

➡️ Next guide: [04 - Log Analytics Setup](../04-log-analytics-setup/README.md)
