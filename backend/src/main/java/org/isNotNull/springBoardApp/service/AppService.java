package org.isNotNull.springBoardApp.service;

import org.isNotNull.springBoardApp.common.api.MissingEntityException;
import org.isNotNull.springBoardApp.api.ViewJson;
import org.isNotNull.springBoardApp.domain.*;
import org.isNotNull.springBoardApp.repository.*;
import org.isNotNull.springBoardApp.security.CurrentUser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Main application service for dashboard and catalog operations.
 *
 * Example:
 * Public screens and private dashboards both read from this service layer.
 */
@Service
public final class AppService {

    private final UserRepo users;
    private final CompanyRepo companies;
    private final TagRepo tags;
    private final OpportunityRepo opportunities;
    private final ApplicationRepo applications;
    private final FavoriteRepo favorites;
    private final ViewMapper view;
    private final CurrentUser current;

    public AppService(
        final UserRepo users,
        final CompanyRepo companies,
        final TagRepo tags,
        final OpportunityRepo opportunities,
        final ApplicationRepo applications,
        final FavoriteRepo favorites,
        final ViewMapper view,
        final CurrentUser current
    ) {
        this.users = users;
        this.companies = companies;
        this.tags = tags;
        this.opportunities = opportunities;
        this.applications = applications;
        this.favorites = favorites;
        this.view = view;
        this.current = current;
    }

    public List<ViewJson.Tag> tags() {
        return this.tags.findAll().stream().map(this.view::tag).toList();
    }

    public List<ViewJson.Company> companies() {
        return this.companies.findAll().stream().map(this.view::company).toList();
    }

    public List<ViewJson.Opportunity> opportunities() {
        return this.opportunities.findAll().stream()
            .sorted(Comparator.comparing(OpportunityEntity::publishedDate).reversed())
            .map(this.view::opportunity)
            .toList();
    }

    public ViewJson.User profile(final ViewJson.ProfileUpdate form) {
        final UserEntity user = this.current.user().profile(
            form.fullName(),
            form.university(),
            form.course(),
            form.graduationYear(),
            this.list(form.skills()),
            this.list(form.portfolioLinks()),
            form.resume(),
            this.list(form.contacts())
        );
        return this.view.user(this.users.save(user));
    }

    public ViewJson.User privacy(final ViewJson.PrivacyUpdate form) {
        final UserEntity user = this.current.user().privacy(form.showApplications(), form.showResume());
        return this.view.user(this.users.save(user));
    }

    public List<ViewJson.Application> myApplications() {
        return this.applications.findByApplicantId(this.current.user().id()).stream().map(this.view::application).toList();
    }

    public void apply(final String opportunityId, final ViewJson.ApplicationCreate form) {
        final UserEntity user = this.current.user();
        final UUID opportunity = UUID.fromString(opportunityId);
        this.opportunities.findById(opportunity).orElseThrow(() -> new MissingEntityException("Opportunity is missing"));
        if (this.applications.findByOpportunityIdAndApplicantId(opportunity, user.id()).isPresent()) {
            throw new IllegalStateException("Application already exists");
        }
        this.applications.save(new ApplicationEntity(opportunity, user.id(), ApplicationStatus.PENDING, Instant.now(), form.message()));
    }

    public List<ViewJson.Opportunity> favorites() {
        return this.favorites.findByUserId(this.current.user().id()).stream()
            .map(item -> this.opportunities.findById(item.opportunityId()).orElseThrow())
            .map(this.view::opportunity)
            .toList();
    }

    public void favorite(final String opportunityId) {
        final UUID user = this.current.user().id();
        final UUID opportunity = UUID.fromString(opportunityId);
        if (this.favorites.findByUserIdAndOpportunityId(user, opportunity).isEmpty()) {
            this.favorites.save(new FavoriteEntity(user, opportunity));
        }
    }

