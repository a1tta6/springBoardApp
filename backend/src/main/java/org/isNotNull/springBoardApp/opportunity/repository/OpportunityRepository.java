package org.isNotNull.springBoardApp.opportunity.repository;

import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.isNotNull.springBoardApp.Tables.*;

@Repository
@AllArgsConstructor
public class OpportunityRepository {

    private final DSLContext dslContext;

    public List<Opportunity> fetch(Condition condition, Integer page, Integer pageSize) {
        return dslContext
                .selectFrom(OPPORTUNITY)
                .where(condition)
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(Opportunity.class);
    }

    public Opportunity fetchById(Long id) {
        return dslContext
                .selectFrom(OPPORTUNITY)
                .where(OPPORTUNITY.ID.eq(id))
                .fetchOneInto(Opportunity.class);
    }

    public Long count(Condition condition) {
        return dslContext
                .selectCount()
                .from(OPPORTUNITY)
                .where(condition)
                .fetchOneInto(Long.class);
    }

    public List<Long> fetchOpportunityIdsBySelectedTags(List<String> tags) {
        return dslContext
                .select(OPPORTUNITY.ID)
                .from(OPPORTUNITY)
                .join(OPPORTUNITY_TAG).on(OPPORTUNITY.ID.eq(OPPORTUNITY_TAG.OPPORTUNITY_ID))
                .join(TAG).on(TAG.ID.eq(OPPORTUNITY_TAG.TAG_ID))
                .where(TAG.NAME.in(tags))
                .groupBy(OPPORTUNITY.ID)
                .having(DSL.countDistinct(TAG.NAME).eq(tags.size()))
                .fetchInto(Long.class);
    }

    public void recordView(Long userId, Long opportunityId) {
        boolean exists = dslContext.fetchExists(
                dslContext.selectOne()
                        .from(USER_OPPORTUNITY_INTERACTION)
                        .where(USER_OPPORTUNITY_INTERACTION.USER_ID.eq(userId))
                        .and(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID.eq(opportunityId))
                        .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq("VIEW"))
        );

        if (!exists) {
            dslContext.insertInto(USER_OPPORTUNITY_INTERACTION)
                    .set(USER_OPPORTUNITY_INTERACTION.USER_ID, userId)
                    .set(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID, opportunityId)
                    .set(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE, "VIEW")
                    .set(USER_OPPORTUNITY_INTERACTION.CREATED_AT, LocalDateTime.now())
                    .execute();
        }
    }

    public Long fetchViews(Long opportunityId) {
        return dslContext
                .selectCount()
                .from(USER_OPPORTUNITY_INTERACTION)
                .where(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID.eq(opportunityId))
                .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq("VIEW"))
                .fetchOneInto(Long.class);
    }

    public Long fetchSubscriptionsCount(Long opportunityId) {
        return dslContext
                .selectCount()
                .from(OPPORTUNITY_PARTICIPANT)
                .where(OPPORTUNITY_PARTICIPANT.OPPORTUNITY_ID.eq(opportunityId))
                .fetchOneInto(Long.class);
    }
}
