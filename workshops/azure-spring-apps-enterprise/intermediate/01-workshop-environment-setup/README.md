## Launch Github Codspaces
This workshop leverages Github Codespaces to provide a development environment for running the instructions. Certainly you can run these instruction from a shell. But for people trying to get familiar with ASA-E for the first time, to rule out any environment related issues we recommend using Github Codespaces.

1. The first step in getting access to github codespaces option for [Azure Samples](https://github.com/Azure-Samples/) is to share your github id with the workshop co-ordinator. They will add you to the organization and assign you permissions that makes the Codespaces option visible.

2. Upon getting the confirmation that you are added to the Org, navigate to https://github.com/Azure-Samples/acme-fitness-store/tree/Azure, click "Code" button. You should be able to "Codespaces" as an option listed. If you do not see that option listed, most probably you are not added to [Azure-Samples](https://github.com/Azure-Samples/) org or your github id is still not active in this org. Please discuss this issue with your workshop co-ordinator. If invitation was sent but it is not in your mailbox, you can visit https://github.com/Azure-Samples/acme-fitness-store/invitations to accept it directly.

3. Assuming the above steps are succesful, you should be able to open a terminal inside VS Code that opens up in Codespaces. Refer to this link to understand more about [Codespaces](https://github.com/CodeSpaces). This Codespace comes installed with the following software:
   1. * [JDK 17](https://docs.microsoft.com/java/openjdk/download?WT.mc_id=azurespringcloud-github-judubois#openjdk-17)
   2. * The environment variable `JAVA_HOME` should be set to the path of the JDK installation. The directory specified by this path should have `bin`, `jre`, and `lib` among its subdirectories. Further, ensure your `PATH` variable contains the directory `${JAVA_HOME}/bin`. To test, type `which javac` into bash shell ensure the resulting path points to a file inside `${JAVA_HOME}/bin`.
   3. * [Azure CLI version 2.31.0 or higher](https://docs.microsoft.com/cli/azure/install-azure-cli?view=azure-cli-latest) version 2.31.0 or later. You can check the version of your current Azure CLI installation by running:

    ```bash
    az --version
    ```

### Prepare your environment for deployments

This and following steps should be completed from within the terminal of your VS Code in Github Codespaces.

Open `./scripts/setup-env-variables.sh` and update the following variables:

```shell
export SUBSCRIPTION=CHANGEME                 # replace it with your subscription-id (quote is not required)
export RESOURCE_GROUP=CHANGEME           # existing resource group that will be provided by your workshop moderator
export SPRING_APPS_SERVICE=CHANGEME   # A unique name of the service that will be provided by your workshop moderator
```

- To get the Subscription ID, go to Azure portal, in search bar type subscriptions. The results should display your subscription and its id.
- RESOURCE_GROUP name will be provided by your workshop moderator
- SPRING_APPS_SERVICE name will be provided by your workshop moderator

This env file comes with default values that were provided as part of arm template. It is recommended to leave the values as-is for the purpose of this workshop. If for any reason you updated these default values in the arm template, those values need to be entereted in here.

Now, set the environment:

```shell
source ./scripts/setup-env-variables.sh
``` 

### Login to Azure

Login to the Azure CLI and choose your active subscription. In the terminal of VS Code in Codespace, run the below commands. `--use-device-code` parameter for login is mandatory in Codespaces environment.

```shell
az login --use-device-code
az account list -o table
az account set --subscription ${SUBSCRIPTION}
```

Set your default resource group name and cluster name using the following commands:

```shell
az configure --defaults \
    group=${RESOURCE_GROUP} \
    location=${REGION} \
    spring=${SPRING_APPS_SERVICE}
```

If you completed all the steps till here, you have successfully completed the following steps
* Accessing a dev environment via Github Codespaces
* Required az cli extensions are added and default subscription is set

➡️ Next guide: [02 - HOL 1 Hello World App](../02-hol-1-hello-world-app/README.md)
