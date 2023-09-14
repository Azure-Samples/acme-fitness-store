# ベクトル・ストアでデータ処理 （省略可能）

`assist-service`サービスを構築する前に、ベクトル・ストアに対して、事前にデータを処理する必要があります。
ここで扱うベクトル・ストアは、ACME Fitness Store の各商品に関する説明を記載した、ベクトル表現を含むファイルです。
リポジトリ中に `vector_store.json` という名前の事前に構築したファイルがあります。
そこで、下記の処理をスキップすることも可能です。

仮に、ご自身でベクトル・ストアを構築したい場合は、以下のコマンドを実行してください：

   ```bash
   source ./azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh
   cd apps/acme-assist
   ./preprocess.sh data/bikes.json,data/accessories.json src/main/resources/vector_store.json
   cd ../../
   ```

> 次の作業: [04 - Azure Spring Apps Enterprise に AI アシスト・アプリをデプロイ](../04-build-and-deploy-assist-app-to-azure-spring-apps-enterprise/README.md)
