package com.wastemanagement.service;

import com.wastemanagement.model.AppUser;
import com.wastemanagement.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.auth.min-password-length:6}")
    private int minPasswordLength;

    public AppUser register(String name, String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (!StringUtils.hasText(normalizedEmail)) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (!StringUtils.hasText(password) || password.length() < minPasswordLength) {
            throw new IllegalArgumentException("Password must be at least " + minPasswordLength + " characters.");
        }
        if (appUserRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("Account already exists. Please login.");
        }

        LocalDateTime now = LocalDateTime.now();
        AppUser user = AppUser.builder()
                .id(normalizedEmail)
                .email(normalizedEmail)
                .name(cleanName(name))
                .passwordHash(passwordEncoder.encode(password))
                .createdAt(now)
                .lastLoginAt(now)
                .build();
        return appUserRepository.save(user);
    }

    public AppUser login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (!StringUtils.hasText(normalizedEmail) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Email and password are required.");
        }
        Optional<AppUser> userOpt = appUserRepository.findByEmailIgnoreCase(normalizedEmail);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        AppUser user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        user.setLastLoginAt(LocalDateTime.now());
        if (!StringUtils.hasText(user.getName())) {
            user.setName(defaultNameFromEmail(user.getEmail()));
        }
        return appUserRepository.save(user);
    }

    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmailIgnoreCase(normalizeEmail(email)).orElse(null);
    }

    public List<AppUser> listUsers() {
        return appUserRepository.findAllByOrderByLastLoginAtDesc();
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String cleanName(String name) {
        if (StringUtils.hasText(name)) {
            return name.trim();
        }
        return "";
    }

    private String defaultNameFromEmail(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            return "User";
        }
        return email.substring(0, email.indexOf('@'));
    }
}

