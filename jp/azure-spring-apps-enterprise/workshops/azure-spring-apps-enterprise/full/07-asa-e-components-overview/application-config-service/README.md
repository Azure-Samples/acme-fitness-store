Spring Apps アプリケーションは通常、外部の git リポジトリ上で設定項目を管理し、下記のように Spring Apps Config サーバーと関連クライアントライブラリを使用して行います。

![old-way](images/old-way.png)

Application Configuration Service は、Azure Spring Apps Enterprise の機能の一部で、Spring Apps Configuration Server の機能を多言語で利用可能です。
Application Configuration Service は、git リポジトリから設定を参照し、それを Kubernetes の Config Map に変換し、実行中のコンテナのボリュームとしてマウントします。Spring Boot は設定内容を自動的に読み取る事ができます。

![new-way-with-acs](images/with-acs.png)

Application Configuration Service は2つの利点があります
 - アプリケーションのクラスパスから Spring Apps Config Server 用の jar を削除できます
 - 非 Spring ベースのアプリの場合、特定のプログラミング言語でカスタムライブラリを必要とせずに、設定に簡単にアクセスできます