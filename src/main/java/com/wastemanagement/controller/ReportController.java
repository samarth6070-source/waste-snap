package com.wastemanagement.controller;

import com.wastemanagement.model.WasteReport;
import com.wastemanagement.service.FileStorageService;
import com.wastemanagement.service.WasteReportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final WasteReportService wasteReportService;
    private final FileStorageService fileStorageService;

    @GetMapping("/report")
    public String reportPage(Model model, HttpSession session) {
        if (session.getAttribute("userEmail") == null) {
            return "redirect:/";
        }
        model.addAttribute("report", WasteReport.builder().build());
        model.addAttribute("userName", session.getAttribute("userName"));
        return "report";
    }

    @GetMapping("/report/track")
    public String trackRequests(Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/";
        }
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("requests", wasteReportService.findByUserEmail(userEmail));
        return "report-track";
    }

    @PostMapping("/report")
    public String submitReport(
            @ModelAttribute WasteReport report,
            MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("userEmail") == null) {
            return "redirect:/";
        }
        report.setUserEmail((String) session.getAttribute("userEmail"));
        report.setUserName((String) session.getAttribute("userName"));
        String storedImageUrl = fileStorageService.storeImage(imageFile);
        if (storedImageUrl != null) {
            report.setImageUrl(storedImageUrl);
        }
        wasteReportService.submitReport(report);
        redirectAttributes.addFlashAttribute("submitted", true);
        return "redirect:/report";
    }
}
