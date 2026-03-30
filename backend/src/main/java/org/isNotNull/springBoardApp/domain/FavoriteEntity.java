package org.isNotNull.springBoardApp.domain;

import java.util.UUID;

/**
 * Favorite link between an applicant and an opportunity.
 *
 * Example:
 * The applicant dashboard lists opportunities from this table.
 */
public final class FavoriteEntity {

    private final UUID id;
    private final UUID userId;
    private final UUID opportunityId;

    public FavoriteEntity(final UUID id, final UUID userId, final UUID opportunityId) {
        this.id = id;
        this.userId = userId;
        this.opportunityId = opportunityId;
    }

    public FavoriteEntity(final UUID userId, final UUID opportunityId) {
        this(null, userId, opportunityId);
    }

    public UUID id() { return this.id; }
    public UUID userId() { return this.userId; }
    public UUID opportunityId() { return this.opportunityId; }
}
