package com.example.acme.assist.mongodb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;

@Configuration
@ConditionalOnProperty(value = "vectorstore", havingValue = "mongodb", matchIfMissing = false)
public class MongoDBConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String url;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    public MongoTemplate mongoTemplate() {
        ConnectionString cs = new ConnectionString(url);
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(cs).build();

        return new MongoTemplate(MongoClients.create(settings), database);
    }
}
