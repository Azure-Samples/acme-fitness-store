package com.example.acme.assist.tools;

import com.example.acme.assist.FitAssistApplication;
import com.example.acme.assist.ProductRepository;
import com.example.acme.assist.utils.DocumentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * A CLI application for building and updating a vector store in the FitAssistApplication.
 */
@SpringBootApplication
@ComponentScan(
        basePackages = {"com.example.acme.assist"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { FitAssistApplication.class, BuildVectorStoreApplication.class })
)
public class UpdateVectorStoreJob implements CommandLineRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(UpdateVectorStoreJob.class);

    @Value("${assistService:http://assist-service}/ai/admin")
    private String fitAssistAdminUrl;
    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;
    private final EmbeddingClient embeddingClient;

    public UpdateVectorStoreJob(ProductRepository productRepository, EmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
        this.restTemplate = new RestTemplate();
        this.productRepository = productRepository;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(UpdateVectorStoreJob.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(String... originalArgs) throws Exception {
        List<Document> documents = new ArrayList<>();
        List<String> productIds = findProductsToIndex();
        LOGGER.info("Found {} products to index, the ids are {}", productIds.size(), productIds);
        for (var productId : productIds) {
            var document = DocumentUtils.createDocument(this.productRepository.getProductById(productId));
            LOGGER.info("Calling EmbeddingClient for document id = {}", document.getId());
            List<Double> embedding = this.embeddingClient.embed(document);
            document.setEmbedding(embedding);
            documents.add(document);
        }
        indexDocuments(documents);
    }

    private List<String> findProductsToIndex() {
        ResponseEntity<List<String>> responseEntity = this.restTemplate.exchange(
                fitAssistAdminUrl + "/products-to-index",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        return responseEntity.getBody();
    }

    private void indexDocuments(List<Document> documents) {
        this.restTemplate.postForEntity(fitAssistAdminUrl + "/documents", documents, Void.class);
    }

}

