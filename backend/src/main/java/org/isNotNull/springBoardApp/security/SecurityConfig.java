package org.isNotNull.springBoardApp.security;

import jakarta.servlet.http.HttpServletResponse;
import org.isNotNull.springBoardApp.domain.UserEntity;
import org.isNotNull.springBoardApp.repository.UserRepo;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Security rules for public pages, dashboards and authentication.
 *
 * Example:
 * The landing page endpoints stay public while role specific endpoints stay protected.
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProps.class)
public class SecurityConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService details(final UserRepo users) {
        return username -> {
            final UserEntity user = users.findByUsername(username)
                .or(() -> users.findByEmail(username))
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(username));
            return User.withUsername(user.id().toString())
                .password(user.passwordHash())
                .roles(user.role().name())
                .disabled(user.blocked())
                .build();
        };
    }

    @Bean
    public AuthenticationManager auth(final UserDetailsService details, final PasswordEncoder encoder) {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(details);
        provider.setPasswordEncoder(encoder);
        return provider::authenticate;
    }

    @Bean
    public CorsConfigurationSource cors() {
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://localhost", "http://194.67.119.172", "http://sprad.ru"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsFilter corsFilter(final CorsConfigurationSource cors) {
        return new CorsFilter(cors);
    }

    @Bean
    public SecurityFilterChain chain(final HttpSecurity http, final JwtFilter filter) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(cfg -> cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/opportunities", "/v1/tags", "/v1/companies").permitAll()
                .requestMatchers("/v1/applicant/**").hasRole("APPLICANT")
                .requestMatchers("/v1/employer/**").hasRole("EMPLOYER")
                .requestMatchers("/v1/curator/**").hasRole("CURATOR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(cfg -> cfg.authenticationEntryPoint((request, response, failure) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Authentication is required\"}");
            }))
            .build();
    }
}
