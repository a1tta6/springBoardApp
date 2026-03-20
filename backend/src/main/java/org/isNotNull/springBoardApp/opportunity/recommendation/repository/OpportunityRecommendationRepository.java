package org.isNotNull.springBoardApp.opportunity.recommendation.repository;

import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static org.isNotNull.springBoardApp.Tables.OPPORTUNITY;
import static org.isNotNull.springBoardApp.Tables.OPPORTUNITY_TAG;
import static org.isNotNull.springBoardApp.Tables.TAG;
import static org.isNotNull.springBoardApp.Tables.USER_OPPORTUNITY_INTERACTION;

@Repository
@AllArgsConstructor
public class OpportunityRecommendationRepository {

    private final DSLContext dslContext;

    public List<Opportunity> findRecommendedOpportunities(
            Long userId,
            double lng,
            double lat,
            int page,
            int pageSize,
            Condition additionalConditions
    ) {

        Field<Double> distanceScore = DSL
                .val(0.8).mul(
                        DSL.val(1.0).div(
                                DSL.val(1.0).add(
                                        DSL.field("ST_Distance(ST_SetSRID(ST_Point({0}, {1}), 4326), ST_SetSRID(ST_Point({2}, {3}), 4326)) / 1000.0",
                                                Double.class,
                                                OPPORTUNITY.LONGITUDE, OPPORTUNITY.LATITUDE,
                                                DSL.val(lng), DSL.val(lat)
                                        )
                                )
                        )
                );

        Field<Integer> viewScore = DSL.select(DSL.inline(1))
                .from(USER_OPPORTUNITY_INTERACTION)
                .where(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID.eq(OPPORTUNITY.ID))
                .and(USER_OPPORTUNITY_INTERACTION.USER_ID.eq(userId))
                .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq("VIEW"))
                .asField();

        Field<Integer> interactionScore = DSL.select(DSL.inline(1))
                .from(USER_OPPORTUNITY_INTERACTION)
                .where(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID.eq(OPPORTUNITY.ID))
                .asField();

        Field<Double> totalScore = DSL
                .val(0.8).mul(distanceScore.cast(BigDecimal.class))
                .plus(DSL.val(0.2).mul(DSL.coalesce(viewScore.cast(BigDecimal.class), BigDecimal.ZERO)))
                .plus(DSL.val(0.3).mul(DSL.coalesce(interactionScore.cast(BigDecimal.class), BigDecimal.ZERO)));

        SelectConditionStep<?> baseQuery = dslContext
                .select(
                        OPPORTUNITY.ID,
                        OPPORTUNITY.TITLE,
                        OPPORTUNITY.SHORT_DESCRIPTION,
                        OPPORTUNITY.DESCRIPTION,
                        OPPORTUNITY.FORMAT,
                        OPPORTUNITY.START_DATE_TIME,
                        OPPORTUNITY.END_DATE_TIME,
                        OPPORTUNITY.LOCATION,
                        OPPORTUNITY.LATITUDE,
                        OPPORTUNITY.LONGITUDE,
                        OPPORTUNITY.ORGANIZER_ID,
                        OPPORTUNITY.PICTURES,
                        totalScore.as("score")
                )
                .from(OPPORTUNITY)
                .where(DSL.trueCondition());

        baseQuery = baseQuery.and(additionalConditions);

        return dslContext.selectFrom(baseQuery.asTable("e"))
                .orderBy(DSL.field("score").desc())
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(Opportunity.class);
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

    public Long countRecommendedOpportunities(Condition condition) {
        return dslContext
                .selectCount()
                .from(OPPORTUNITY)
                .where(condition)
                .fetchOneInto(Long.class);
    }
}
