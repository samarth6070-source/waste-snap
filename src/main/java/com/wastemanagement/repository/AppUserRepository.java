package com.wastemanagement.repository;

import com.wastemanagement.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    List<AppUser> findAllByOrderByLastLoginAtDesc();
}

