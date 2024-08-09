## Goal
The goal of this section is to build and test a basic hello-world spring-boot application locally, so that we can use this in the next sections to deploy the azure spring apps instance.

### Build hello-world app source code
Perform the below steps to build the app locally

```bash
cd hello-world
./mvnw spring-boot:run &
cd ..
```

### Test the hello-world app locally

```bash
curl http://127.0.0.1:8080/hello
```

Finally, kill running app:

```bash
kill %1
```

## Conclusion
You succesfully built and tested a hello-world app locally.

⬅️ Previous guide: [04 - Create ASA-E App](../04-create-app/README.md)

➡️ Next guide: [06 - Test ASA-E App](../06-test-app/README.md)
