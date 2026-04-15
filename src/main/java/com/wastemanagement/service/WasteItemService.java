package com.wastemanagement.service;

import com.wastemanagement.model.WasteItem;
import com.wastemanagement.repository.WasteItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WasteItemService {

    private final WasteItemRepository wasteItemRepository;

    public WasteItem save(WasteItem item) {
        return wasteItemRepository.save(item);
    }
}
