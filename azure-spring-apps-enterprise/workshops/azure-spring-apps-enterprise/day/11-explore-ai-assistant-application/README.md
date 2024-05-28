# 11 - Explore AI Assistant Application

Let's take a closer look at the AI Assistant Application. 

## Explore the Structure

* Observe the `acme-assist` directory and the structure of the application.

* Look at the `application.yaml` and see how the application is configured, e.g.

```yaml
spring:
  ...
  ai:
    azure:
      openai:
        chat:
          options:
            deployment-name: gpt-35-turbo-16k
        embedding:
          options:
            deployment-name: text-embedding-ada-002
```

  * Could you define other Chat and Embedding models?
  * Where would you deploy these models?
  * Explore Azure OpenAI Portal to look what other models are available in Azure OpenAI.
  * How would you define this?

* Look at the specific Java classes, e.g.
  * `com.example.acme.assist.FitAssistApplication`
  * `com.example.acme.assist.FitAssistController`
  * `com.example.acme.assist.ChatService`
  * `com.example.acme.assist.config.FitAssistConfiguration`

  * What are the endpoints that the frontend application is calling?
  * What is `VectorStore` used for?
  * Could you use a different type of `VectorStore`?
  * What is `similarityThreshold(0.4)` in `ChatService`?

* Look at the defined prompts, e.g.
  * `chatWithProductId.st`
  * `chatWithoutProductId.st`

## Configure Model Options

* Review `VectorStore` and [vector databases Spring AI documentation](https://docs.spring.io/spring-ai/reference/api/vectordbs.html).
* Feel free to experiment with different temperatures (0.0 .. 1.0), e.g. `similarityThreshold()`.
* Feel free to set a default temperature, e.g. `spring.ai.azure.openai.chat.options.temperature`.
* Feel free to experiment with different `topK` levels.

## Configure System Prompt

* Feel free to experiment with various systems prompts, e.g. `chatWithoutProductId` and `chatWithProductId`.
* Try experimenting with various languages, styles, or return values.

## Next Guide

Next guide - [12 - Summary](../12-summary/README.md)