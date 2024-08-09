In the previous section you created an empty azure spring apps instance. In this section we will try to deploy a very simple hello-world spring boot app to get a high level understanding of how to deploy an asa-e app and access it. 

---

## Create a Hello World Spring Boot app

A typical way to create Spring Boot applications is to use the Spring Initializer at  [https://start.spring.io/](https://start.spring.io/). 
**For the purposes of this workshop, we will only invoke the Spring Initializer site via the `curl` command**.

>💡 __Note:__ All subsequent commands in this workshop should be run from the same directory, except where otherwise indicated via `cd` commands.

In the same directory as this README execute the curl command line below:

```shell
curl https://start.spring.io/starter.tgz -d dependencies=web -d baseDir=hello-world \ -d bootVersion=2.7.5 -d javaVersion=17 -d type=maven-project | tar -xzvf -
```

> We force the Spring Boot version to be 2.7.5, and keep default settings that use the `com.example.demo` package.

## Add a new Spring MVC Controller

In the `hello-world/src/main/java/com/example/demo` directory, create a
new file  called `HelloController.java` next to `DemoApplication.java` file with
the following content:

```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Azure Spring Apps Enterprise\n";
    }
}
```

## Test the project locally

Run the project:

```bash
cd hello-world
./mvnw spring-boot:run &
cd ..
```

Requesting the `/hello` endpoint should return the "Hello from Azure Spring Apps" message.

```bash
curl http://127.0.0.1:8080/hello
```

Finally, kill running app:

```bash
kill %1
```
The above step ensures that the hello-world app is up and running locally without any issues.

## Create and deploy the application on Azure Spring Apps instance


Use the command below to create the app instance from cli:

```bash
az spring app create -n hello-world
```

You can now build your "hello-world" project and deploy it to Azure Spring Apps Enterprise:

```bash
cd hello-world
./mvnw clean package
az spring app deploy -n hello-world --artifact-path target/demo-0.0.1-SNAPSHOT.jar
cd ..
```

This creates a jar file on your local disk and uploads it to the app instance you created in the preceding step.  The `az` command will output a result in JSON.  You don't need to pay attention to this output right now, but in the future, you will find it useful for diagnostic and testing purposes.

## Test the project in the cloud

Go to [the Azure portal](https://portal.azure.com/):

- Look for your Azure Spring Apps instance in your resource group
- Click "Apps" in the "Settings" section of the navigation pane and select "hello-world"
- Find the "Test endpoint" in the "Essentials" section.
![Test endpoint](images/test-endpoint.png)
- This will give you something like:
  `https://primary:<REDACTED>@hello-world.test.azuremicroservices.io/hello-world/default/`
  >💡 Note the text between `https://` and `@`.  These are the basic authentication credentials, without which you will not be authorized to access the service.
- Append `hello/` to the URL.  Failure to do this will result in a "404 not found".

You can now use cURL again to test the `/hello` endpoint, this time served by Azure Spring Apps.  For example.

```bash
curl https://primary:...hello-world/default/hello/
```

If successful, you should see the message: `Hello from Azure Spring Apps Enterprise`.

## View Logs

```shell
az spring app logs -s ${SPRING_APPS_SERVICE} -g ${RESOURCE_GROUP} -n hello-world -f
```

## Scale App

```shell
az spring app scale -n hello-world --instance-count 3
```
Once this command is successfully complete, you will find in Azure portal the Running Instance count updated from default 1 to 3.

![Updated instance count](./images/instance-count.png)

## Delete the hello-world app
Once you successfully test the hello-world app, please go ahead and delete the app to save on resources. To delete this app, use the below command.

```bash
az spring app delete --name hello-world
```
## Conclusion

Congratulations, you have deployed your first Spring Boot app to Azure Spring Apps!


---

⬅️ Previous guide: [04 - Log Analytics Setup](../04-log-analytics-setup/README.md)

➡️ Next guide: [06 - Acme Fitness Micorservices App Introduction](../06-polyglot-microservices-app-acme-fitness/README.md)
