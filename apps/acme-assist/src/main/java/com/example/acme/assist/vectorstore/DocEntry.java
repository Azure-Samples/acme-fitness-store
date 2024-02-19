package com.example.acme.assist.vectorstore;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Jacksonized
@Document(collection = "vectorstore")
public class DocEntry {

    private List<Double> embedding;
    @Id
    private String id;

    public MetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }

    private MetaData metadata;
    private String text;


    public DocEntry() {}

    public DocEntry(List<Double> embedding, String id, MetaData metadata, String text) {
        this.id = id;
        this.text = text;
        this.embedding = embedding;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }

    @Override
    public String toString() {
        return "DocEntry{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", embedding='" + embedding + '\'' +
                '}';
    }

}

