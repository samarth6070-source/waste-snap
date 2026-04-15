package com.wastemanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.wastemanagement.model.WasteReport;
import com.wastemanagement.repository.WasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WasteReportService {

    private final WasteRepository wasteRepository;

    public WasteReport save(WasteReport report) {
        return wasteRepository.save(report);
    }

    public List<WasteReport> findByStatus(String status) {
        return wasteRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public List<WasteReport> findByUserEmail(String userEmail) {
        return wasteRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }

    public List<WasteReport> findAllReports() {
        return wasteRepository.findAllByOrderByCreatedAtDesc();
    }

    public WasteReport submitReport(WasteReport report) {
        report.setStatus("PENDING");
        report.setCreatedAt(LocalDateTime.now());
        return wasteRepository.save(report);
    }

    public boolean resolveById(String id) {
        Optional<WasteReport> found = wasteRepository.findById(id);
        if (found.isEmpty()) {
            return false;
        }
        WasteReport report = found.get();
        report.setStatus("RESOLVED");
        report.setResolvedAt(LocalDateTime.now());
        wasteRepository.save(report);
        return true;
    }

    public long clearAllReports() {
        long count = wasteRepository.count();
        wasteRepository.deleteAll();
        return count;
    }
}
