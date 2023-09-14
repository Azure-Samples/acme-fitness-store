# Process data into vector store (Optional)

Before building the `assist-service` service, we need to preprocess the data into the vector store.
The vector store is a file that contains the vector representation of each product description.
There is already a pre-built file `vector_store.json` in the repo, so you can skip this step.

If you want to build the vector store yourself, please run the following commands:

   ```bash
   source ./azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh
   cd apps/acme-assist
   ./preprocess.sh data/bikes.json,data/accessories.json src/main/resources/vector_store.json
   cd ../../
   ```

> Next: [04 - Build and deploy assist app to Azure Spring Apps Enterprise](../04-build-and-deploy-assist-app-to-azure-spring-apps-enterprise/README.md)
