---
page_type: sample
languages:
- java
products:
- Azure Spring Apps
- Azure Database for PostgreSQL
- Azure Cache for Redis
- Azure Active Directory
description: "Deploy Microservice Apps to Azure"
urlFragment: "acme-fitness-store"
---
# ACME Fitness Store

ACME Fitness Store は、架空のスポーツ用品を販売するオンライン・ショップです。このリポジトリで、ACME Fitness Store アプリケーションのソース・コードとデプロイ用スクリプトを提供しています。

## ハイ・レベルのアーキテクチャ
![An image showing the services involved in the ACME Fitness Store. It depicts the applications and their dependencies](../media/acme-fitness-store-architecture.jpg)

このアプリケーションは、下記の複数のサービスから構成されています。

* Java Spring Boot アプリケーション:
  * カタログサービス：利用可能な製品を取得
  * 支払いサービス：ユーザーの注文に対する支払い処理と承認
  * ID サービス：認証されたユーザーを参照
  * 支援サービス：フィットネス・ストアに AI を適用

* Python アプリケーション:
  * カートサービス：購入用に選択されたユーザーのアイテムを管理

* ASP .NET Core アプリケーション:
  * 注文サービス：ユーザーのカートに入っている商品の購入処理

* Node.js & 静的 HTML
  * フロントエンド：フロントエンド・ショッピング・アプリケーション

このサンプルは、Azure Spring Apps Enterprise または Tanzu Application Platform にデプロイできます。

## レポジトリの構成

| ディレクトリ                                                        | 目的 |
| ---------------------------------------------------------------- | ------------- |
| [apps/](../../apps)                                                   | 各サービスのソースコード  |
| [azure-spring-apps-enterprise/](./azure-spring-apps-enterprise)   | Azure Spring Apps Enterprise にデプロイするためのドキュメントとスクリプト |
| [tanzu-application-platform/](../../tanzu-application-platform)       | Tanzuアプリケーションプラットフォームにデプロイするためのドキュメントとスクリプト |

## Azure Spring Apps Enterprise (ASA-E) へのアプリのデプロイ

Azure Spring Apps Enterprise を使用すると、Azure 上で Spring Boot や 多言語のアプリケーションを動作させることができます。
azure-spring-apps-enterprise/README.md のクイックスタート ガイドでは、ACME Fitness ストア・アプリケーションを Azure Spring Apps Enterprise にデプロイする方法について記載しています。

* [Azure Spring Apps へのアプリケーションのデプロイ](./azure-spring-apps-enterprise/README.md#spring-boot-apps-を-azure-spring-apps-enterprise-にデプロイする)
  * [本ワークショップで得られる内容](./azure-spring-apps-enterprise/README.md#本ワークショップで得られる内容)
  * [事前準備](./azure-spring-apps-enterprise/README.md#事前準備)
  * [Azure CLI extension のインストール](./azure-spring-apps-enterprise/README.md#azure-cli-extension-のインストール)
  * [レポジトリを Clone](./azure-spring-apps-enterprise/README.md#レポジトリを-clone)
  * [作業 0 - インストール環境の準備](./azure-spring-apps-enterprise/README.md#作業-0---インストール環境の準備)  
  * [作業 1 - アプリケーションのビルドとデプロイ](./azure-spring-apps-enterprise/README.md#作業-1---アプリケーションのビルドとデプロイ)
  * [作業 2 - シングル サインオンの構成](./azure-spring-apps-enterprise/README.md#作業-2---シングル-サインオンの構成)
  * [作業 3 - Azure Database for PostgreSQL および Azure Cache for Redis の統合](./azure-spring-apps-enterprise/README.md#作業-3---azure-database-for-postgresql-および-azure-cache-for-redis-の統合)
  * [作業 4 - アプリケーション内の機密情報の安全な読み込み](./azure-spring-apps-enterprise/README.md#作業-4---アプリケーション内の機密情報の安全な読み込み)
  * [作業 5 - エンドツーエンドのアプリケーション監視](./azure-spring-apps-enterprise/README.md#作業-5---エンドツーエンドのアプリケーション監視)
  * [作業 6 - リクエスト数に対するレート制限の設定](./azure-spring-apps-enterprise/README.md#作業-6---リクエスト数に対するレート制限の設定)
  * [作業 7 - アイデアから生産までの自動化](./azure-spring-apps-enterprise/README.md#作業-7---アイデアから生産までの自動化)
  * [作業 8 - フィットネス・ストアに AI を適用](./azure-spring-apps-enterprise/README.md#作業-8---フィットネスストアに-ai-を適用)

## Tanzuアプリケーションプラットフォーム(TAP)へのアプリのデプロイ

Tanzuアプリケーションプラットフォーム(TAP) を使用すると、多言語アプリを CNCF に準拠する Kubernetes クラスター上に、簡単に構築して実行できます。
[tanzu-application-platform/README.md](../../tanzu-application-platform/README.md) の記載内容に従い、アプリケーションを TAP にデプロイします。
