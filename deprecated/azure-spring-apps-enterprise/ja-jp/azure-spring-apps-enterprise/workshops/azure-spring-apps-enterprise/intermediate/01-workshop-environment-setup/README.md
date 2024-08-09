#### このワークショップは、複数の方法で実施可能です

- [GitHub Codespaces](#github-codespaces)
- [Cloud Shell](#cloud-shell)
- [Git Bash](#git-bash)

下記に選択した方法に応じた、それぞれの手順を記載します。

## Github Codespaces

Github Codespaces を利用して、開発環境・実行環境を構築可能です。コマンドは GitHub codespaces の Shell から実行できます。Azure Spring Apps Enterprise に初めて操作する方は、環境依存の問題を除外するため、Github Codespaces を使用することをお勧めします。

1. [Azure サンプル](https://github.com/Azure-Samples/) で提供する Github Codespaces にアクセスするために、GitHub ID をワークショップの担当者に共有してください。担当者は、GitHub Codespaces を表示する権限を参加者の皆様に割り当てます。

2. GitHu Codespaces へのアクセス許可が与えられた後、[https://github.com/Azure-Samples/acme-fitness-store/tree/Azure に移動し、「Code」ボタンをクリックしてください。リストされているオプションとして「codespaces」が実行できるようになっています。このオプションが表示されない場合は、Azure-Samples 組織に追加されていないか、Github ID が組織でまだアクティブになっていない可能性があります。この問題についてワークショップコーディネーターにご相談ください。招待状が送信されたメールが場合は、https://github.com/Azure-Samples/acme-fitness-store/invitations にアクセスして直接申請できます。

3. 上記の手順が成功した場合、Codespaces の VSCode 内でターミナルを開くことができるようになっています。Codespaces の詳細については、[こちら](https://docs.github.com/codespaces)をご参照ください。この Codespaces の環境では、次のソフトウェアがインストールされています。
   1. * [JDK 17](https://docs.microsoft.com/java/openjdk/download?WT.mc_id=azurespringcloud-github-judubois#openjdk-17)
   2. * 環境変数は、`JAVA_HOME` は JDK のインストール・パスを設定する必要があります。このパスで指定するディレクトリには、`bin`, `jre`, `lib` とそのサブディレクトリが必要です。さらに、`PATH` 変数に `${JAVA_HOME}/bin` ディレクトリが含まれていることも確認してください。テストするには、bashシェルに `which javac` 入力して、結果のパスが内のファイルを指していることを確認します。
   3. * [Azure CLI バージョン 2.31.0](https://docs.microsoft.com/cli/azure/install-azure-cli?view=azure-cli-latest) 以降のバージョン。現在の Azure CLI インストールのバージョンを確認するには、次のコマンドを実行してください。

    ```bash
    az --version
    ```

    #### 環境変数の更新
    ```shell
    cd acme-fitness-store/azure-spring-apps-enterprise
    cp ./scripts/setup-env-variables-template.sh ./scripts/setup-env-variables.sh -i
    code ./scripts/setup-env-variables.sh # Use the editor of your choice
    ```

    ```shell
    export SUBSCRIPTION=CHANGEME               # replace it with your subscription-id (quote is not required)
    export RESOURCE_GROUP=CHANGEME             # choose a unique name if the moderator doesn't provide it
    export SPRING_APPS_SERVICE=CHANGEME        # choose a unique name if the moderator doesn't provide it
    export LOG_ANALYTICS_WORKSPACE=CHANGEME    # choose a unique name if the moderator doesn't provide it
    export REGION=CHANGEME                     # The region where you are running this workshop
    ```

    - サブスクリプション ID を取得するには、Azure portal に移動し、検索バーから「サブスクリプション」と入力します。サブスクリプションとその ID が表示されます。
    - RESOURCE_GROUP 名はワークショップのモデレーターから提供されます
    - SPRING_APPS_SERVICE 名はワークショップのモデレーターから提供されます

    この env ファイルには、arm テンプレートの一部として提供されたデフォルト値が付属しています。このワークショップでは、値はそのままのデフォルト値にしておくことをお勧めします。何らかの理由で arm テンプレートでこれらの既定値を更新した場合は、これらの値をここで修正する必要があります。

    次に、環境を設定します。

    ```shell
    source ./scripts/setup-env-variables.sh
    ``` 

    環境変数が設定されていることを確認します

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

    > Codespaces を終了して Cloud Shell に再接続する場合、もしくはログアウトした場合は、`source ./scripts/setup-env-variables.sh` コマンドを再実行して環境変数を設定してください。

    #### Azure にログインしてサブスクリプションを設定する

    ```shell
    az login --use-device-code # Only for Codespaces and Git Bash
    az account list -o table
    az account set --subscription ${SUBSCRIPTION}
    ```

    下記のコマンドを実行して、Azure CLI を実行する際のデフォルトのリソース・グループ名とクラスター名を設定します。

    ```shell
    az configure --defaults \
        group=${RESOURCE_GROUP} \
        spring=${SPRING_APPS_SERVICE}
    ```

    デフォルト値を確認する

    ```shell
    az configure --list-defaults 
    ```

## Cloud Shell

Azure ポータルにログインし、クラウド・シェルから bash プロンプトを開く

![Alt text](../../../../../../media/cloudshell.png?raw=true "Optional Title")

ドロップダウンで、利用するシェルが Bash シェルとして選択されていることを確認してください

![Alt text](../../../../../../media/bashshell.png?raw=true "Optional Title")

## Git Bash

1. Visual Studio コードのインストール: [Download Visual Studio Code - Mac, Linux, Windows](https://code.visualstudio.com/download)
2. Azure CLI のインストール: [Install the Azure CLI for Windows | Microsoft Learn](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli-windows?tabs=azure-cli)
3. GIT のインストール: [Git (git-scm.com)](https://git-scm.com/)
4. jq コマンドをインストール: [Download jq (jqlang.github.io)](https://jqlang.github.io/jq/download/)
jq-win64.exe の名前を jq.exe に変更し PATH に追加してください
5. Java 17のインストール: [Download Microsoft build of OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-17)
6. 環境変数 **JAVA_HOME** は、JDK インストールのパスに設定してください。このパスで指定されたディレクトリには、bin、jre、lib およびサブディレクトリが含まれている必要があります。さらに、**PATH** 変数に `${JAVA_HOME}/bin` ディレクトリが含まれていることを確認してください。テストするには、bash　シェルで `which javac` と入力し、結果がファイルを指していることを確認します。

7. maven のインストール: [Maven – Installing Apache Maven](https://maven.apache.org/install.html)
8. このラボ用に任意のディレクトリ名でフォルダを作成します: 例：`ase-lab`
9. Visual Studio Code > ファイル> [フォルダーを開く] を開き>手順 8 で作成したフォルダーを選択します
10. Visual Studio Code > ターミナル>新しいターミナルを開く > 右下の + をクリックし、GitBash を選択します。

![Alt text](../../../../../../media/gitbash.png?raw=true "Git Bash in VS Code Terminal")

## デプロイ用の環境準備 (Codespaces/Cloud Shell/Git Bash) 

下記の手順は、Github Codespaces 上の VS Code のターミナル、もしくは Cloud Shell もしくは Git Bash の bash シェルから実行してください。

下記のコマンドを実行し、本ワークショップのリポジトリをクローンします (Cloud Shell/git bash を利用する場合)

```shell
git clone https://github.com/Azure-Samples/acme-fitness-store.git
```

### Azure CLI Spring Extension (Git Bash および Cloud Shell) のインストール

下記のコマンドを実行して、Azure CLI に Azure Spring Apps の拡張機能をインストールします

```shell
az extension add --name spring
```

拡張機能が既にインストールされている場合は、下記のコマンドで最新版に更新してください

```shell
az extension update --name spring
```

### 環境変数の更新

```shell
cd acme-fitness-store/azure-spring-apps-enterprise
cp ./scripts/setup-env-variables-template.sh ./scripts/setup-env-variables.sh -i
code ./scripts/setup-env-variables.sh # Use the editor of your choice
```

```shell
export SUBSCRIPTION=CHANGEME               # replace it with your subscription-id (quote is not required)
export RESOURCE_GROUP=CHANGEME             # choose a unique name if the moderator doesn't provide it
export SPRING_APPS_SERVICE=CHANGEME        # choose a unique name if the moderator doesn't provide it
export LOG_ANALYTICS_WORKSPACE=CHANGEME    # choose a unique name if the moderator doesn't provide it
export REGION=CHANGEME                     # The region where you are running this workshop
```

- サブスクリプション ID を取得するには、Azure Portal から検索バーで「サブスクリプション」と入力します。するとサブスクリプション ID が表示されます。
- RESOURCE_GROUP 名はワークショップのモデレーターから提供されます
- SPRING_APPS_SERVICE 名はワークショップのモデレーターから提供されます

この env ファイルには、arm テンプレートの一部として提供されたデフォルト値が付属しています。このワークショップでは、値はそのままのデフォルト値にしておくことをお勧めします。何らかの理由で arm テンプレートでこれらの既定値を更新した場合は、これらの値をここで修正する必要があります。

次に、環境を設定します。

```shell
source ./scripts/setup-env-variables.sh
``` 

環境変数が設定されていることを確認します

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

> Codespaces を終了して Cloud Shell に再接続する場合、もしくはログアウトした場合は、`source ./scripts/setup-env-variables.sh` コマンドを再実行して環境変数を設定してください。

### Azure にログインしサブスクリプションを設定

```shell
az login --use-device-code # Only for Codespaces and Git Bash
az account list -o table
az account set --subscription ${SUBSCRIPTION}
```

下記のコマンドを実行し、デフォルトのリソース グループ名とクラスター名を設定します。

```shell
az configure --defaults \
    group=${RESOURCE_GROUP} \
    spring=${SPRING_APPS_SERVICE}
```

デフォルト値を確認してください

```shell
az configure --list-defaults 
```
### 環境の作成 (省略可能)

これらのリソースがすでに作成されているか、インストラクターに相談してください

リソース・グループの作成

```shell
az group create --name ${RESOURCE_GROUP} --location ${REGION} | jq '.properties.provisioningState' 
```

Azure Spring Apps Enterprise の法律条項とプライバシーに関する声明に同意

```shell
az provider register --namespace Microsoft.SaaS
az term accept --publisher vmware-inc --product azure-spring-cloud-vmware-tanzu-2 --plan asa-ent-hr-mtr 
```

Azure Spring Apps Enterpise のインスタンスを作成する

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

Azure CLI で正常に作成されていることを確認してください。

```shell
az spring show -n $SPRING_APPS_SERVICE -g $RESOURCE_GROUP --query id
```

Log Analytics ワークスペースを作成してください
(以下のコマンドの実行に失敗した場合は、ポータルから作成してください)

```shell
az monitor log-analytics workspace create \
    --workspace-name ${LOG_ANALYTICS_WORKSPACE} \
    --location ${REGION} \
    --resource-group ${RESOURCE_GROUP} 
```

ポータルから作成する方法

1. Azure ポータルにログイン>上部の検索バーで Log Analytics ワークスペースを検索してください

    ![Alt text](../../../../../../media/la.png?raw=true "Git Bash in VS Code Terminal")

2. Log Analytics workspaces の選択
    Create Resource Group:　Azure Spring Apps と同じリソース・グループを指定してください
    Name: 任意の名前を指定してください
    Review + Create をクリックしワークスペースを作成してください

    ![Alt text](../../../../../../media/la2.png?raw=true "Git Bash in VS Code Terminal")

ワークスペースのリソース ID を取得してください

```shell
export LOG_ANALYTICS_RESOURCE_ID=$(az monitor log-analytics workspace show \
    --resource-group ${RESOURCE_GROUP} \
    --workspace-name ${LOG_ANALYTICS_WORKSPACE} | jq -r '.id') 
```

Log Analytics のリソース ID が設定されていることを確認してください

```shell
echo $LOG_ANALYTICS_RESOURCE_ID 

export SPRING_APPS_RESOURCE_ID=$(az spring show \
    --name ${SPRING_APPS_SERVICE} \
    --resource-group ${RESOURCE_GROUP} | jq -r '.id') 
```

Spring Apps のリソース ID が設定されていることを確認してください

```shell
echo $SPRING_APPS_RESOURCE_ID 
```
 
 **ご注意:**  Git Bash をご利用の方は、リソース ID が / で始まるためにファイルのパスとして誤って解釈され、コマンドの実行に失敗する可能性があります。そこで、下記のコマンドを実行してください

```shell
export MSYS_NO_PATHCONV=1 
```

診断設定を構成してください。

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
上記の手順をすべて完了すると、下記の作業が正常に完了しています

* Github codespaces を介した開発環境へのアクセス
* 必要な az cli 拡張機能を追加し、デフォルトのサブスクリプションの設定

➡️ 次のガイド: [02 - HOL 1 Hello World App](../02-hol-1-hello-world-app/README.md)

