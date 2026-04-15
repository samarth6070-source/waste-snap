package com.wastemanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${app.firebase.web-client-id:}")
    private String firebaseWebClientId;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        Object userEmail = session.getAttribute("userEmail");
        model.addAttribute("isLoggedIn", userEmail != null);
        model.addAttribute("firebaseWebClientId", firebaseWebClientId);
        return "index";
    }
}
