package com.wastemanagement;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class WasteManagementApplication {

    private static final Logger log = LoggerFactory.getLogger(WasteManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WasteManagementApplication.class, args);
    }

    /**
     * Verifies Atlas connectivity on startup (ping against the configured database).
     */
    @Bean
    CommandLineRunner mongoAtlasConnectionTest(MongoTemplate mongoTemplate) {
        return args -> {
            Document pong = mongoTemplate.getDb().runCommand(new Document("ping", 1));
            String dbName = mongoTemplate.getDb().getName();
            log.info("MongoDB Atlas connection test: OK — database='{}' ping ok={}", dbName, pong.get("ok"));
        };
    }
}
