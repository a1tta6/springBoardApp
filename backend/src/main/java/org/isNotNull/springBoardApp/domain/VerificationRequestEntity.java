package org.isNotNull.springBoardApp.domain;

import java.util.UUID;

/**
 * Company verification request from employer.
 */
public final class VerificationRequestEntity {

    private final UUID id;
    private final UUID companyId;
    private final String status;
    private final String rejectionReason;
    private final java.time.Instant createdAt;
    private final java.time.Instant processedAt;

    public VerificationRequestEntity(
        final UUID id,
        final UUID companyId,
        final String status,
        final String rejectionReason,
        final java.time.Instant createdAt,
        final java.time.Instant processedAt
    ) {
        this.id = id;
        this.companyId = companyId;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public VerificationRequestEntity(final UUID companyId) {
        this(null, companyId, "pending", null, java.time.Instant.now(), null);
    }

    public UUID id() { return this.id; }
    public UUID companyId() { return this.companyId; }
    public String status() { return this.status; }
    public String rejectionReason() { return this.rejectionReason; }
    public java.time.Instant createdAt() { return this.createdAt; }
    public java.time.Instant processedAt() { return this.processedAt; }

    public VerificationRequestEntity approved() {
        return new VerificationRequestEntity(this.id, this.companyId, "approved", this.rejectionReason, this.createdAt, java.time.Instant.now());
    }

    public VerificationRequestEntity rejected(final String reason) {
        return new VerificationRequestEntity(this.id, this.companyId, "rejected", reason, this.createdAt, java.time.Instant.now());
    }

    public VerificationRequestEntity pending() {
        return new VerificationRequestEntity(this.id, this.companyId, "pending", null, java.time.Instant.now(), null);
    }
}