package com.wastemanagement.service;

public record AuthenticatedUser(
        String uid,
        String email,
        String name
) {
}
