package org.isNotNull.springBoardApp.api;

import org.isNotNull.springBoardApp.service.AppService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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

    @PatchMapping("/opportunities/{opportunityId}")
    public ViewJson.Opportunity update(@PathVariable final String opportunityId, @RequestBody final ViewJson.OpportunityCreate form) {
        return this.app.updateOpportunity(opportunityId, form);
    }

    @DeleteMapping("/opportunities/{opportunityId}")
    public void delete(@PathVariable final String opportunityId) {
        this.app.deleteOpportunity(opportunityId);
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

    @GetMapping("/verification")
    public ViewJson.VerificationRequest verificationStatus() {
        return this.app.getVerificationStatus();
    }

    @PostMapping("/verification/submit")
    public ViewJson.VerificationRequest submitVerification() {
        return this.app.submitVerificationRequest();
    }

    @PatchMapping("/company")
    public void updateCompany(@RequestBody final ViewJson.CompanyUpdate form) {
        this.app.updateCompanyProfile(form);
    }
}
