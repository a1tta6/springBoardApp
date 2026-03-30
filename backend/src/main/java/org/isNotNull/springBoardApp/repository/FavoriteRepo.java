package org.isNotNull.springBoardApp.repository;

import org.isNotNull.springBoardApp.domain.FavoriteEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.isNotNull.springBoardApp.repository.DbTables.*;

/**
 * Access to persisted favorites through jOOQ.
 *
 * Example:
 * The applicant dashboard uses favorites to show saved opportunities.
 */
@Repository
public class FavoriteRepo {

    private final DSLContext dsl;

    public FavoriteRepo(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public FavoriteEntity save(final FavoriteEntity item) {
        final UUID id = item.id() == null ? UUID.randomUUID() : item.id();
        final Optional<FavoriteEntity> existing = this.findByUserIdAndOpportunityId(item.userId(), item.opportunityId());
        if (existing.isPresent()) {
            return existing.get();
        }
        this.dsl.insertInto(FAVORITES)
            .set(FAVORITE_ID, id)
            .set(FAVORITE_USER_ID, item.userId())
            .set(FAVORITE_OPPORTUNITY_ID, item.opportunityId())
            .execute();
        return new FavoriteEntity(id, item.userId(), item.opportunityId());
    }

    public List<FavoriteEntity> findByUserId(final UUID userId) {
        return this.dsl.selectFrom(FAVORITES).where(FAVORITE_USER_ID.eq(userId)).fetch(this::map);
    }

    public Optional<FavoriteEntity> findByUserIdAndOpportunityId(final UUID userId, final UUID opportunityId) {
        return this.dsl.selectFrom(FAVORITES)
            .where(FAVORITE_USER_ID.eq(userId).and(FAVORITE_OPPORTUNITY_ID.eq(opportunityId)))
            .fetchOptional(this::map);
    }

    public void delete(final FavoriteEntity item) {
        this.dsl.deleteFrom(FAVORITES).where(FAVORITE_ID.eq(item.id())).execute();
    }

    private FavoriteEntity map(final Record record) {
        return new FavoriteEntity(record.get(FAVORITE_ID), record.get(FAVORITE_USER_ID), record.get(FAVORITE_OPPORTUNITY_ID));
    }
}
