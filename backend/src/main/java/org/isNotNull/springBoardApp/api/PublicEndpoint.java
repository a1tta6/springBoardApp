package org.isNotNull.springBoardApp.api;

import org.isNotNull.springBoardApp.service.AppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public catalog endpoints for the landing page.
 *
 * Example:
 * The home page can load opportunities, companies and tags without login.
 */
@RestController
@RequestMapping("/v1")
public final class PublicEndpoint {

    private final AppService app;

    public PublicEndpoint(final AppService app) {
        this.app = app;
    }

    @GetMapping("/tags")
    public List<ViewJson.Tag> tags() {
        return this.app.tags();
    }

    @GetMapping("/companies")
    public List<ViewJson.Company> companies() {
        return this.app.companies();
    }

    @GetMapping("/opportunities")
    public List<ViewJson.Opportunity> opportunities() {
        return this.app.opportunities();
    }
}
