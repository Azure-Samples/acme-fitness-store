# ベクトル・ストアでデータ処理 （省略可能）

`assist-service`サービスを構築する前に、ベクトル・ストアに対して、事前にデータを処理する必要があります。
ここで扱うベクトル・ストアは、ACME Fitness Store の各商品に関する説明を記載した、ベクトル表現を含むファイルです。
リポジトリ中に `vector_store.json` という名前の事前に構築したファイルがあります。
そこで、下記の処理をスキップすることも可能です。

仮に、ご自身でベクトル・ストアを構築したい場合は、以下のコマンドを実行してください：

   ```bash
   export SPRING_AI_AZURE_OPENAI_ENDPOINT="your_azure_openai_endpoint"
   export SPRING_AI_AZURE_OPENAI_APIKEY="your_api_key"
   export SPRING_AI_AZURE_OPENAI_MODEL=gpt-35-turbo-16k
   export SPRING_AI_AZURE_OPENAI_EMBEDDINGMODEL=text-embedding-ada-002

   export AI_APP="assist-service"
 
   cd apps/acme-assist
   ./preprocess.sh data/bikes.json,data/accessories.json src/main/resources/vector_store.json
   cd ../../
   ```

> 次の作業: [04 - Azure Spring Apps Enterprise に AI アシスト・アプリをデプロイ](../04-build-and-deploy-assist-app-to-azure-spring-apps-enterprise/README.md)
