package com.wastemanagement.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.wastemanagement.service.AuthenticatedUser;
import com.wastemanagement.service.AppUserService;
import com.wastemanagement.service.FirebaseAuthService;
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

    private final FirebaseAuthService firebaseAuthService;
    private final AppUserService appUserService;

    @PostMapping("/auth/firebase-login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> payload, HttpSession session) {
        String idToken = payload.get("idToken");
        if (!StringUtils.hasText(idToken)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing ID token."));
        }

        try {
            AuthenticatedUser user = firebaseAuthService.verifyIdToken(idToken);
            session.setAttribute("firebaseUid", user.uid());
            session.setAttribute("userEmail", user.email());
            session.setAttribute("userName", user.name());
            appUserService.upsertLogin(user);
            return ResponseEntity.ok(Map.of("message", "Login successful."));
        } catch (FirebaseAuthException ex) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid sign-in token."));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Sign-in configuration is incomplete."));
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out."));
    }

    @GetMapping("/auth/session")
    public Map<String, Object> session(HttpSession session) {
        boolean loggedIn = session.getAttribute("userEmail") != null;
        return Map.of("loggedIn", loggedIn);
    }
}
