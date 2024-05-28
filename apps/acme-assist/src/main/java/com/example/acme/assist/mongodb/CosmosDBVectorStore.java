package com.example.acme.assist.mongodb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CosmosDBVectorStore implements VectorStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(CosmosDBVectorStore.class);

    private static final String COLLECTION = "vectorstore";

    private final Resource vectorDbResource;

    private final MongoTemplate template;

    protected EmbeddingClient embeddingClient;

    public CosmosDBVectorStore(Resource vectorDbResource,
                               MongoTemplate mongoTemplate,
                               EmbeddingClient embeddingClient) {
        this.vectorDbResource = vectorDbResource;
        this.template = mongoTemplate;
        this.embeddingClient = embeddingClient;
    }

    @PostConstruct
    protected void init() {
        template.dropCollection(COLLECTION);
        this.load(vectorDbResource);
        LOGGER.info("initialized collection in mongodb");
    }

    public void load(Resource resource) {
        TypeReference<HashMap<String, Document>> typeRef = new TypeReference<>() {
        };
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Document> docs = objectMapper.readValue(resource.getInputStream(), typeRef);
            Optional<Document> doc = docs.values().stream().findFirst();
            if (doc.isPresent()) {
                int dimensions = doc.get().getEmbedding().size();
                template.insert(docs.values(), COLLECTION);
                createVectorIndex(5, dimensions, "COS");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void createVectorIndex(int numLists, int dimensions, String similarity) {
        String bsonCmd = """
                {
                    "createIndexes": "%s",
                    "indexes": [
                        {
                            "name": "vectorsearch",
                            "key": {
                                "embedding": "cosmosSearch"
                            },
                            "cosmosSearchOptions": {
                                "kind": "vector-ivf",
                                "numLists": %d,
                                "similarity": "%s",
                                "dimensions": %d
                            }
                        }
                    ]
                }
                """.formatted(COLLECTION, numLists, similarity, dimensions);
        LOGGER.info("creating vector index in Cosmos DB Mongo vCore...");
        try {
            template.executeCommand(bsonCmd);
        } catch (Exception e) {
            LOGGER.warn("Failed to create vector index in Cosmos DB Mongo vCore", e);
        }
    }

    @Override
    public void add(List<Document> documents) {
        if (!CollectionUtils.isEmpty(documents)) {
            for (Document doc : documents) {
                if (CollectionUtils.isEmpty(doc.getEmbedding())) {
                    LOGGER.info("Calling EmbeddingClient for document id = {}", doc.getId());
                    List<Double> embedding = this.embeddingClient.embed(doc);
                    doc.setEmbedding(embedding);
                }

                Query query = new Query();
                query.addCriteria(Criteria.where("id").is(doc.getId()));

                org.bson.Document bsonDoc = new org.bson.Document(); // org.bson.Document
                template.getConverter().write(doc, bsonDoc);
                template.upsert(query, Update.fromDocument(bsonDoc), COLLECTION);
            }
        }
    }

    @Override
    public Optional<Boolean> delete(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Optional.of(Boolean.FALSE);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(idList));
        template.remove(query, COLLECTION);
        return Optional.of(Boolean.TRUE);
    }

    private List<Double> getUserQueryEmbedding(String query) {
        return this.embeddingClient.embed(query);
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        List<Double> embedding = getUserQueryEmbedding(request.getQuery());

        // perform vector search in Cosmos DB Mongo API - vCore
        String command = """
                {
                    "$search": {
                        "cosmosSearch": {
                            "vector": %s,
                            "path": "embedding",
                            "k": %d
                        }
                    }
                }
                """.formatted(embedding, request.getTopK());
        Aggregation agg = Aggregation.newAggregation(Aggregation.stage(command));
        AggregationResults<org.bson.Document> results = template.aggregate(agg, COLLECTION, org.bson.Document.class);
        List<Document> ret = new ArrayList<>();
        results.getMappedResults().forEach(bDoc -> {
            String content = bDoc.getString("content");
            Document doc = new Document(content);
            ret.add(doc);
        });
        return ret;
    }
}
