package com.microsoft.azure.spring.chatgpt.sample.common.vectorstore;

import java.util.List;

public interface VectorStore {
    void saveDocument(String key, DocEntry doc);

    DocEntry getDocument(String key);

    void removeDocument(String key);

    List<DocEntry> searchTopKNearest(List<Double> embedding, int k);

    List<DocEntry> searchTopKNearest(List<Double> embedding, int k, double cutOff);
}
