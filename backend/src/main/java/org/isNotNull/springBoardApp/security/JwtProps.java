package org.isNotNull.springBoardApp.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jwt configuration loaded from application properties.
 *
 * Example:
 * Access and refresh token lifetimes are configured in application.yml.
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProps(String secret, long accessMinutes, long refreshDays) {
}