    public void unfavorite(final String opportunityId) {
        this.favorites.findByUserIdAndOpportunityId(this.current.user().id(), UUID.fromString(opportunityId))
            .ifPresent(this.favorites::delete);
    }

    public List<ViewJson.Opportunity> employerOpportunities() {
        final UserEntity user = this.current.user();
        if (user.companyId() == null) {
            return List.of();
        }
        return this.opportunities.findByCompanyId(user.companyId()).stream().map(this.view::opportunity).toList();
    }

    public ViewJson.Opportunity create(final ViewJson.OpportunityCreate form) {
        final UserEntity user = this.current.user();
        final CompanyEntity company = this.companies.findById(user.companyId())
            .orElseThrow(() -> new MissingEntityException("Company is missing"));
        if (!company.verified()) {
            throw new IllegalStateException("Company verification is required");
        }
        final OpportunityEntity saved = this.opportunities.save(new OpportunityEntity(
            form.title(),
            form.description(),
            OpportunityType.valueOf(form.type().toUpperCase()),
            company.id(),
            WorkFormat.valueOf(form.workFormat().toUpperCase()),
            form.city(),
            form.address(),
            form.latitude(),
            form.longitude(),
            form.salaryMin(),
            form.salaryMax(),
            form.currency() == null ? "RUB" : form.currency(),
            Instant.now(),
            form.expiryDate(),
            form.eventDate(),
            form.contactEmail(),
            form.contactPhone(),
            form.contactWebsite(),
            this.ids(form.tags()),
            OpportunityStatus.PLANNED,
            form.requirements(),
            List.of()
        ));
        return this.view.opportunity(saved);
    }

    public List<ViewJson.Application> employerApplications() {
        final List<UUID> own = this.opportunities.findByCompanyId(this.current.user().companyId()).stream()
            .map(OpportunityEntity::id)
            .toList();
        return this.applications.findAll().stream()
            .filter(item -> own.contains(item.opportunityId()))
            .map(this.view::application)
            .toList();
    }

    public void status(final String applicationId, final ViewJson.StatusUpdate form) {
        final ApplicationEntity item = this.applications.findById(UUID.fromString(applicationId))
            .orElseThrow(() -> new MissingEntityException("Application is missing"))
            .status(ApplicationStatus.valueOf(form.status().toUpperCase()));
        this.applications.save(item);
    }

    public List<ViewJson.Company> pendingCompanies() {
        return this.companies.findByVerified(false).stream().map(this.view::company).toList();
    }

    public List<ViewJson.Opportunity> pendingOpportunities() {
        return this.opportunities.findByStatus(OpportunityStatus.PLANNED).stream().map(this.view::opportunity).toList();
    }

    public void verifyCompany(final String companyId) {
        final CompanyEntity company = this.companies.findById(UUID.fromString(companyId))
            .orElseThrow(() -> new MissingEntityException("Company is missing"))
            .verify();
        this.companies.save(company);
    }

    public void moderate(final String opportunityId, final String status) {
        final OpportunityEntity item = this.opportunities.findById(UUID.fromString(opportunityId))
            .orElseThrow(() -> new MissingEntityException("Opportunity is missing"))
            .status(OpportunityStatus.valueOf(status.toUpperCase()));
        this.opportunities.save(item);
    }

    public List<ViewJson.User> users() {
        return this.users.findAll().stream().map(this.view::user).toList();
    }

    public List<ViewJson.User> applicants() {
        return this.users.findByRole(UserRole.APPLICANT).stream().map(this.view::user).toList();
    }

    public void block(final String userId) {
        final UserEntity user = this.users.findById(UUID.fromString(userId))
            .orElseThrow(() -> new MissingEntityException("User is missing"))
            .block();
        this.users.save(user);
    }

    private List<String> list(final List<String> items) {
        return items == null ? List.of() : items;
    }

    private List<UUID> ids(final List<String> items) {
        return this.list(items).stream().map(UUID::fromString).toList();
    }
}
