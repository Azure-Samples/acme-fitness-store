package com.example.acme.assist;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/admin")
public class FitAssistAdminController {

    private final VectorStore vectorStore;
    private final IndexService indexService;

    public FitAssistAdminController(VectorStore vectorStore, IndexService indexService) {
        this.vectorStore = vectorStore;
        this.indexService = indexService;
    }

    @PostMapping("/documents")
    public ResponseEntity<Void> index(@RequestBody List<Document> request) {
        this.vectorStore.add(request);
        this.indexService.markIndexed(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products-to-index")
    public ResponseEntity<List<String>> findProductsToIndex() {
        return ResponseEntity.ok(this.indexService.findProductsToIndex());
    }

}
