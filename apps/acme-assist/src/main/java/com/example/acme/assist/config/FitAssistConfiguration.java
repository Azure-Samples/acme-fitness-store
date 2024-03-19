package com.example.acme.assist.config;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.example.acme.assist.AzureOpenAIClient;
import com.example.acme.assist.vectorstore.CosmosDBVectorStore;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

@Configuration
public class FitAssistConfiguration {

    @Value("${spring.ai.azure.openai.embedding-model}")
    private String embeddingDeploymentId;

    @Value("${spring.ai.azure.openai.deployment-name}")
    private String chatDeploymentId;

    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;

    @Value("${spring.ai.azure.openai.api-key}")
    private String apiKey;

    @Value("${vector-store.file}")
    private String cosmosVectorJsonFile;

    @Value("${spring.data.mongodb.enabled}")
    private String cosmosEnabled;

    //@Autowired
    private MongoTemplate mongoTemplate;
    public FitAssistConfiguration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }



    @Value("classpath:/vector_store.json")
    private Resource vectorDbResource;

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingClient embeddingClient) {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingClient);
        if (cosmosEnabled.equals("false")) {
            simpleVectorStore.load(vectorDbResource);
        }
        return simpleVectorStore;
    }

    @Bean
    public CosmosDBVectorStore vectorStore() throws IOException {
        CosmosDBVectorStore store = null;
        if (cosmosEnabled.equals("true")) {
            store = new CosmosDBVectorStore(mongoTemplate);
            String currentPath = new java.io.File(".").getCanonicalPath();
            String path = currentPath + cosmosVectorJsonFile.replace("\\", "//");
            store.loadFromJsonFile(path);
        }
        else {
            store = new CosmosDBVectorStore(null);
        }
        return store;
    }

    @Bean
    public AzureOpenAIClient AzureOpenAIClient() {
        var innerClient = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .buildClient();
        return new AzureOpenAIClient(innerClient, embeddingDeploymentId, chatDeploymentId);
    }


}
