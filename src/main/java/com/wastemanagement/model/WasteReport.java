package com.wastemanagement.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "waste_reports")
public class WasteReport {

    @Id
    private String id;
    private String description;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String userEmail;
    private String userName;

    @Builder.Default
    private String status = "PENDING";

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
