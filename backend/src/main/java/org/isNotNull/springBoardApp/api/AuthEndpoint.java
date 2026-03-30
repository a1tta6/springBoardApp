package org.isNotNull.springBoardApp.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.isNotNull.springBoardApp.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints consumed by the frontend auth context.
 *
 * Example:
 * Login sets cookies and me returns the authenticated user object.
 */
@RestController
@RequestMapping("/auth")
public final class AuthEndpoint {

    private final AuthService auth;

    public AuthEndpoint(final AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public ViewJson.User register(@RequestBody @Valid final AuthJson.Register form, final HttpServletResponse response) {
        return this.auth.register(form, response);
    }

    @PostMapping("/login")
    public void login(@RequestBody @Valid final AuthJson.Login form, final HttpServletResponse response) {
        this.auth.login(form, response);
    }

    @GetMapping("/me")
    public ViewJson.User me() {
        return this.auth.me();
    }

    @PostMapping("/logout")
    public void logout(final HttpServletResponse response) {
        this.auth.logout(response);
    }

    @PostMapping("/refresh")
    public void refresh(final HttpServletRequest request, final HttpServletResponse response) {
        this.auth.refresh(request, response);
    }
}
