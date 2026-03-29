package org.isNotNull.springBoardApp.service;

import org.isNotNull.springBoardApp.api.ViewJson;
import org.isNotNull.springBoardApp.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Maps entities to the JSON shape expected by the frontend.
 *
 * Example:
 * A persisted opportunity becomes the same object shape as the former mock.
 */
@Component
public final class ViewMapper {

    public ViewJson.User user(final UserEntity user) {
        return new ViewJson.User(
            user.id().toString(),
            user.email(),
            user.username(),
            user.displayName(),
            this.text(user.role()),
            user.fullName(),
            user.university(),
            user.course(),
            user.graduationYear(),
            List.copyOf(user.skills()),
            List.copyOf(user.portfolioLinks()),
            user.resume(),
            List.copyOf(user.contacts()),
            new ViewJson.Privacy(user.showApplications(), user.showResume()),
            Optional.ofNullable(user.companyId()).map(UUID::toString).orElse(null),
            user.photo()
        );
    }

    public ViewJson.Company company(final CompanyEntity company) {
        return new ViewJson.Company(
            company.id().toString(),
            company.name(),
            company.inn(),
            company.ogrn(),
            company.address(),
            company.website(),
            company.logo(),
            company.socialLinks(),
            company.bio(),
            company.verified(),
            company.email()
        );
    }

    public ViewJson.Tag tag(final TagEntity tag) {
        return new ViewJson.Tag(tag.id().toString(), tag.name(), this.text(tag.category()));
    }

    public ViewJson.Opportunity opportunity(final OpportunityEntity item) {
        final ViewJson.Salary salary = item.salaryMin() == null && item.salaryMax() == null
            ? null
            : new ViewJson.Salary(item.salaryMin(), item.salaryMax(), item.currency());
        return new ViewJson.Opportunity(
            item.id().toString(),
            item.title(),
            item.description(),
            this.text(item.type()),
            item.companyId().toString(),
            this.text(item.workFormat()),
            new ViewJson.Location(item.city(), item.address(), List.of(item.latitude(), item.longitude())),
            salary,
            item.publishedDate(),
            item.expiryDate(),
            item.eventDate(),
            new ViewJson.Contact(item.contactEmail(), item.contactPhone(), item.contactWebsite()),
            item.tags().stream().map(UUID::toString).toList(),
            this.text(item.status()),
            item.requirements(),
            List.copyOf(item.mediaContent())
        );
    }

    public ViewJson.Application application(final ApplicationEntity item) {
        return new ViewJson.Application(
            item.id().toString(),
            item.opportunityId().toString(),
            item.applicantId().toString(),
            this.text(item.status()),
            item.appliedDate(),
            item.message()
        );
    }

    public ViewJson.Friend friend(final FriendEntity friend, final UserEntity user) {
        return new ViewJson.Friend(
            friend.id().toString(),
            user.id().toString(),
            user.email(),
            user.displayName(),
            user.fullName(),
            user.university(),
            friend.status(),
            friend.createdAt()
        );
    }

    public ViewJson.VerificationRequest verificationRequest(final VerificationRequestEntity req) {
        return new ViewJson.VerificationRequest(
            req.id().toString(),
            req.companyId().toString(),
            req.status(),
            req.rejectionReason(),
            req.createdAt(),
            req.processedAt()
        );
    }

    public ViewJson.Recommendation recommendation(
        final RecommendationEntity rec,
        final UserEntity referrer,
        final UserEntity referee,
        final UserEntity subjectUser,
        final OpportunityEntity opportunity
    ) {
        return new ViewJson.Recommendation(
            rec.id().toString(),
            referrer != null ? this.user(referrer) : null,
            referee != null ? this.user(referee) : null,
            subjectUser != null ? this.user(subjectUser) : null,
            opportunity != null ? this.opportunity(opportunity) : null,
            rec.comment(),
            rec.createdAt()
        );
    }

    private String text(final Enum<?> value) {
        return value.name().toLowerCase().replace('_', '-');
    }
}
