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
    private final FriendRepo friends;
    private final ViewMapper view;
    private final CurrentUser current;

    public AppService(
        final UserRepo users,
        final CompanyRepo companies,
        final TagRepo tags,
        final OpportunityRepo opportunities,
        final ApplicationRepo applications,
        final FavoriteRepo favorites,
        final FriendRepo friends,
        final ViewMapper view,
        final CurrentUser current
    ) {
        this.users = users;
        this.companies = companies;
        this.tags = tags;
        this.opportunities = opportunities;
        this.applications = applications;
        this.favorites = favorites;
        this.friends = friends;
        this.view = view;
        this.current = current;
    }

    public List<ViewJson.Tag> tags() {
        return this.tags.findAll().stream().map(this.view::tag).toList();
    }

    public List<ViewJson.Company> companies() {
        return this.companies.findAll().stream().map(this.view::company).toList();
    }

    public List<ViewJson.Opportunity> opportunities(final Double minLat, final Double maxLat, final Double minLng, final Double maxLng) {
        final List<OpportunityEntity> items;
        if (minLat != null && maxLat != null && minLng != null && maxLng != null) {
            items = this.opportunities.findByBounds(minLat, maxLat, minLng, maxLng);
        } else {
            items = this.opportunities.findAll();
        }
        return items.stream()
            .sorted(Comparator.comparing(OpportunityEntity::publishedDate).reversed())
            .map(this.view::opportunity)
            .toList();
    }

    public ViewJson.Opportunity opportunity(final String opportunityId) {
        return this.view.opportunity(
            this.opportunities.findById(UUID.fromString(opportunityId))
                .orElseThrow(() -> new MissingEntityException("Opportunity is missing"))
        );
    }

    public ViewJson.User profile(final ViewJson.ProfileUpdate form) {
        final UserEntity user = this.current.user().profile(
            form.displayName(),
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

    public void cancelApplication(final String opportunityId) {
        final UserEntity user = this.current.user();
        final UUID opportunity = UUID.fromString(opportunityId);
        final var application = this.applications.findByOpportunityIdAndApplicantId(opportunity, user.id())
            .orElseThrow(() -> new MissingEntityException("Application is missing"));
        this.applications.delete(application.id());
    }

    public boolean hasApplied(final String opportunityId) {
        final UserEntity user = this.current.user();
        final UUID opportunity = UUID.fromString(opportunityId);
        return this.applications.findByOpportunityIdAndApplicantId(opportunity, user.id()).isPresent();
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

    public List<ViewJson.Friend> myFriends() {
        final UUID userId = this.current.user().id();
        final List<FriendEntity> friendships = this.friends.findFriendsByUserId(userId);
        
        return friendships.stream().map(f -> {
            final UUID friendId = f.requesterId().equals(userId) ? f.addresseeId() : f.requesterId();
            final UserEntity friend = this.users.findById(friendId).orElse(null);
            if (friend == null) return null;
            return this.view.friend(f, friend);
        }).filter(f -> f != null).toList();
    }

    public List<ViewJson.Friend> pendingFriendRequests() {
        final UUID userId = this.current.user().id();
        final List<FriendEntity> requests = this.friends.findPendingRequestsToUser(userId);
        
        return requests.stream().map(r -> {
            final UserEntity requester = this.users.findById(r.requesterId()).orElse(null);
            if (requester == null) return null;
            return this.view.friend(r, requester);
        }).filter(f -> f != null).toList();
    }

    public List<ViewJson.Friend> sentFriendRequests() {
        final UUID userId = this.current.user().id();
        final List<FriendEntity> requests = this.friends.findPendingRequestsFromUser(userId);
        
        return requests.stream().map(r -> {
            final UserEntity addressee = this.users.findById(r.addresseeId()).orElse(null);
            if (addressee == null) return null;
            return this.view.friend(r, addressee);
        }).filter(f -> f != null).toList();
    }

    public void sendFriendRequest(final String userId) {
        final UUID currentUserId = this.current.user().id();
        final UUID targetUserId = UUID.fromString(userId);
        
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalStateException("Cannot send friend request to yourself");
        }
        
        final var existing = this.friends.findFriendship(currentUserId, targetUserId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Friend request already exists");
        }
        
        this.friends.save(new FriendEntity(currentUserId, targetUserId, "pending"));
    }

    public void acceptFriendRequest(final String userId) {
        final UUID currentUserId = this.current.user().id();
        final UUID requesterId = UUID.fromString(userId);
        
        final var friendship = this.friends.findByRequesterAndAddressee(requesterId, currentUserId)
            .orElseThrow(() -> new MissingEntityException("Friend request is missing"));
        
        this.friends.save(friendship.status("accepted"));
    }

    public void rejectFriendRequest(final String userId) {
        final UUID currentUserId = this.current.user().id();
        final UUID requesterId = UUID.fromString(userId);
        
        this.friends.deleteByRequesterAndAddressee(requesterId, currentUserId);
    }

    public void cancelFriendRequest(final String userId) {
        final UUID currentUserId = this.current.user().id();
        final UUID targetUserId = UUID.fromString(userId);
        
        this.friends.deleteByRequesterAndAddressee(currentUserId, targetUserId);
    }

    public void removeFriend(final String userId) {
        final UUID currentUserId = this.current.user().id();
        final UUID friendId = UUID.fromString(userId);
        
        this.friends.deleteByRequesterAndAddressee(currentUserId, friendId);
    }

    public ViewJson.FriendStatus getFriendStatus(final String userId) {
        final UUID currentUserId = this.current.user().id();
        final UUID targetUserId = UUID.fromString(userId);
        
        final var friendship = this.friends.findFriendship(currentUserId, targetUserId);
        
        if (friendship.isEmpty()) {
            return new ViewJson.FriendStatus("none", null);
        }
        
        final var f = friendship.get();
        if (f.status().equals("accepted")) {
            return new ViewJson.FriendStatus("friends", f.id().toString());
        }
        if (f.requesterId().equals(currentUserId)) {
            return new ViewJson.FriendStatus("sent", f.id().toString());
        } else {
            return new ViewJson.FriendStatus("pending", f.id().toString());
        }
    }

    public List<ViewJson.User> searchUsers(final String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        final String searchPattern = "%" + query.toLowerCase() + "%";
        return this.users.findAll().stream()
            .filter(u -> u.role().equals(UserRole.APPLICANT))
            .filter(u -> 
                (u.email() != null && u.email().toLowerCase().contains(query.toLowerCase())) ||
                (u.displayName() != null && u.displayName().toLowerCase().contains(query.toLowerCase())) ||
                (u.fullName() != null && u.fullName().toLowerCase().contains(query.toLowerCase()))
            )
            .map(this.view::user)
            .toList();
    }

    public ViewJson.UserProfile userProfile(final String userId) {
        final UUID targetUserId = UUID.fromString(userId);
        final UserEntity user = this.users.findById(targetUserId)
            .orElseThrow(() -> new MissingEntityException("User is missing"));
        
        final UUID currentUserId = this.current.user().id();
        
        final var friendship = this.friends.findFriendship(currentUserId, targetUserId);
        final boolean isFriend = friendship.isPresent() && friendship.get().status().equals("accepted");
        
        final List<ViewJson.Opportunity> favorites = isFriend 
            ? this.favorites.findByUserId(targetUserId).stream()
                .map(f -> this.opportunities.findById(f.opportunityId()).orElse(null))
                .filter(o -> o != null)
                .map(this.view::opportunity)
                .toList()
            : List.of();
        
        final List<ViewJson.Application> applications = isFriend || user.showApplications()
            ? this.applications.findByApplicantId(targetUserId).stream()
                .map(this.view::application)
                .toList()
            : List.of();
        
        return new ViewJson.UserProfile(
            this.view.user(user),
            isFriend,
            user.showResume(),
            user.showApplications(),
            favorites,
            applications
        );
    }

    private List<String> list(final List<String> items) {
        return items == null ? List.of() : items;
    }

    private List<UUID> ids(final List<String> items) {
        return this.list(items).stream().map(UUID::fromString).toList();
    }
}
