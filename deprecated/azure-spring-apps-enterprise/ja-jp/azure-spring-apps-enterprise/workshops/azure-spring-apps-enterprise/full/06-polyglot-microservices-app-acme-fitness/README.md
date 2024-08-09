ここでは、Azure Spring Apps Enterprise のさまざまな機能を確認するために使用するサンプル・アプリケーションの概要について説明します。

下記の図は、ACME Fitness Store に関連するサービスを示します。Azure Spring Apps Enterprise 上で稼働するアプリケーションと依存する関連サービスを示しています。このワークショップを完了するまでに、ここで示すアーキテクチャを構築していきます。

![acme-fitness](images/end-end-arch.png)

このアプリケーションはいくつかのサービスから構成されています

* Java Spring Boot アプリケーション：
  * カタログサービス：取扱商品を取得するためのサービス
  * 支払いサービス：ユーザーの注文を処理し承認するための支払いサービス
  * ユーザーサービス：認証されたユーザーを参照するためのアイデンティティサービス

* Python アプリケーション：
  * カートサービス：ユーザーが購入する商品を選択するためのカートサービス

* ASP.NET Core アプリケーション：
  * 注文サービス：ユーザーのカートにある商品を購入するための注文サービス

* Node.js と静的 HTML
  * フロントエンド・サービス：フロントエンドのショッピング・アプリケーション

上記のアプリケーションに加えて、アーキテクチャ図に示すよう、下記のサービスも利用します。

 - Spring Cloud Gateway
 - Azure Active Directory
 - Azure Postgres 
 - Azure Cache for Redis
 - Azure Key Vault
 - Azure services for Monitoring and Logging

次のセクションでは、上記の設定方法について説明します。

⬅️ 前の作業: [05 - Hello World の単純な Spring Boot アプリのデプロイ](../05-hol-1-hello-world-app/README.md)

➡️ 次の作業: [07 - ASA-E コンポーネントの概要](../07-asa-e-components-overview/README.md)