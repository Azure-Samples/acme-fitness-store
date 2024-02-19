package com.example.acme.assist;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class AzureOpenAIClient {
    private static final double TEMPERATURE = 0.7;

    private final OpenAIClient client;

    private final String embeddingDeploymentId;

    private final String chatDeploymentId;

    public Embeddings getEmbeddings(List<String> texts) {
        var response = client.getEmbeddings(embeddingDeploymentId,
                new EmbeddingsOptions(texts).setModel(embeddingDeploymentId));
        log.info("Finished an embedding call with {} tokens.", response.getUsage().getTotalTokens());
        return response;
    }

    public ChatCompletions getChatCompletions(List<ChatMessage> messages) {
        var chatCompletionsOptions = new ChatCompletionsOptions(messages)
                .setModel(chatDeploymentId)
                .setTemperature(TEMPERATURE);
        var response = client.getChatCompletions(chatDeploymentId, chatCompletionsOptions);
        log.info("Finished a chat completion call with {} tokens", response.getUsage().getTotalTokens());
        return response;
    }
}
