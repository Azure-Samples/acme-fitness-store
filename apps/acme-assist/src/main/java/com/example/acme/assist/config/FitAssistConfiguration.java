package com.example.acme.assist.config;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.impl.SimplePersistentVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class FitAssistConfiguration {

    @Value("classpath:/vector_store.json")
    private Resource vectorDbResource;
    @Bean
    public SimplePersistentVectorStore simpleVectorStore(EmbeddingClient embeddingClient) {
        SimplePersistentVectorStore simpleVectorStore = new SimplePersistentVectorStore(embeddingClient);
        simpleVectorStore.load(vectorDbResource);
        return simpleVectorStore;
    }
}
