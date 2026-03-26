package org.isNotNull.springBoardApp.repository;

import org.isNotNull.springBoardApp.domain.FriendEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.isNotNull.springBoardApp.repository.DbTables.*;

/**
 * Access to persisted friends through jOOQ.
 *
 * Example:
 * Applicants can send, accept, reject friend requests through this repository.
 */
@Repository
public class FriendRepo {

    private final DSLContext dsl;

    public FriendRepo(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public FriendEntity save(final FriendEntity item) {
        final UUID id = item.id() == null ? UUID.randomUUID() : item.id();
        if (item.id() == null || this.findById(id).isEmpty()) {
            this.dsl.insertInto(FRIENDS)
                .set(FRIEND_ID, id)
                .set(FRIEND_REQUESTER_ID, item.requesterId())
                .set(FRIEND_ADDRESSEE_ID, item.addresseeId())
                .set(FRIEND_STATUS, item.status())
                .set(FRIEND_CREATED_AT, Timestamp.from(item.createdAt()))
                .execute();
        } else {
            this.dsl.update(FRIENDS)
                .set(FRIEND_STATUS, item.status())
                .where(FRIEND_ID.eq(id))
                .execute();
        }
        return new FriendEntity(id, item.requesterId(), item.addresseeId(), item.status(), item.createdAt());
    }

    public Optional<FriendEntity> findById(final UUID id) {
        return this.dsl.selectFrom(FRIENDS).where(FRIEND_ID.eq(id)).fetchOptional(this::map);
    }

    public List<FriendEntity> findByRequesterId(final UUID requesterId) {
        return this.dsl.selectFrom(FRIENDS)
            .where(FRIEND_REQUESTER_ID.eq(requesterId))
            .fetch(this::map);
    }

    public List<FriendEntity> findByAddresseeId(final UUID addresseeId) {
        return this.dsl.selectFrom(FRIENDS)
            .where(FRIEND_ADDRESSEE_ID.eq(addresseeId))
            .fetch(this::map);
    }

    public Optional<FriendEntity> findByRequesterAndAddressee(final UUID requesterId, final UUID addresseeId) {
        return this.dsl.selectFrom(FRIENDS)
            .where(FRIEND_REQUESTER_ID.eq(requesterId).and(FRIEND_ADDRESSEE_ID.eq(addresseeId)))
            .fetchOptional(this::map);
    }

    public Optional<FriendEntity> findFriendship(final UUID user1, final UUID user2) {
        return this.dsl.selectFrom(FRIENDS)
            .where(
                (FRIEND_REQUESTER_ID.eq(user1).and(FRIEND_ADDRESSEE_ID.eq(user2)))
                .or(FRIEND_REQUESTER_ID.eq(user2).and(FRIEND_ADDRESSEE_ID.eq(user1)))
            )
            .fetchOptional(this::map);
    }

    public List<FriendEntity> findFriendsByUserId(final UUID userId) {
        return this.dsl.selectFrom(FRIENDS)
            .where(
                (FRIEND_REQUESTER_ID.eq(userId).and(FRIEND_STATUS.eq("accepted")))
                .or(FRIEND_ADDRESSEE_ID.eq(userId).and(FRIEND_STATUS.eq("accepted")))
            )
            .fetch(this::map);
    }

    public List<FriendEntity> findPendingRequestsToUser(final UUID userId) {
        return this.dsl.selectFrom(FRIENDS)
            .where(FRIEND_ADDRESSEE_ID.eq(userId).and(FRIEND_STATUS.eq("pending")))
            .fetch(this::map);
    }

    public List<FriendEntity> findPendingRequestsFromUser(final UUID userId) {
        return this.dsl.selectFrom(FRIENDS)
            .where(FRIEND_REQUESTER_ID.eq(userId).and(FRIEND_STATUS.eq("pending")))
            .fetch(this::map);
    }

    public void delete(final UUID id) {
        this.dsl.deleteFrom(FRIENDS).where(FRIEND_ID.eq(id)).execute();
    }

    public void deleteByRequesterAndAddressee(final UUID requesterId, final UUID addresseeId) {
        this.dsl.deleteFrom(FRIENDS)
            .where(FRIEND_REQUESTER_ID.eq(requesterId).and(FRIEND_ADDRESSEE_ID.eq(addresseeId)))
            .execute();
    }

    private FriendEntity map(final Record record) {
        return new FriendEntity(
            record.get(FRIEND_ID),
            record.get(FRIEND_REQUESTER_ID),
            record.get(FRIEND_ADDRESSEE_ID),
            record.get(FRIEND_STATUS),
            record.get(FRIEND_CREATED_AT).toInstant()
        );
    }
}