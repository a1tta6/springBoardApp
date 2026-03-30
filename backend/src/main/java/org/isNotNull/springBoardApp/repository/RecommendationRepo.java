package org.isNotNull.springBoardApp.repository;

import org.isNotNull.springBoardApp.domain.RecommendationEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.isNotNull.springBoardApp.repository.DbTables.*;

@Repository
public class RecommendationRepo {

    private final DSLContext dsl;

    public RecommendationRepo(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<RecommendationEntity> findByRefereeId(final UUID refereeId) {
        return this.dsl.selectFrom(RECOMMENDATIONS)
            .where(RECOMMENDATION_REFEREE_ID.eq(refereeId))
            .orderBy(RECOMMENDATION_CREATED_AT.desc())
            .fetch(this::map);
    }

    public List<RecommendationEntity> findByReferrerId(final UUID referrerId) {
        return this.dsl.selectFrom(RECOMMENDATIONS)
            .where(RECOMMENDATION_REFERRER_ID.eq(referrerId))
            .orderBy(RECOMMENDATION_CREATED_AT.desc())
            .fetch(this::map);
    }

    public List<RecommendationEntity> findBySubjectUserId(final UUID subjectUserId) {
        return this.dsl.selectFrom(RECOMMENDATIONS)
            .where(RECOMMENDATION_SUBJECT_USER_ID.eq(subjectUserId))
            .orderBy(RECOMMENDATION_CREATED_AT.desc())
            .fetch(this::map);
    }
    
    public Optional<RecommendationEntity> findRecommendation(final UUID referrerId, final UUID refereeId, final UUID subjectUserId, final UUID opportunityId) {
        var query = this.dsl.selectFrom(RECOMMENDATIONS)
            .where(RECOMMENDATION_REFERRER_ID.eq(referrerId))
            .and(RECOMMENDATION_REFEREE_ID.eq(refereeId));
            
        if (subjectUserId != null) {
            query = query.and(RECOMMENDATION_SUBJECT_USER_ID.eq(subjectUserId));
        } else {
            query = query.and(RECOMMENDATION_SUBJECT_USER_ID.isNull());
        }
            
        if (opportunityId != null) {
            query = query.and(RECOMMENDATION_OPPORTUNITY_ID.eq(opportunityId));
        } else {
            query = query.and(RECOMMENDATION_OPPORTUNITY_ID.isNull());
        }
        
        return query.fetchOptional(this::map);
    }

    public RecommendationEntity save(final RecommendationEntity entity) {
        final UUID id = entity.id() == null ? UUID.randomUUID() : entity.id();
        final Instant createdAt = entity.createdAt() == null ? Instant.now() : entity.createdAt();
        
        this.dsl.insertInto(RECOMMENDATIONS)
            .set(RECOMMENDATION_ID, id)
            .set(RECOMMENDATION_REFERRER_ID, entity.referrerId())
            .set(RECOMMENDATION_REFEREE_ID, entity.refereeId())
            .set(RECOMMENDATION_SUBJECT_USER_ID, entity.subjectUserId())
            .set(RECOMMENDATION_OPPORTUNITY_ID, entity.opportunityId())
            .set(RECOMMENDATION_COMMENT, entity.comment())
            .set(RECOMMENDATION_CREATED_AT, Timestamp.from(createdAt))
            .execute();
            
        return entity.withId(id).withCreatedAt(createdAt);
    }

    private RecommendationEntity map(final Record record) {
        return new RecommendationEntity(
            record.get(RECOMMENDATION_ID),
            record.get(RECOMMENDATION_REFERRER_ID),
            record.get(RECOMMENDATION_REFEREE_ID),
            record.get(RECOMMENDATION_SUBJECT_USER_ID),
            record.get(RECOMMENDATION_OPPORTUNITY_ID),
            record.get(RECOMMENDATION_COMMENT),
            record.get(RECOMMENDATION_CREATED_AT).toInstant()
        );
    }
}
