package org.isNotNull.springBoardApp.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Authenticates requests from the access cookie.
 *
 * Example:
 * Requests to protected endpoints become authenticated when the cookie is valid.
 */
@Component
public final class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtFilter(final JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return CorsUtils.isPreFlightRequest(request);
    }

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain chain
    ) throws ServletException, IOException {
        final String token = this.cookie(request, "access");
        if (token != null) {
            try {
                final Claims claims = this.jwt.claims(token);
                final UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        token,
                        List.of(new SimpleGrantedAuthority("ROLE_" + Objects.toString(claims.get("role"), "")))
                    );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (RuntimeException failure) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    private String cookie(final HttpServletRequest request, final String name) {
        final Cookie[] items = request.getCookies();
        if (items == null) {
            return null;
        }
        for (Cookie item : items) {
            if (name.equals(item.getName())) {
                return item.getValue();
            }
        }
        return null;
    }
}
