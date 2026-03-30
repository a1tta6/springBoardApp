package org.isNotNull.springBoardApp.api;

import org.isNotNull.springBoardApp.service.AppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Applicant endpoints for profile, applications and favorites.
 *
 * Example:
 * The applicant dashboard saves profile fields through these routes.
 */
@RestController
@RequestMapping("/v1/applicant")
public final class ApplicantEndpoint {

    private final AppService app;

    public ApplicantEndpoint(final AppService app) {
        this.app = app;
    }

    @PutMapping("/profile")
    public ViewJson.User profile(@RequestBody final ViewJson.ProfileUpdate form) {
        return this.app.profile(form);
    }

    @PutMapping("/privacy")
    public ViewJson.User privacy(@RequestBody final ViewJson.PrivacyUpdate form) {
        return this.app.privacy(form);
    }

    @GetMapping("/applications")
    public List<ViewJson.Application> applications() {
        return this.app.myApplications();
    }

    @PostMapping("/opportunities/{opportunityId}/applications")
    public void apply(@PathVariable final String opportunityId, @RequestBody final ViewJson.ApplicationCreate form) {
        this.app.apply(opportunityId, form);
    }

    @DeleteMapping("/opportunities/{opportunityId}/applications")
    public void cancelApplication(@PathVariable final String opportunityId) {
        this.app.cancelApplication(opportunityId);
    }

    @GetMapping("/opportunities/{opportunityId}/applied")
    public boolean hasApplied(@PathVariable final String opportunityId) {
        return this.app.hasApplied(opportunityId);
    }

    @GetMapping("/favorites")
    public List<ViewJson.Opportunity> favorites() {
        return this.app.favorites();
    }

    @PostMapping("/favorites/{opportunityId}")
    public void favorite(@PathVariable final String opportunityId) {
        this.app.favorite(opportunityId);
    }

    @DeleteMapping("/favorites/{opportunityId}")
    public void unfavorite(@PathVariable final String opportunityId) {
        this.app.unfavorite(opportunityId);
    }
}
