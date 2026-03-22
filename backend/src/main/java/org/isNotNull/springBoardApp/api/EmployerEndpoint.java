package org.isNotNull.springBoardApp.api;

import org.isNotNull.springBoardApp.service.AppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Employer endpoints for company publications and incoming applications.
 *
 * Example:
 * The employer dashboard creates an opportunity with these routes.
 */
@RestController
@RequestMapping("/v1/employer")
public final class EmployerEndpoint {

    private final AppService app;

    public EmployerEndpoint(final AppService app) {
        this.app = app;
    }

    @GetMapping("/opportunities")
    public List<ViewJson.Opportunity> opportunities() {
        return this.app.employerOpportunities();
    }

    @PostMapping("/opportunities")
    public ViewJson.Opportunity create(@RequestBody final ViewJson.OpportunityCreate form) {
        return this.app.create(form);
    }

    @GetMapping("/applications")
    public List<ViewJson.Application> applications() {
        return this.app.employerApplications();
    }

    @GetMapping("/applicants")
    public List<ViewJson.User> applicants() {
        return this.app.applicants();
    }

    @PatchMapping("/applications/{applicationId}")
    public void status(@PathVariable final String applicationId, @RequestBody final ViewJson.StatusUpdate form) {
        this.app.status(applicationId, form);
    }
}
