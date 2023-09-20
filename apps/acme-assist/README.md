# Enhance with Azure OpenAI

## Prerequisites
- JDK 17
- Maven
- Azure CLI (>= 2.51.0)
- An Azure subscription with access granted to Azure OpenAI (see more [here](https://customervoice.microsoft.com/Pages/ResponsePage.aspx?id=v4j5cvGGr0GRqy180BHbR7en2Ais5pxKtso_Pz4b1_xUOFA5Qk1UWDRBMjg0WFhPMkIzTzhKQ1dWNyQlQCN0PWcu))


## Prepare the Environment Variables
1. Please navigate to the root folder of this cloned repository.
1. Run `cp azure-spring-apps-enterprise/scripts/setup-env-variables-template.sh azure-spring-apps-enterprise/scripts/setup-env-variables.sh` and update the values in `setup-env-variables.sh` with your own values.
1. Run `cp azure-spring-apps-enterprise/scripts/setup-ai-env-variables-template.sh azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh` and update the values in `setup-ai-env-variables.sh` with your own values.


## Prepare Azure OpenAI Service

1. Run the following command to create an Azure OpenAI resource in the the resource group.

   ```bash
   source ./azure-spring-apps-enterprise/scripts/setup-env-variables.sh
   export OPENAI_RESOURCE_NAME=<choose-a-resource-name>
   az cognitiveservices account create \
      -n ${OPENAI_RESOURCE_NAME} \
      -g ${RESOURCE_GROUP} \
      -l eastus \
      --kind OpenAI \
      --sku s0 \
      --custom-domain ${OPENAI_RESOURCE_NAME}   
   ```

1. Create the model deployments for `text-embedding-ada-002` and `gpt-35-turbo-16k` in your Azure OpenAI service.
   ```bash
   az cognitiveservices account deployment create \
      -g ${RESOURCE_GROUP} \
      -n ${OPENAI_RESOURCE_NAME} \
      --deployment-name text-embedding-ada-002 \
      --model-name text-embedding-ada-002 \
      --model-version "2"  \
      --model-format OpenAI

    az cognitiveservices account deployment create \
      -g ${RESOURCE_GROUP} \
      -n ${OPENAI_RESOURCE_NAME} \
      --deployment-name gpt-35-turbo-16k \
      --model-name gpt-35-turbo-16k \
      --model-version "0613"  \
      --model-format OpenAI \
      --sku Standard \
      --capacity 120
   ```


## (Optional) Preprocess the data into the vector store

Before building the `assist-service` service, we need to preprocess the data into the vector store. The vector store is a file that contains the vector representation of each product description. There's already a pre-built file `vector_store.json` in the repo so you can skip this step. If you want to build the vector store yourself, please run the following commands:
```bash
source ./azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh
cd apps/acme-assist
./preprocess.sh data/bikes.json,data/accessories.json src/main/resources/vector_store.json
```


## Build and deploy to Azure Spring Apps

1. Prepare the new sample data and images:
   ```bash
   ./apps/acme-assist/prepare_data.sh
   ```
1. Redeploy `catalog-service` with the new resources:
    ```bash
    source ./azure-spring-apps-enterprise/scripts/setup-env-variables.sh
    az spring app deploy --name ${CATALOG_SERVICE_APP} \
    --config-file-pattern catalog/default \
    --source-path apps/acme-catalog \
    --build-env BP_JVM_VERSION=17
    ```
1. Deploy the new ai service `assist-service` :
    ```bash
    source ./azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh
    az spring app create --name ${AI_APP} --instance-count 1 --memory 1Gi
    az spring gateway route-config create \
        --name ${AI_APP} \
        --app-name ${AI_APP} \
        --routes-file azure-spring-apps-enterprise/resources/json/routes/assist-service.json
    
    az spring app deploy --name ${AI_APP} \
        --source-path apps/acme-assist \
        --build-env BP_JVM_VERSION=17 \
        --env \
            SPRING_AI_AZURE_OPENAI_ENDPOINT=${SPRING_AI_AZURE_OPENAI_ENDPOINT} \
            SPRING_AI_AZURE_OPENAI_APIKEY=${SPRING_AI_AZURE_OPENAI_APIKEY} \
            SPRING_AI_AZURE_OPENAI_MODEL=${SPRING_AI_AZURE_OPENAI_MODEL} \
            SPRING_AI_AZURE_OPENAI_EMBEDDINGMODEL=${SPRING_AI_AZURE_OPENAI_EMBEDDINGMODEL}
    ```
