package org.isNotNull.springBoardApp.opportunity.favorite.repository;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.isNotNull.springBoardApp.Tables.OPPORTUNITY;
import static org.isNotNull.springBoardApp.Tables.USER_OPPORTUNITY_INTERACTION;

@Repository
@AllArgsConstructor
public class OpportunityFavoriteRepository {
    private static final String TYPE_FAVORITE = "FAVORITE";

    private final DSLContext dsl;

    public boolean exists(Long userId, Long opportunityId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(USER_OPPORTUNITY_INTERACTION)
                        .where(USER_OPPORTUNITY_INTERACTION.USER_ID.eq(userId))
                        .and(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID.eq(opportunityId))
                        .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq(TYPE_FAVORITE))
        );
    }

    public void insertIgnoreDuplicate(Long userId, Long opportunityId) {
        boolean exists = exists(userId, opportunityId);
        if (exists) return;

        dsl.insertInto(USER_OPPORTUNITY_INTERACTION)
                .set(USER_OPPORTUNITY_INTERACTION.USER_ID, userId)
                .set(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID, opportunityId)
                .set(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE, TYPE_FAVORITE)
                .set(USER_OPPORTUNITY_INTERACTION.CREATED_AT, LocalDateTime.now())
                .execute();
    }

    public int delete(Long userId, Long opportunityId) {
        return dsl.deleteFrom(USER_OPPORTUNITY_INTERACTION)
                .where(USER_OPPORTUNITY_INTERACTION.USER_ID.eq(userId))
                .and(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID.eq(opportunityId))
                .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq(TYPE_FAVORITE))
                .execute();
    }

    public List<Opportunity> listFavorites(Long userId, Integer page, Integer pageSize) {
        return dsl.select(OPPORTUNITY.fields())
                .from(OPPORTUNITY)
                .join(USER_OPPORTUNITY_INTERACTION)
                .on(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID.eq(OPPORTUNITY.ID))
                .and(USER_OPPORTUNITY_INTERACTION.USER_ID.eq(userId))
                .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq(TYPE_FAVORITE))
                .orderBy(USER_OPPORTUNITY_INTERACTION.CREATED_AT.desc())
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(Opportunity.class);
    }

    public Long countFavorites(Long userId) {
        return dsl.selectCount()
                .from(USER_OPPORTUNITY_INTERACTION)
                .where(USER_OPPORTUNITY_INTERACTION.USER_ID.eq(userId))
                .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq(TYPE_FAVORITE))
                .fetchOneInto(Long.class);
    }
}
