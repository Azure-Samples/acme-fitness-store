# Workshop Environment Setup

## Prerequisites
- JDK 17
- Maven
- Azure CLI
- An Azure subscription with access granted to Azure OpenAI (request access to Azure OpenAI [here](https://customervoice.microsoft.com/Pages/ResponsePage.aspx?id=v4j5cvGGr0GRqy180BHbR7en2Ais5pxKtso_Pz4b1_xUOFA5Qk1UWDRBMjg0WFhPMkIzTzhKQ1dWNyQlQCN0PWcu))

> NOTE: Consult with your workshop instructor if the Azure subscription has been pre-provisioned for this workshop.

## Collect participant details

Please share with your instructor your details, e.g.
* Your first and last name
* Your email address
* Your company name
* Your Microsoft account that you will use to connect to Azure subscription, i.e. email address
* Your Github account

Workshop instructors will use this information to provision your access to the workshop environment.

## Prepare the Environment Variables

1. Clone this repository if you have not already done that, e.g.

   ```bash
   git clone https://github.com/Azure-Samples/acme-fitness-store
   ```

1. Please navigate to the root folder of this cloned repository, e.g.

   ```bash
   cd acme-fitness-store
   ```

1. Please consult with your workshop instructor if they have already prepared two environment
   variables files for this workshop, e.g.

   ```bash
   setup-env-variables.sh
   setup-ai-env-variables.sh
   ```

1. Copy the AI environment variables template file, e.g. 

   ```bash
   cp azure-spring-apps-enterprise/scripts/setup-ai-env-variables-template.sh azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh
   ```

1. Update the values in `azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh` with your own values, as configured in Azure OpenAI instance:
   - Name, e.g. `my-ai`
   - Endpoint, e.g. `https://my-ai.openai.azure.com`
   - Chat deployment ID, e.g. `gpt-35-turbo-16k``
   - Embedding deployment ID, e.g. `text-embedding-ada-002``
   - OpenAI API Key, to be updated once you create AI instance (in next step)


1. You might already have environment variables file from earlier labs - check with your instructor.
   Please copy the environment variables template file, e.g.
   ```bash
   cp azure-spring-apps-enterprise/scripts/setup-env-variables-template.sh azure-spring-apps-enterprise/scripts/setup-env-variables.sh
   ```

1. Updates the values in `azure-spring-apps-enterprise/scripts/setup-env-variables.sh` as directed by your instructor.

> Next: [02 - Prepare Azure Open AI](../02-prepare-azure-openai/README.md)
