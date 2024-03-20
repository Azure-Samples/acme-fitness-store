package com.example.acme.assist.mongodb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value = "vectorstore", havingValue = "mongodb", matchIfMissing = false)
public class CosmosDBVectorStore implements VectorStore {

    private static Logger LOGGER = LoggerFactory.getLogger(CosmosDBVectorStore.class);

    private static String COLLECTION = "vectorstore";

    @Value("classpath:/vector_store.json")
    private Resource vectorDbResource;

    @Autowired
    private MongoTemplate template;

    @Autowired
    protected EmbeddingClient embeddingClient;

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
        String bsonCmd = "{\"createIndexes\":\"" + COLLECTION + "\",\"indexes\":"
                + "[{\"name\":\"vectorsearch\",\"key\":{\"embedding\":\"cosmosSearch\"},\"cosmosSearchOptions\":"
                + "{\"kind\":\"vector-ivf\",\"numLists\":" + numLists + ",\"similarity\":\"" + similarity
                + "\",\"dimensions\":" + dimensions + "}}]}";
        LOGGER.info("creating vector index in Cosmos DB Mongo vCore...");
        try {
            template.executeCommand(bsonCmd);
        } catch (Exception e) {
            LOGGER.warn("Failed to create vector index in Cosmos DB Mongo vCore", e);
        }
    }

    @Override
    public void add(List<Document> documents) {
        // TODO Auto-generated method stub
    }

    @Override
    public Optional<Boolean> delete(List<String> idList) {
        return Optional.empty();
    }

    private List<Double> getUserQueryEmbedding(String query) {
        return this.embeddingClient.embed(query);
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        List<Double> embedding = getUserQueryEmbedding(request.getQuery());
        
        // perform vector search in Cosmos DB Mongo API - vCore
        String command = "{\"$search\":{\"cosmosSearch\":{\"vector\":" + embedding + ",\"path\":\"embedding\",\"k\":"
                + request.getTopK() + "}}}";
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
