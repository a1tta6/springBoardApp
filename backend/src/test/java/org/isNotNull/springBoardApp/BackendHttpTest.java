package org.isNotNull.springBoardApp;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End to end tests for the rewritten backend contract.
 *
 * Example:
 * These tests verify public browsing and role protected flows.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public final class BackendHttpTest {

    @Container
    private static final PostgreSQLContainer<?> DB =
        new PostgreSQLContainer<>(DockerImageName.parse("postgis/postgis:14-3.5-alpine").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("springboard")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JdbcTemplate jdbc;

    @DynamicPropertySource
    static void properties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DB::getJdbcUrl);
        registry.add("spring.datasource.username", DB::getUsername);
        registry.add("spring.datasource.password", DB::getPassword);
    }

    @Test
    void public_catalog_returns_seeded_opportunities() throws Exception {
        this.mvc.perform(get("/v1/opportunities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value(Matchers.not(Matchers.blankOrNullString())));
    }

    @Test
    void postgis_extension_is_enabled() {
        final String version = this.jdbc.queryForObject("select PostGIS_Version()", String.class);
        org.junit.jupiter.api.Assertions.assertNotNull(version);
    }

    @Test
    void login_sets_access_cookie_for_demo_applicant() throws Exception {
        this.mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"student@example.com","password":"student123"}
                    """))
            .andExpect(status().isOk())
            .andExpect(cookie().exists("access"));
    }

    @Test
    void preflight_request_returns_cors_headers() throws Exception {
        this.mvc.perform(options("/auth/login")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
            .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void me_returns_authenticated_user_after_login() throws Exception {
        final var login = this.mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"student@example.com","password":"student123"}
                    """))
            .andReturn();

        this.mvc.perform(get("/auth/me").cookie(login.getResponse().getCookies()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("applicant"));
    }

    @Test
    void logout_clears_auth_cookies() throws Exception {
        this.mvc.perform(post("/auth/logout"))
            .andExpect(status().isOk())
            .andExpect(cookie().value("access", ""))
            .andExpect(cookie().value("refresh", ""));
    }

    @Test
    void curator_verifies_company_when_authenticated() throws Exception {
        final var login = this.mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"admin@tramplin.ru","password":"admin123"}
                    """))
            .andReturn();

        final String body = this.mvc.perform(get("/v1/curator/companies/pending").cookie(login.getResponse().getCookies()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        final String company = body.split("\"id\":\"")[1].split("\"")[0];

        this.mvc.perform(patch("/v1/curator/companies/{companyId}/verify", company).cookie(login.getResponse().getCookies()))
            .andExpect(status().isOk());
    }
}
