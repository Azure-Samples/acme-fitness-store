本セクションでは、下記の 2 つの作業を行います

 - 事前に作成済みの ARM テンプレートを使用して、ワークショップに必要なリソースを作成
 - 事前に設定済みの Github Codespaces を起動

## ARM テンプレートを使用して Azure リソースを作成

前のセクションで述べたように、このワークショップを実行するためには、下記のようにたくさんの Azure リソースを作成します。ただし、このワークショップの目標は、基盤のインフラストラクチャではなく、アプリケーションやサービス関連の作業に重点を置くため、事前に環境構築に必要な Azure ARM テンプレートを用意しています。

 - Resource Group
 - Azure Cache for Redis
 - Azure SQL for Postgres Flexible Server
 - Azure Key Vault
 - Log Analytics workspace
 - Application Insights workspace
 
下記のボタンを右クリックしてください。そして ``[リンクを新しいタブで開く]`` を選択してください。画面上のフォームに入力する必須項目がたくさんあるため、新しいタブで作業をする事で入力値に対するガイダンスが簡単にできるようになるためです。

[![Deploy to Azure](images/deploybutton.svg)](https://portal.azure.com/#create/Microsoft.Template/uri/https%3a%2f%2fraw.githubusercontent.com%2fAzure-Samples%2facme-fitness-store%2fAzure%2fazure-spring-apps-enterprise%2fworkshops%2fazure-spring-apps-enterprise%2ffull%2f03-workshop-environment-setup%2facmedeploy.json)

各フィールドの横にある小さな情報アイコン (ℹ️) をクリックすると、各フィールドに対する説明が表示されます。デフォルト値が設定されているフィールドは、デフォルト値を使用することをお勧めします。ここで入力が必須のフィールドは、``ObjectId``です。この値を取得するためには、次の手順を行なってください。

1. サービスのタブで「Microsoft Entra ID」を選択
2. 左側のメニューから ``Users`` のリンクを見つけクリック
3. 検索バーでご自身の名前を検索し、ご自身の名前をクリック
4. プロパティの一覧から Object ID をコピー

上記で、[Object ID] の値をコピーした後、ARM テンプレートの [Object ID] フィールドに値を貼り付けます。

### 補足 (省略可能)

Azure Key Vault は一旦作成すると、リソースを削除した後も、デフォルトで 90 日間設定情報が保護されます。
そこで、本ワークショップの ARM テンプレートを使用して複数回リソースを作成する場合は、リソース名が競合し作成に失敗する場合があります。
複数回実施する場合は、Project Name の名前を変更するか、もしくは ARM テンプレートの下記の値を修正する事をおすすめします。具体的には ARM テンプレートの画面から `Edit template` をクリックし、`Variables` を開いてください。デフォルトで `0,4` と記載されている箇所をすべて下記のように `0,5` などのように数値を変更してください。全体で 24 文字が許容されているため、最大で `0,6` まで変更可能です。

```json
    "variables": {
        "resourceGroupName": "[concat(parameters('projectName'), '-rg')]",
        "dbName": "[concat(parameters('projectName'), '-db-', substring(uniqueString(subscription().id, parameters('projectName')),0,5))]",
        "cacheName": "[concat(parameters('projectName'), '-cache-', substring(uniqueString(subscription().id, parameters('projectName')),0,5))]",
        "keyVaultName": "[concat(parameters('projectName'), '-kv-', substring(uniqueString(subscription().id, parameters('projectName')),0,5))]",
        "logAnalyticsWorkspaceName": "[concat(parameters('projectName'),'-la-',substring(uniqueString(subscription().id, parameters('projectName')),0,5))]",
        "appInsightsName": "[concat(parameters('projectName'),'-insights-',substring(uniqueString(subscription().id, parameters('projectName')),0,5))]"
    },
```

**Next** をクリックし、次の画面に進みます。次の画面で **Create** をクリックします

上記のデプロイは、完了するまでにしばらく時間がかかります (25 分から 30 分)。この手順が完了するまで待つ必要はありません。次のセクションに進むことができます。ただし定期的に、Azure ポータルでこの手順の完了を確認してください。

この手順が正常に完了すると、リソース・グループと関連する全リソースが自動的に作成されます。全リソースが作成されていることを Azure Portal で確認してください。Azure Portal の [ホーム] ボタンに移動し、サブスクリプションをクリックして、左側の [リソース グループ] リンクをクリックします。下記のような画面が表示されます。

![Resource Group](images/arm-resourcegroup.png)

## Github Codspaces を起動

このワークショップは、Github Codespaces 上で開発環境を整え、以降の作業を進めます。コマンドは通常のシェルの環境からも実行できますが、Azure Spring Apps Enterprise を初めて操作する方は、環境依存の問題を排除するためにも Github Codespaces から実行することをお勧めします。

1. GitHub Codespaces から [Azure サンプル](https://github.com/Azure-Samples/) にアクセスするため、ご自身の GitHub ID をワークショップのコーディネーターにお伝えください。コーディネーターが、共有した ID を組織に追加し、[Codespaces] のオプションを表示する許可を割り当てます。

2. 組織に追加されたと表示された後、[https://github.com/Azure-Samples/acme-fitness-store/tree/Azure](https://github.com/Azure-Samples/acme-fitness-store/tree/Azure) に移動し、[コード]ボタンをクリックしてください。リストされているオプションとして「Codespaces」を実行できるようになっています。Codespaces のオプションが表示されない場合、Azure-Samples の組織に GitHub の ID が追加されていないかもしくは、まだアクティブになっていない可能性があります。表示されない場合は、ワークショップのコーディネーターにお声掛けください。

3. 上記の手順が成功した場合、Codespaces の中で VSCode のターミナルを開くことができるようになります。詳細は [Codespaces](https://docs.github.com/codespaces) をご参照ください。

	この Codespaces の環境には、次のソフトウェアがインストールされています。

	1. [JDK 17](https://docs.microsoft.com/java/openjdk/download?WT.mc_id=azurespringcloud-github-yoterada#openjdk-17)

		```shell
		java -version
		```

	2. `JAVA_HOME` の環境変数は、JDK インストール・パスが設定されています。また、`PATH` 変数に `${JAVA_HOME}/bin` ディレクトリが含まれていることを確認してください。パスの設定が正しく設定されているか確認するために、シェルのターミナルで `which java` コマンドを実行し、結果のパスが `${JAVA_HOME}/bin` のファイルを指していることを確認してください。

		```shell
		echo $JAVA_HOME 
		echo $PATH
		which java
		```
	   
	3. [Azure CLI version 2.31.0 もしくはそれ以降](https://docs.microsoft.com/cli/azure/install-azure-cli?view=azure-cli-latest)：現在インストールされている Azure CLI のバージョンを確認するためには、下記のコマンドを実行します。

		```shell
		which az
		az --version
		```

	4. GitHub codespaces を起動した際、古いソースコードやドキュメントを参照している可能性があるため、ソースコードやドキュメントを最新の情報に更新してください。

		```shell
		git pull
		```

### デプロイ用の環境設定の準備

下記の手順は、上記の手順 1 が正常に完了した後に実行してください。一部のリソース、例えばリソース・グループや Key Vault, Log Analytics などは 4〜5分以内に作成されますが、完全に完了するまでに 25 〜 30 分ほどかかります。作成したリソースを使用して、以降の手順を進めてください。

この手順と次の手順は、Github Codespaces の VS Code のターミナル内から実行してください。

レポジトリ内で提供するテンプレートをコピーして、環境変数の設定を含む bash スクリプトを作成します。

```shell
cd azure-spring-apps-enterprise/
cp ./scripts/setup-env-variables-template.sh ./scripts/setup-env-variables.sh
```


`./scripts/setup-env-variables.sh` ファイルを開いて環境変数を更新してください。

```shell
export SUBSCRIPTION='subscription-id'                 # ご自身の Subscription ID に置き換えてください
export RESOURCE_GROUP='resource-group-name'    # 既存のリソース・グループ名もしくは以降の手順で作成するリソース・グループ名
export SPRING_APPS_SERVICE='azure-spring-apps-name'   # 次の手順で作成する Azure Spring Apps 用のサービス名
export LOG_ANALYTICS_WORKSPACE='log-analytics-name'   # 既存のワークスペース名もしくは以降の手順で作成するワークスペース名
export REGION='region-name'                           # Enterprise をインストールする場所
```

- サブスクリプション ID を取得するために、Azure Portal の検索フィールドで「サブスクリプション」と入力してください。検索結果に、サブスクリプションと ID が表示されます

この env ファイルは、ARM テンプレート中で定義したデフォルト値が記載されています。仮に何らかの理由で ARM テンプレートを修正しデフォルト値を変更した場合は、その値に修正してください。

### Azure にログイン

Azure CLI でログインし、有効なサブスクリプションを選択します。Codespaces 上の VS Code のターミナルで、次のコマンドを実行してください。

```shell
source ./scripts/setup-env-variables.sh
``` 

次に下記のコマンドを実行してください。

```shell
az login --use-device-code
az account list -o table
az account set --subscription ${SUBSCRIPTION}
```

次に、Azure Spring Apps Enterprise を利用するための法律条項とプライバシーに関する声明に同意してください。

> 注: 下記のコマンドは、利用するサブスクリプションで一度も Azure Spring Apps Enterprise のインスタンスを作成したことがない場合にのみ必要です。

```shell
az provider register --namespace Microsoft.SaaS
az term accept --publisher vmware-inc --product azure-spring-cloud-vmware-tanzu-2 --plan asa-ent-hr-mtr
```

## Azure Spring Apps Enterprise インスタンスの作成

このセクションでは、Azure CLI を使用して Azure Spring Apps Enterprise のインスタンスを作成します。

Azure Spring Apps のサービスに割り当てる名前を準備してください。名前は 4〜32 文字の長さで、小文字、数字、ハイフンのみを使用できます。サービス名の最初の文字は文字で、最後の文字は文字または数字の必要があります。

名前は ```./scripts/setup-env-variables.sh``` ファイル中の SPRING_APPS_SERVICE という環境変数で定義します。

### Azure Spring Apps Enterprise のインスタンスを作成

下記のコマンドを実行し、Azure Spring Apps Enterprise のインスタンスを作成します。

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
> 注: サービス・インスタンスのデプロイには約 10〜15 分かかります。上記のコマンドは、Application Config Service、Service Registry、Gateway、および API Portal を有効にする設定も記載しています。これらのサービスは、以降のセクションで、マイクロサービス・アプリケーションを紹介する際に説明します。詳細は気にせずに上記のコマンドを実行してください。

上記のコマンドが完了した後、下記のコマンドを実行してください。

下記のコマンドを実行すると、デフォルトのリソース・グループ名とクラスター名を設定します。

```shell
az configure --defaults \
    group=${RESOURCE_GROUP} \
    location=${REGION} \
    spring=${SPRING_APPS_SERVICE}
```

以上の手順を全て完了すると、下記のリソースが正常に作成/インストールされています

* Github Codespaces を利用した開発環境へのアクセス
* ARM テンプレートを利用してワークショップに必要な全リソースのインストール
* ASA-E を操作するために必要な Azure CLI の拡張機能の追加と、デフォルトのサブスクリプション設定


⬅️ 前のワークショップ: [02 - Azure Spring Apps Enterprise の概要](../02-asa-e-introduction/README.md)

➡️ 次のワークショップ: [04 - Log Analytics のセットアップ](../04-log-analytics-setup/README.md)
