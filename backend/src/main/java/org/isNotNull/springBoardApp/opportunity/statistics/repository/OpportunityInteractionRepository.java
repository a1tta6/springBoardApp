package org.isNotNull.springBoardApp.opportunity.statistics.repository;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static org.isNotNull.springBoardApp.Tables.OPPORTUNITY;
import static org.isNotNull.springBoardApp.Tables.OPPORTUNITY_PARTICIPANT;
import static org.isNotNull.springBoardApp.Tables.MEMBER_ORGANIZER;
import static org.isNotNull.springBoardApp.Tables.USER_OPPORTUNITY_INTERACTION;

@Repository
@AllArgsConstructor
public class OpportunityInteractionRepository {

    private final DSLContext dsl;

    public int countViewsByOpportunityId(Long opportunityId) {
        return dsl.selectCount()
                .from(USER_OPPORTUNITY_INTERACTION)
                .where(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID.eq(opportunityId)
                        .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq("VIEW")))
                .fetchOne(0, int.class);
    }

    public int countViewsByUserId(Long userId) {
        return dsl.selectCount()
                .from(USER_OPPORTUNITY_INTERACTION)
                .where(USER_OPPORTUNITY_INTERACTION.USER_ID.eq(userId)
                        .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq("VIEW")))
                .fetchOne(0, int.class);
    }

    public int countViewsByUserAndOpportunity(Long userId, Long opportunityId) {
        return dsl.selectCount()
                .from(USER_OPPORTUNITY_INTERACTION)
                .where(USER_OPPORTUNITY_INTERACTION.USER_ID.eq(userId)
                        .and(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID.eq(opportunityId))
                        .and(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq("VIEW")))
                .fetchOne(0, int.class);
    }

    public Long countEmployerViews(Long employerId) {
        return dsl.selectCount()
                .from(USER_OPPORTUNITY_INTERACTION)
                .innerJoin(OPPORTUNITY).on(OPPORTUNITY.ID.eq(USER_OPPORTUNITY_INTERACTION.OPPORTUNITY_ID))
                .where(USER_OPPORTUNITY_INTERACTION.INTERACTION_TYPE.eq("VIEW")
                        .and(OPPORTUNITY.ORGANIZER_ID.eq(employerId)))
                .fetchOneInto(Long.class);
    }

    public Long countEmployerFavorites(Long employerId) {
        return dsl.selectCount()
                .from(MEMBER_ORGANIZER)
                .where(MEMBER_ORGANIZER.ORGANIZER_ID.eq(employerId))
                .groupBy(MEMBER_ORGANIZER.ORGANIZER_ID)
                .fetchOneInto(Long.class);
    }

    public Long countEmployerCandidates(Long employerId) {
        return dsl.selectCount()
                .from(OPPORTUNITY_PARTICIPANT)
                .innerJoin(OPPORTUNITY).on(OPPORTUNITY.ID.eq(OPPORTUNITY_PARTICIPANT.OPPORTUNITY_ID))
                .where(OPPORTUNITY.ORGANIZER_ID.eq(employerId))
                .fetchOneInto(Long.class);
    }

    public Long countEmployerOpportunities(Long employerId) {
        return dsl.selectCount()
                .from(OPPORTUNITY)
                .where(OPPORTUNITY.ORGANIZER_ID.eq(employerId))
                .fetchOneInto(Long.class);
    }
}
