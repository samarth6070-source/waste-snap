package com.wastemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "app_users")
public class AppUser {

    @Id
    private String id; // email

    private String email;
    private String name;
    private String passwordHash;

    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}

