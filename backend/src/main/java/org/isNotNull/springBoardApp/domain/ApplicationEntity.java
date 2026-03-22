package org.isNotNull.springBoardApp.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Application submitted by an applicant to an opportunity.
 *
 * Example:
 * An employer can change the status from pending to accepted.
 */
public final class ApplicationEntity {

    private final UUID id;
    private final UUID opportunityId;
    private final UUID applicantId;
    private final ApplicationStatus status;
    private final Instant appliedDate;
    private final String message;

    public ApplicationEntity(
        final UUID id,
        final UUID opportunityId,
        final UUID applicantId,
        final ApplicationStatus status,
        final Instant appliedDate,
        final String message
    ) {
        this.id = id;
        this.opportunityId = opportunityId;
        this.applicantId = applicantId;
        this.status = status;
        this.appliedDate = appliedDate;
        this.message = message;
    }

    public ApplicationEntity(
        final UUID opportunityId,
        final UUID applicantId,
        final ApplicationStatus status,
        final Instant appliedDate,
        final String message
    ) {
        this(null, opportunityId, applicantId, status, appliedDate, message);
    }

    public UUID id() { return this.id; }
    public UUID opportunityId() { return this.opportunityId; }
    public UUID applicantId() { return this.applicantId; }
    public ApplicationStatus status() { return this.status; }
    public Instant appliedDate() { return this.appliedDate; }
    public String message() { return this.message; }

    public ApplicationEntity status(final ApplicationStatus state) {
        return new ApplicationEntity(
            this.id,
            this.opportunityId,
            this.applicantId,
            state,
            this.appliedDate,
            this.message
        );
    }
}
