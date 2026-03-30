package org.isNotNull.springBoardApp.security;

import org.isNotNull.springBoardApp.domain.UserEntity;
import org.isNotNull.springBoardApp.repository.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Resolves the authenticated domain user from the security context.
 *
 * Example:
 * Services use this object instead of parsing principal strings.
 */
@Component
public final class CurrentUser {

    private final UserRepo users;

    public CurrentUser(final UserRepo users) {
        this.users = users;
    }

    public UserEntity user() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Authentication is required");
        }
        return this.users.findById(UUID.fromString(auth.getName()))
            .orElseThrow(() -> new IllegalStateException("Authenticated user is missing"));
    }
}
