package com.example.acme.assist.config;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class FitAssistConfiguration {

    public FitAssistConfiguration() {
        
    }

    @Value("classpath:/vector_store.json")
    private Resource vectorDbResource;

    @Bean
    @ConditionalOnProperty(value="vectorstore", havingValue = "simple", matchIfMissing = true)
    public SimpleVectorStore simpleVectorStore(EmbeddingClient embeddingClient) {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingClient);
        simpleVectorStore.load(vectorDbResource);
        return simpleVectorStore;
    }

}
