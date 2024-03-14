package com.example.acme.assist.config;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class FitAssistConfiguration {

    @Value("classpath:/vector_store.json")
    private Resource vectorDbResource;

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingClient embeddingClient) {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingClient);
        simpleVectorStore.load(vectorDbResource);
        return simpleVectorStore;
    }
}
