package com.wastemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration. Connection settings come from {@code spring.data.mongodb.uri}
 * and {@code spring.data.mongodb.database} in {@code application.properties} (Atlas + database {@code app}).
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.wastemanagement.repository")
public class MongoConfig {
}
