package com.wastemanagement.repository;

import com.wastemanagement.model.WasteItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WasteItemRepository extends MongoRepository<WasteItem, String> {
}
