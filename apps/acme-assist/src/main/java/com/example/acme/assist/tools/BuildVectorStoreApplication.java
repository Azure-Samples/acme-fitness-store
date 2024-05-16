package com.example.acme.assist.tools;

import com.example.acme.assist.FitAssistApplication;
import com.example.acme.assist.vectorstore.IdAwareJsonReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonMetadataGenerator;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A CLI application for building and persisting a vector store from files.
 */
@SpringBootApplication
@ComponentScan(
        basePackages = {"com.example.acme.assist"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { FitAssistApplication.class, UpdateVectorStoreJob.class })
)
public class BuildVectorStoreApplication implements CommandLineRunner {

    @Autowired
    private SimpleVectorStore simpleVectorStore;

    public static void main(String[] args) {
        new SpringApplicationBuilder(BuildVectorStoreApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(String... originalArgs) throws Exception {
        DefaultApplicationArguments args = new DefaultApplicationArguments(originalArgs);
        var from = args.getOptionValues("from");
        if (from == null || from.size() != 1) {
            System.err.println("argument --from is required.");
            System.exit(-1);
        }
        var to = args.getOptionValues("to");
        if (to == null || to.size() != 1) {
            System.err.println("argument --to is required.");
            System.exit(-1);
        }
        var jsonFiles = List.of(from.get(0).split(","));

        for (var file : jsonFiles) {
            File sourceFile = new File(file);
            JsonReader jsonLoader = new IdAwareJsonReader("id", new FileSystemResource(sourceFile),
                    new ProductMetadataGenerator(),
                    "price", "name", "shortDescription", "description", "tags");
            List<Document> documents = jsonLoader.get();
            this.simpleVectorStore.add(documents);
        }
        this.simpleVectorStore.save(new File(to.get(0)));
    }

    public static class ProductMetadataGenerator implements JsonMetadataGenerator {
        @Override
        public Map<String, Object> generate(Map<String, Object> jsonMap) {
            return Map.of("name", jsonMap.get("name"));
        }
    }
}
