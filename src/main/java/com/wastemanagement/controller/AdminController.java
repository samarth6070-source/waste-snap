package com.wastemanagement.controller;

import com.wastemanagement.model.AppUser;
import com.wastemanagement.model.WasteReport;
import com.wastemanagement.service.AppUserService;
import com.wastemanagement.service.FileStorageService;
import com.wastemanagement.service.WasteReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final WasteReportService wasteReportService;
    private final FileStorageService fileStorageService;
    private final AppUserService appUserService;

    @GetMapping
    public String adminRoot() {
        return "redirect:/admin/pending";
    }

    @GetMapping("/pending")
    public String pendingReports(Model model) {
        model.addAttribute("reports", wasteReportService.findByStatus("PENDING"));
        model.addAttribute("activeSection", "pending");
        return "admin/pending";
    }

    @GetMapping("/resolved")
    public String resolvedTasks(Model model) {
        model.addAttribute("reports", wasteReportService.findByStatus("RESOLVED"));
        model.addAttribute("activeSection", "resolved");
        return "admin/resolved";
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<AppUser> users = appUserService.listUsers();

        List<WasteReport> reports = wasteReportService.findAllReports();
        Map<String, UserRow> reportStats = new LinkedHashMap<>();
        for (WasteReport r : reports) {
            String email = r.getUserEmail();
            if (email == null || email.isBlank()) continue;
            UserRow row = reportStats.get(email);
            if (row == null) {
                row = new UserRow(email, safe(r.getUserName()), 0, 0, 0, null, r.getCreatedAt());
            }
            int total = row.totalReports + 1;
            int pending = row.pendingReports + ("PENDING".equalsIgnoreCase(r.getStatus()) ? 1 : 0);
            int resolved = row.resolvedReports + ("RESOLVED".equalsIgnoreCase(r.getStatus()) ? 1 : 0);
            LocalDateTime lastReportAt = max(row.lastReportAt, r.getCreatedAt());
            reportStats.put(email, new UserRow(email, row.displayName, total, pending, resolved, null, lastReportAt));
        }

        List<UserRow> rows = users.stream().map(u -> {
            UserRow stats = reportStats.get(u.getEmail());
            String displayName = !safe(u.getName()).isBlank() ? u.getName() : (stats != null ? stats.displayName : "");
            int total = stats != null ? stats.totalReports : 0;
            int pending = stats != null ? stats.pendingReports : 0;
            int resolved = stats != null ? stats.resolvedReports : 0;
            return new UserRow(
                    u.getEmail(),
                    displayName,
                    total,
                    pending,
                    resolved,
                    u.getCreatedAt(),
                    u.getLastLoginAt(),
                    stats != null ? stats.lastReportAt : null
            );
        }).stream().sorted(Comparator
                .comparing(UserRow::lastLoginAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(UserRow::createdAt, Comparator.nullsLast(Comparator.reverseOrder()))
        ).toList();

        model.addAttribute("users", rows);
        model.addAttribute("activeSection", "users");
        return "admin/users";
    }

    @PostMapping("/reports/{id}/resolve")
    public String resolveReport(
            @PathVariable String id,
            RedirectAttributes redirectAttributes) {
        boolean ok = wasteReportService.resolveById(id);
        redirectAttributes.addFlashAttribute("resolvedOk", ok);
        return "redirect:/admin/pending";
    }

    @PostMapping("/clear-all")
    public String clearAll(RedirectAttributes redirectAttributes) {
        long deletedReports = wasteReportService.clearAllReports();
        int deletedImages = fileStorageService.deleteAllStoredImages();
        redirectAttributes.addFlashAttribute("clearedOk", true);
        redirectAttributes.addFlashAttribute("clearedReports", deletedReports);
        redirectAttributes.addFlashAttribute("clearedImages", deletedImages);
        return "redirect:/admin/pending";
    }

    private static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    public record UserRow(
            String email,
            String displayName,
            int totalReports,
            int pendingReports,
            int resolvedReports,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt,
            LocalDateTime lastReportAt
    ) {
    }
}
