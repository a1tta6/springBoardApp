package org.isNotNull.springBoardApp.service;

import io.jsonwebtoken.Claims;
import org.isNotNull.springBoardApp.common.api.MissingEntityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.isNotNull.springBoardApp.api.AuthJson;
import org.isNotNull.springBoardApp.api.ViewJson;
import org.isNotNull.springBoardApp.domain.CompanyEntity;
import org.isNotNull.springBoardApp.domain.UserEntity;
import org.isNotNull.springBoardApp.domain.UserRole;
import org.isNotNull.springBoardApp.repository.CompanyRepo;
import org.isNotNull.springBoardApp.repository.UserRepo;
import org.isNotNull.springBoardApp.security.CurrentUser;
import org.isNotNull.springBoardApp.security.JwtProps;
import org.isNotNull.springBoardApp.security.JwtService;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Handles registration, login, refresh and current user lookup.
 *
 * Example:
 * The React auth context talks only to this service through the auth endpoints.
 */
@Service
public final class AuthService {

    private final UserRepo users;
    private final CompanyRepo companies;
    private final PasswordEncoder encoder;
    private final AuthenticationManager auth;
    private final JwtService jwt;
    private final JwtProps props;
    private final ViewMapper view;
    private final CurrentUser current;

    public AuthService(
        final UserRepo users,
        final CompanyRepo companies,
        final PasswordEncoder encoder,
        final AuthenticationManager auth,
        final JwtService jwt,
        final JwtProps props,
        final ViewMapper view,
        final CurrentUser current
    ) {
        this.users = users;
        this.companies = companies;
        this.encoder = encoder;
        this.auth = auth;
        this.jwt = jwt;
        this.props = props;
        this.view = view;
        this.current = current;
    }

    public ViewJson.User register(final AuthJson.Register form, final HttpServletResponse response) {
        if (this.users.findByEmail(form.email()).isPresent()) {
            throw new IllegalStateException("User with this email already exists");
        }
        final UserRole role = UserRole.valueOf(form.role().toUpperCase());
        final UserEntity user = new UserEntity(
            form.email(),
            form.username(),
            form.displayName(),
            this.encoder.encode(form.password()),
            role
        );
        if (role == UserRole.EMPLOYER) {
            final CompanyEntity company = this.companies.save(new CompanyEntity(
                form.displayName(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                form.email()
            ));
            final UserEntity linked = user.company(company.id());
            final UserEntity saved = this.users.save(linked);
            this.cookies(saved, response);
            return this.view.user(saved);
        }
        final UserEntity saved = this.users.save(user);
        this.cookies(saved, response);
        return this.view.user(saved);
    }

    public void login(final AuthJson.Login form, final HttpServletResponse response) {
        this.auth.authenticate(new UsernamePasswordAuthenticationToken(form.username(), form.password()));
        final UserEntity user = this.users.findByUsername(form.username())
            .or(() -> this.users.findByEmail(form.username()))
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (user.blocked()) {
            throw new IllegalStateException("User is blocked");
        }
        this.cookies(user, response);
    }

    public ViewJson.User me() {
        return this.view.user(this.current.user());
    }

    public void logout(final HttpServletResponse response) {
        response.addHeader("Set-Cookie", this.cookie("access", "", 0).toString());
        response.addHeader("Set-Cookie", this.cookie("refresh", "", 0).toString());
    }

    public void refresh(final HttpServletRequest request, final HttpServletResponse response) {
        final String token = this.cookie(request, "refresh");
        if (token == null) {
            throw new BadCredentialsException("Invalid credentials");
        }
        final Claims claims = this.jwt.claims(token);
        if (!"refresh".equals(claims.get("kind", String.class))) {
            throw new BadCredentialsException("Invalid credentials");
        }
        final UserEntity user = this.users.findById(UUID.fromString(claims.getSubject()))
            .orElseThrow(() -> new MissingEntityException("User is missing"));
        this.cookies(user, response);
    }

    private void cookies(final UserEntity user, final HttpServletResponse response) {
        response.addHeader("Set-Cookie", this.cookie("access", this.jwt.access(user), this.props.accessMinutes() * 60).toString());
        response.addHeader("Set-Cookie", this.cookie("refresh", this.jwt.refresh(user), this.props.refreshDays() * 24 * 60 * 60).toString());
    }

    private ResponseCookie cookie(final String name, final String value, final long age) {
        return ResponseCookie.from(name, value)
            .httpOnly(true)
            .path("/")
            .sameSite("Lax")
            .maxAge(age)
            .build();
    }

    private String cookie(final HttpServletRequest request, final String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (jakarta.servlet.http.Cookie item : request.getCookies()) {
            if (name.equals(item.getName())) {
                return item.getValue();
            }
        }
        return null;
    }
}
