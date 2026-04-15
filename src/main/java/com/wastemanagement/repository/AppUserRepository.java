package com.wastemanagement.repository;

import com.wastemanagement.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    List<AppUser> findAllByOrderByLastLoginAtDesc();
}

