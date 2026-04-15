package com.wastemanagement.controller;

import com.wastemanagement.model.AppUser;
import com.wastemanagement.service.AppUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;

    @PostMapping("/auth/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody Map<String, String> payload, HttpSession session) {
        try {
            AppUser user = appUserService.register(
                    payload.get("name"),
                    payload.get("email"),
                    payload.get("password")
            );
            setSessionUser(session, user);
            return ResponseEntity.ok(Map.of("message", "Signup successful."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Unable to create account."));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> payload, HttpSession session) {
        String email = payload.get("email");
        String password = payload.get("password");
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required."));
        }
        try {
            AppUser user = appUserService.login(email, password);
            setSessionUser(session, user);
            return ResponseEntity.ok(Map.of("message", "Login successful."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Unable to login now."));
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out."));
    }

    @GetMapping("/auth/session")
    public Map<String, Object> session(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        String name = (String) session.getAttribute("userName");
        return Map.of(
                "loggedIn", email != null,
                "userEmail", email == null ? "" : email,
                "userName", name == null ? "" : name
        );
    }

    private void setSessionUser(HttpSession session, AppUser user) {
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName", StringUtils.hasText(user.getName()) ? user.getName() : user.getEmail());
    }
}
