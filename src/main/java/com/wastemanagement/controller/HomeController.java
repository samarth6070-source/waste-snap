package com.wastemanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        Object userEmail = session.getAttribute("userEmail");
        model.addAttribute("isLoggedIn", userEmail != null);
        return "index";
    }
}
