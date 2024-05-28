package com.example.acme.assist;

import com.example.acme.assist.utils.DocumentUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The IndexService class is used to manage the indexing of documents. It will call the ProductRepository to get the
 * list of products and compare them to the indexed documents. If a product is not indexed, it will be added to the
 * list of products to index. If a product is already indexed, it will be compared to the existing document. If the
 * content or metadata of the document has changed, the product will be added to the list of products to index.
 */
@Service
public class IndexService {

    public static final Logger LOGGER = LoggerFactory.getLogger(IndexService.class);
    private Map<String, Document> indexedDocuments;
    private final ProductRepository productRepository;

    public IndexService(@Value("classpath:/vector_store.json") Resource indexedDocuments,
                        ProductRepository productRepository) {
        load(indexedDocuments);
        this.productRepository = productRepository;
    }

    public List<String> findProductsToIndex() {
        List<String> productIds = new ArrayList<>();
        productRepository.refreshProductList();
        productRepository.getProductList().stream()
                .map(DocumentUtils::createDocument)
                .forEach(doc -> {
                    if (!indexedDocuments.containsKey(doc.getId())) {
                        productIds.add(doc.getId());
                    } else {
                        Document existingDoc = indexedDocuments.get(doc.getId());
                        if (!doc.getContent().startsWith(existingDoc.getContent())
                                || !existingDoc.getMetadata().equals(doc.getMetadata())) {
                            LOGGER.info("Document with id {} has changed, adding to list of products to index", doc.getId());
                            LOGGER.debug("Existing document: {}", existingDoc);
                            LOGGER.debug("Current document: {}", doc);

                            productIds.add(doc.getId());
                        }
                    }
                });
        return productIds;
    }

    public void markIndexed(List<Document> documents) {
        documents.forEach(doc -> indexedDocuments.put(doc.getId(), doc));
    }

    private void load(Resource resource) {
        TypeReference<HashMap<String, Document>> typeRef = new TypeReference<>() {
        };
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.indexedDocuments = objectMapper.readValue(resource.getInputStream(), typeRef);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
