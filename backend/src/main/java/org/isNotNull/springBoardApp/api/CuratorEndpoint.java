package org.isNotNull.springBoardApp.api;

import org.isNotNull.springBoardApp.service.AppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Curator endpoints for moderation and administration.
 *
 * Example:
 * Pending companies are verified through these routes.
 */
@RestController
@RequestMapping("/v1/curator")
public final class CuratorEndpoint {

    private final AppService app;

    public CuratorEndpoint(final AppService app) {
        this.app = app;
    }

    @GetMapping("/companies/pending")
    public List<ViewJson.Company> companies() {
        return this.app.pendingCompanies();
    }

    @PatchMapping("/companies/{companyId}/verify")
    public void verify(@PathVariable final String companyId) {
        this.app.verifyCompany(companyId);
    }

    @GetMapping("/opportunities/pending")
    public List<ViewJson.Opportunity> opportunities() {
        return this.app.pendingOpportunities();
    }

    @PatchMapping("/opportunities/{opportunityId}")
    public void moderate(@PathVariable final String opportunityId, @RequestBody final ViewJson.StatusUpdate form) {
        this.app.moderate(opportunityId, form.status());
    }

    @GetMapping("/users")
    public List<ViewJson.User> users() {
        return this.app.users();
    }

    @PatchMapping("/users/{userId}/block")
    public void block(@PathVariable final String userId) {
        this.app.block(userId);
    }
}
