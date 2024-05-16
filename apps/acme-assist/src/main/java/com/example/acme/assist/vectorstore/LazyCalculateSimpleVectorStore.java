package com.example.acme.assist.vectorstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.util.List;

public class LazyCalculateSimpleVectorStore extends SimpleVectorStore {

    public static final Logger LOGGER = LoggerFactory.getLogger(LazyCalculateSimpleVectorStore.class);

    public LazyCalculateSimpleVectorStore(EmbeddingClient embeddingClient) {
        super(embeddingClient);
    }

    @Override
    public void add(List<Document> documents) {
        for (Document document : documents) {
            if (document.getEmbedding() != null) {
                LOGGER.info("Document id = {} already has an embedding, skipping.", document.getId());
            } else {
                LOGGER.info("Calling EmbeddingClient for document id = {}", document.getId());
                List<Double> embedding = this.embeddingClient.embed(document);
                document.setEmbedding(embedding);
            }
            this.store.put(document.getId(), document);
        }
    }
}
