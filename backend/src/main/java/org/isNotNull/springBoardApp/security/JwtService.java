package org.isNotNull.springBoardApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.isNotNull.springBoardApp.domain.UserEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * Issues and validates jwt tokens stored in cookies.
 *
 * Example:
 * Login creates access and refresh tokens from a user account.
 */
@Service
public final class JwtService {

    private final SecretKey key;

    public JwtService(final JwtProps props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String access(final UserEntity user) {
        return this.token(user, ChronoUnit.MINUTES, 30, "access");
    }

    public String refresh(final UserEntity user) {
        return this.token(user, ChronoUnit.DAYS, 14, "refresh");
    }

    public Claims claims(final String token) {
        return Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token).getPayload();
    }

    private String token(final UserEntity user, final ChronoUnit unit, final long amount, final String kind) {
        final Instant now = Instant.now();
        return Jwts.builder()
            .subject(user.id().toString())
            .claims(Map.of("role", user.role().name(), "kind", kind))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(amount, unit)))
            .signWith(this.key)
            .compact();
    }
}
