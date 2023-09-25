前の作業では、単純に Azure Spring Apps のインスタンスを作成しました。本ワークショップでは、簡単な Spring Boot の Hello World アプリケーションを Azure Spring Apps Enterprise にデプロイし、アプリのデプロイ方法やアクセス方法について理解します。

## Spring Boot で Hello World アプリの作成


Spring Bootアプリケーションを作成する際、通常 Spring Initializer を使用します。
[https://start.spring.io/](https://start.spring.io/)

**本ワークショップでは、`curl`コマンドを利用して Spring Initializer サイトを呼び出します**


>💡 注: 以降の手順は、すべてのこの README と同じディレクトリ上からコマンドを実行してください（`cd`コマンドで指示される場合を除く）

```shell
curl https://start.spring.io/starter.tgz -d dependencies=web -d baseDir=hello-world \
      -d bootVersion=2.7.5 -d javaVersion=17 -d type=maven-project | tar -xzvf -
```

> Spring Boot のバージョンを 2.7.5 に設定し、Java 17 でプロジェクトを作成します。

## Spring MVC Controller を新規追加

`hello-world/src/main/java/com/example/demo` ディレクトリ配下の `DemoApplication.java`ファイルと同じディレクトリ上に `HelloController.java` という新規ファイルを作成し、以下の内容を入力します：

```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "初めての Azure Spring Apps Enterprise\n";
    }
}
```

## プロジェクトをローカル環境でテスト

プロジェクトの起動には、下記のように Maven Wrapper (`./mvnw`) を使用します。Maven Wrapper は、Maven をインストールせずに Maven プロジェクトをビルドするためのツールです。

```bash
cd hello-world
./mvnw spring-boot:run &
cd ..
```

HTTP クライアントで `/hello` のエンドポイントをリクエストすると、\"初めての Azure Spring Apps Enterprise\"　のメッセージが表示されます。

```bash
curl http://127.0.0.1:8080/hello
```

最後に、実行中のアプリを終了します。

```bash
kill %1
```

上記の作業では、hello-world のアプリがローカルの環境で問題なく動作することを確認しました。

## Azure Spring Apps インスタンス上にアプリを作成し、デプロイ

以下のコマンドを実行し、Azure CLI からアプリを実行するためのインスタンスを作成します。

```bash
az spring app create -n hello-world --service ${SPRING_APPS_SERVICE} --resource-group ${RESOURCE_GROUP} 
```

Azure Spring Apps Enterprise 上でインスタンスを作成した後、"hello-world" プロジェクトをビルドし、デプロイすることができます

```bash
az spring app deploy --service ${SPRING_APPS_SERVICE} --resource-group ${RESOURCE_GROUP}  --name hello-world --source-path hello-world --build-env "BP_JVM_VERSION=17.*"
```

上記のコマンドで、実装したソースコードをビルドした後、アプリ用のインスタンスにアップロードします。
`az`コマンドはJSONの結果を出力します。現時点では出力内容に注意を払う必要はありませんが、将来的には、診断やテスト用で利用できます。

## クラウドの環境上でテスト

[the Azure portal](https://portal.azure.com/) にアクセスし、Azure Spring Apps Enterprise のインスタンスを確認します。

- 作成したリソース・グループ内の Azure Spring Apps のインスタンスを確認
- "Settings" の "Apps" をクリックし "hello-world" を選択
- "Essentials" の "Test endpoint" をクリック
![Test endpoint](images/test-endpoint.png)
- 次のようなもの結果が得られます
  `https://primary:<REDACTED>@hello-world.test.azuremicroservices.io/hello-world/default/`
  >💡 `https://`と`@`の間の文字列に注目してください。記載されているように資格情報が含まれ、これがなければ、テスト用のエンドポイントにアクセスする事はできません。  
- URLの最後に `hello/`を追加してアクセスしてください。これを忘れると "404 not found" が表示されます。

cURLを再度実行して`/hello`エンドポイントにアクセスしテストをしてください。今度は Azure Spring Apps 上で起動している Hello World のアプリケーションから結果が返ってきます。例えば、下記のようになります。

```bash
curl https://primary:...hello-world/default/hello/
```

成功すると、「初めての Azure Spring Apps Enterprise」のメッセージが表示されます：


## Logs の閲覧

下記のコマンドを実行し、Azure Spring Apps Enterprise 上で稼働するアプリケーションのログを確認します。

```shell
az spring app logs -s ${SPRING_APPS_SERVICE} -g ${RESOURCE_GROUP} -n hello-world -f
```

## アプリケーションのスケール

下記のコマンドを実行し、Azure Spring Apps Enterprise 上で稼働するアプリケーションのインスタンス数を増やします。

```shell
az spring app scale -s ${SPRING_APPS_SERVICE} -g ${RESOURCE_GROUP} -n hello-world --instance-count 3
```

コマンドが成功すると、Azureポータルでの Running Instance の数がデフォルトの1から3に更新されます

![Updated instance count](./images/instance-count.png)

## hello-world アプリケーションの削除

hello-world アプリの動作確認を行なった後、リソースを節約するためにアプリを削除してください。このアプリを削除するためには、下記のコマンドを実行します。

```bash
az spring app delete -s ${SPRING_APPS_SERVICE} -g ${RESOURCE_GROUP} --name hello-world
```
## まとめ

おめでとうございます！これで、Azure Spring Apps に Spring Boot アプリケーションをデプロイすることができました！


---

⬅️ 前の作業: [04 - Log Analytics のセットアップ](../04-log-analytics-setup/README.md)

➡️ 次の作業: [06 - Acme フィットネスアプリの概要-マイクロサービス](../06-polyglot-microservices-app-acme-fitness/README.md)
