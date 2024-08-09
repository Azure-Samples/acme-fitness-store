# ワークショップの環境セットアップ

## 前提条件

- JDK 17
- Python 3
- Maven
- Azure CLI
- Azure OpenAI へのアクセスが許可された Azure サブスクリプション（Azure OpenAIへのアクセスは[こちら](https://customervoice.microsoft.com/Pages/ResponsePage.aspx?id=v4j5cvGGr0GRqy180BHbR7en2Ais5pxKtso_Pz4b1_xUOFA5Qk1UWDRBMjg0WFhPMkIzTzhKQ1dWNyQlQCN0PWcu)でリクエストしてください）

> ご注意: Azure のサブスクリプションがこのワークショップのために事前に準備されているかどうか、ワークショップのインストラクターに確認してください。

## 参加者の詳細情報を収集

あなたの詳細な情報をインストラクターに共有してください

* あなたの苗字とお名前
* あなたのメールアドレス
* あなたの会社名
* Azure の接続に使用する Microsoft アカウント、つまりメールアドレス
* あなたの Github アカウント

ワークショップのインストラクターは、この情報を使用してワークショップ環境へのアクセスを設定します。

## 環境変数の準備

1. 事前に行っていない場合は、こちらのリポジトリをクローンしてください。

   ```bash
   git clone https://github.com/Azure-Samples/acme-fitness-store
   ```

1. クローンしたリポジトリのルートフォルダに移動してください。

   ```bash
   cd acme-fitness-store
   ```

1. ワークショップのインストラクターに、本ワークショップ用の 2 つの環境変数設定ファイルを用意しているか確認してください。

   ```bash
   setup-env-variables.sh
   setup-ai-env-variables.sh
   ```

1. AI の環境変数を設定するためのテンプレートファイルをコピーしてください。

   ```bash
   cp azure-spring-apps-enterprise/scripts/setup-ai-env-variables-template.sh azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh
   ```

1. `azure-spring-apps-enterprise/scripts/setup-ai-env-variables.sh`の値を、Azure OpenAIインスタンスで設定したご自身の値に修正してください：

   - 名前、例 `my-ai`
   - エンドポイント、例 `https://my-ai.openai.azure.com`
   - チャットのデプロイメントID、例 `gpt-35-turbo-16k`
   - 埋め込みのデプロイメントID、例 `text-embedding-ada-002`
   - OpenAI APIキー、AIインスタンスを作成した後に更新（次のステップ）

1. 別のワークショップを実施している場合、環境変数ファイルを既に作成している可能性があります - インストラクターにご確認ください。
   環境変数のテンプレートファイルをコピーしてください。

   ```bash
   cp azure-spring-apps-enterprise/scripts/setup-env-variables-template.sh azure-spring-apps-enterprise/scripts/setup--env-variables.sh
   ```

1. `azure-spring-apps-enterprise/scripts/setup-env-variables.sh`の値を、インストラクターの指示に従って修正してください。

> 次の作業: [02 - Azure Open AI の準備](../02-prepare-azure-openai/README.md)
