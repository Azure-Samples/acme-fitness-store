# Azure Spring Apps Enterprise に AI アシスト・アプリをデプロイ

1. Azure Spring Apps と AI 用の環境変数を設定します

   ```bash
   source ./azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh
   source ./azure-spring-apps-enterprise/scripts/setup-env-variables.sh
   ```

1. Azure Spring Apps に新しい AI サービス `assist-service` 用のアプリ環境を作成します。

    ```bash
    az spring app create \
      --service ${SPRING_APPS_SERVICE} \
      --resource-group ${RESOURCE_GROUP} \
      --name ${AI_APP} \
      --instance-count 1 \
      --memory 1Gi
    ```

1.  Spring Cloud Gateway で `assist-service` に対するルーティングを設定します。

    ```bash
    az spring gateway route-config create \
        --service ${SPRING_APPS_SERVICE} \
        --resource-group ${RESOURCE_GROUP} \
        --name ${AI_APP} \
        --app-name ${AI_APP} \
        --routes-file azure-spring-apps-enterprise/resources/json/routes/assist-service.json
    ```
    
1. アプリケーションをデプロイします

    ```bash
    az spring app deploy --name ${AI_APP} \
        --service ${SPRING_APPS_SERVICE} \
        --resource-group ${RESOURCE_GROUP} \
        --source-path apps/acme-assist \
        --build-env BP_JVM_VERSION=17 \
        --env \
          AZURE_OPENAI_ENDPOINT=${SPRING_AI_AZURE_OPENAI_ENDPOINT} \
          AZURE_OPENAI_APIKEY=${SPRING_AI_AZURE_OPENAI_APIKEY} \
          AZURE_OPENAI_CHATDEPLOYMENTID=${SPRING_AI_AZURE_OPENAI_MODEL} \
          AZURE_OPENAI_EMBEDDINGDEPLOYMENTID=${SPRING_AI_AZURE_OPENAI_EMBEDDINGMODEL}
    ```

1. ブラウザで再度 `acme-fitness` アプリケーションをテストします。`ASK TO FITASSIST` に移動し、アシスタントと会話します。

   ```
   通勤用の自転車が必要です。
   ```

   ![A screenshot of the ACME Fitness Store.](../../../../../../media/homepage.png)

1. Assist アプリケーションで生成された出力を確認します

   ![A screenshot of the ACME Fitness Store with FitAssist](../../../../../../media/homepage-fitassist.png)

1. おめでとうございます！多言語対応の電子商取引アプリケーションに AI の機能を追加する事ができました。

## 備考：

今回のクイックスタートは、Azure CLI を使用して Azure Spring Apps に多言語アプリケーションをデプロイしました。
また、エンタープライズティアで VMware Tanzu コンポーネントを設定しました。
Azure Spring Apps や VMware Tanzu コンポーネントについてさらに詳しく知りたい場合は、下記をご覧ください：

* [Azure Spring Apps](https://azure.microsoft.com/services/spring-cloud/)
* [Azure Spring Apps docs](https://docs.microsoft.com/azure/spring-cloud/quickstart-provision-service-instance-enterprise?tabs=azure-portal)
* [Deploy Spring Apps from scratch](https://github.com/microsoft/azure-spring-cloud-training)
* [Deploy existing Spring Apps](https://github.com/Azure-Samples/azure-spring-cloud)
* [Azure for Java Cloud Developers](https://docs.microsoft.com/en-us/azure/java/)
* [Spring Cloud Azure](https://spring.io/projects/spring-cloud-azure)
* [Spring Cloud](https://spring.io/projects/spring-cloud)
* [Spring Cloud Gateway](https://docs.vmware.com/en/VMware-Spring-Cloud-Gateway-for-Kubernetes/index.html)
* [API Portal](https://docs.vmware.com/en/API-portal-for-VMware-Tanzu/index.html)
