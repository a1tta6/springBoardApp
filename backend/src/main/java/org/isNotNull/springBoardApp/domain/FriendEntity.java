package org.isNotNull.springBoardApp.domain;

import java.util.UUID;

/**
 * Friend relationship between two users.
 *
 * Example:
 * Applicants can send friend requests and become friends.
 */
public final class FriendEntity {

    private final UUID id;
    private final UUID requesterId;
    private final UUID addresseeId;
    private final String status;
    private final java.time.Instant createdAt;

    public FriendEntity(final UUID id, final UUID requesterId, final UUID addresseeId, final String status, final java.time.Instant createdAt) {
        this.id = id;
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public FriendEntity(final UUID requesterId, final UUID addresseeId, final String status) {
        this(null, requesterId, addresseeId, status, java.time.Instant.now());
    }

    public UUID id() { return this.id; }
    public UUID requesterId() { return this.requesterId; }
    public UUID addresseeId() { return this.addresseeId; }
    public String status() { return this.status; }
    public java.time.Instant createdAt() { return this.createdAt; }

    public FriendEntity status(final String status) {
        return new FriendEntity(this.id, this.requesterId, this.addresseeId, status, this.createdAt);
    }
}