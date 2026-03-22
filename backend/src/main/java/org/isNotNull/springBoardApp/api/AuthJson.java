package org.isNotNull.springBoardApp.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payloads for authentication endpoints.
 *
 * Example:
 * The React login page posts Login while register posts Register.
 */
public final class AuthJson {

    private AuthJson() {
    }

    public record Login(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") String password
    ) {
    }

    public record Register(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Display name is required") String displayName,
        @Email(message = "Email must be valid") @NotBlank(message = "Email is required") String email,
        @Size(min = 6, message = "Password must contain at least 6 characters") String password,
        @NotBlank(message = "Role is required") String role
    ) {
    }
}
