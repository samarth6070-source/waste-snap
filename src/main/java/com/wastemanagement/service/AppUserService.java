package com.wastemanagement.service;

import com.wastemanagement.model.AppUser;
import com.wastemanagement.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public void upsertLogin(AuthenticatedUser user) {
        if (user == null || user.email() == null || user.email().isBlank()) {
            return;
        }
        AppUser entity = AppUser.builder()
                .id(user.email())
                .email(user.email())
                .name(user.name())
                .firebaseUid(user.uid())
                .lastLoginAt(LocalDateTime.now())
                .build();
        appUserRepository.save(entity);
    }

    public List<AppUser> listUsers() {
        return appUserRepository.findAllByOrderByLastLoginAtDesc();
    }
}

