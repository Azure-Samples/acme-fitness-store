package com.example.acme.assist;

import com.example.acme.assist.model.SuggestedPrompts;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SuggestedPromptRepository {

    private static final Logger log = LoggerFactory.getLogger(SuggestedPromptRepository.class);

    @Autowired
    private ApplicationContext context;

    private Map<String, SuggestedPrompts> promptsMap = new HashMap<>();

    private SuggestedPrompts defaultPrompts;

    @PostConstruct
    private void loadSuggestedPrompts() {
        Resource resource = context.getResource("classpath:com/example/acme/assist/suggested-prompts.json");
        List<SuggestedPrompts> list = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            list = mapper.readValue(resource.getInputStream(),
                    mapper.getTypeFactory().constructCollectionType(List.class, SuggestedPrompts.class));
        } catch (Exception e) {
            log.warn("Cannot load suggested-prompts.json", e);
        }

        if (list != null && list.size() > 0) {
            for (SuggestedPrompts item : list) {
                promptsMap.put(item.getPage(), item);
                if (item.isDefault()) {
                    defaultPrompts = item;
                }
            }
        }
    }

    public SuggestedPrompts getSuggestedPrompts(String page) {
        return (page == null) ? null : promptsMap.get(page);
    }

    public SuggestedPrompts getDefaultSuggestedPrompts() {
        return defaultPrompts;
    }
}
