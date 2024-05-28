package com.example.acme.assist.vectorstore;


import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonMetadataGenerator;
import org.springframework.ai.reader.JsonReader;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IdAwareJsonReader extends JsonReader {

    private static final String DEFAULT_ID_KEY = "id";

    public IdAwareJsonReader(String idKey, Resource resource, JsonMetadataGenerator jsonMetadataGenerator, String... jsonKeysToUse) {
        super(resource, new IdAwareMetadataGenerator(idKey, jsonMetadataGenerator), jsonKeysToUse);
    }

    @Override
    public List<Document> get() {
        List<Document> documents = super.get();

        List<Document> result = new ArrayList<>();
        for (Document document : documents) {
            Object id = document.getMetadata().remove(DEFAULT_ID_KEY);
            result.add(new Document(id.toString(), document.getContent(), document.getMetadata()));
        }
        return result;
    }

    public static class IdAwareMetadataGenerator implements JsonMetadataGenerator {


        private final JsonMetadataGenerator jsonMetadataGenerator;
        private final String idKey;

        public IdAwareMetadataGenerator(String idKey, JsonMetadataGenerator jsonMetadataGenerator) {
            this.idKey = idKey;
            this.jsonMetadataGenerator = jsonMetadataGenerator;
        }

        @Override
        public Map<String, Object> generate(Map<String, Object> jsonMap) {
            Map<String, Object> result = this.jsonMetadataGenerator.generate(jsonMap);
            result.put(DEFAULT_ID_KEY, jsonMap.get(this.idKey));
            return result;
        }
    }


}
