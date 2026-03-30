package org.isNotNull.springBoardApp.domain;

import java.time.Instant;
import java.util.UUID;

public final class RecommendationEntity {

    private final UUID id;
    private final UUID referrerId;
    private final UUID refereeId;
    private final UUID subjectUserId;
    private final UUID opportunityId;
    private final String comment;
    private final Instant createdAt;

    public RecommendationEntity(
        final UUID id,
        final UUID referrerId,
        final UUID refereeId,
        final UUID subjectUserId,
        final UUID opportunityId,
        final String comment,
        final Instant createdAt
    ) {
        this.id = id;
        this.referrerId = referrerId;
        this.refereeId = refereeId;
        this.subjectUserId = subjectUserId;
        this.opportunityId = opportunityId;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public RecommendationEntity(
        final UUID referrerId,
        final UUID refereeId,
        final UUID subjectUserId,
        final UUID opportunityId,
        final String comment
    ) {
        this(null, referrerId, refereeId, subjectUserId, opportunityId, comment, null);
    }

    public UUID id() { return this.id; }
    public UUID referrerId() { return this.referrerId; }
    public UUID refereeId() { return this.refereeId; }
    public UUID subjectUserId() { return this.subjectUserId; }
    public UUID opportunityId() { return this.opportunityId; }
    public String comment() { return this.comment; }
    public Instant createdAt() { return this.createdAt; }

    public RecommendationEntity withId(final UUID id) {
        return new RecommendationEntity(id, this.referrerId, this.refereeId, this.subjectUserId, this.opportunityId, this.comment, this.createdAt);
    }

    public RecommendationEntity withCreatedAt(final Instant createdAt) {
        return new RecommendationEntity(this.id, this.referrerId, this.refereeId, this.subjectUserId, this.opportunityId, this.comment, createdAt);
    }
}
