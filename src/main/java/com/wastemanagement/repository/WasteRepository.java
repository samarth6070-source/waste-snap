package com.wastemanagement.repository;

import java.util.List;

import com.wastemanagement.model.WasteReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WasteRepository extends MongoRepository<WasteReport, String> {

    List<WasteReport> findByStatusOrderByCreatedAtDesc(String status);
    List<WasteReport> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<WasteReport> findAllByOrderByCreatedAtDesc();
}
