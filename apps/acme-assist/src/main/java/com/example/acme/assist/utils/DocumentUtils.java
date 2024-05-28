package com.example.acme.assist.utils;

import com.example.acme.assist.model.Product;
import org.springframework.ai.document.Document;

import java.util.Map;

public class DocumentUtils {

    private DocumentUtils() {
    }

    public static Document createDocument(Product product) {
        StringBuilder sb = new StringBuilder();
        sb.append("price: ").append(product.getPrice()).append(System.lineSeparator());
        sb.append("name: ").append(product.getName()).append(System.lineSeparator());
        sb.append("shortDescription: ").append(product.getShortDescription()).append(System.lineSeparator());
        sb.append("description: ").append(product.getDescription()).append(System.lineSeparator());
        sb.append("tags: ").append(product.getTags()).append(System.lineSeparator());
        var content = sb.toString();

        return new Document(product.getId(), content, Map.of("name", product.getName()));
    }

}
