ここでは、前の作業でデプロイしたバックエンド・アプリを Application Config Service と Service Registry にバインドする方法を説明します。

下記に、Application Configuration Service と Service Registry にバインドする手順を示します。
- [1. Application Configuration Service の作成](#1-application-configuration-service-の作成)
  - [1.1. Application Configuration Service でアプリケーションの設定](#11-application-configuration-service-でアプリケーションの設定)
- [2. Service Registry にアプリケーションをバインド](#2-service-registry-にアプリケーションをバインド)


## 1. Application Configuration Service の作成]

アプリケーションから外部に格納する設定の実装を行う前に、まずその外部リポジトリを指す Application Configuration Service のインスタンスを作成する必要があります。ここでは、Azure CLI を使用して GitHub リポジトリを外部レポジトリとして利用する Application Configuration Service のインスタンスを作成します。

```shell
az spring application-configuration-service git repo add --name acme-fitness-store-config \
    --label main \
    --patterns "catalog/default,catalog/key-vault,identity/default,identity/key-vault,payment/default" \
    --uri "https://github.com/Azure-Samples/acme-fitness-store-config"
```

> ご注意："https://github.com/Azure-Samples/acme-fitness-store-config" は必要に応じて、ご自身の環境に合わせて変更してください。

### 1.1. Application Configuration Service でアプリケーションの設定

次に、上記で作成した　Application Configuration Service インスタンスを、外部設定として利用するよう Azure Spring Apps のアプリにバインドする作業に進みます：

```shell
az spring application-configuration-service bind --app ${PAYMENT_SERVICE_APP} &
az spring application-configuration-service bind --app ${CATALOG_SERVICE_APP} &
wait
```

## 2. Service Registry にアプリケーションをバインド

アプリケーションは互いに通信する必要があります。ASA-E は内部的に [Tanzu Service Registry](https://learn.microsoft.com/azure/spring-apps/how-to-enterprise-service-registry) を使用して動的にサービスのディスカバリーを行います。これを実現するために、サービス/アプリは以下のコマンドを実行して、Service Registry にバインドする必要があります:

```shell
az spring service-registry bind --app ${PAYMENT_SERVICE_APP}
az spring service-registry bind --app ${CATALOG_SERVICE_APP}
```

このセクションでは、バックエンド・アプリを Application Config Service と Service Registry にバインドすることができました。


⬅️ 前の作業: [09 - ハンズオン・ラボ 3.1 - バックエンド・アプリのデプロイ](../09-hol-3.1-deploy-backend-apps/README.md)

➡️ 次の作業: [11 - ハンズオン・ラボ 3.3 - Database と Cache の設定](../11-hol-3.3-configure-database-cache/README.md)
