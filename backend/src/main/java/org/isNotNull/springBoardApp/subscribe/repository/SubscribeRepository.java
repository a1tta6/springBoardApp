package org.isNotNull.springBoardApp.subscribe.repository;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.isNotNull.springBoardApp.tables.pojos.MemberOrganizer;
import org.isNotNull.springBoardApp.tables.pojos.Member;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.isNotNull.springBoardApp.tables.pojos.OpportunityParticipant;
import org.isNotNull.springBoardApp.tables.pojos.Organizer;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.isNotNull.springBoardApp.Tables.*;

@Repository
@AllArgsConstructor
public class SubscribeRepository {

    private final DSLContext dslContext;

    public OpportunityParticipant fetchOptionalByMemberIdAndOpportunityId(Long memberId, Long opportunityId, Integer page, Integer pageSize) {
        return dslContext
                .selectFrom(OPPORTUNITY_PARTICIPANT)
                .where(OPPORTUNITY_PARTICIPANT.OPPORTUNITY_ID.eq(opportunityId))
                .and(OPPORTUNITY_PARTICIPANT.MEMBER_ID.eq(memberId))
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchOneInto(OpportunityParticipant.class);
    }

    public MemberOrganizer fetchOptionalByMemberIdAndOrganizerId(Long memberId, Long organizerId, Integer page, Integer pageSize) {
        return dslContext
                .selectFrom(MEMBER_ORGANIZER)
                .where(MEMBER_ORGANIZER.MEMBER_ID.eq(memberId))
                .and(MEMBER_ORGANIZER.ORGANIZER_ID.eq(organizerId))
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchOneInto(MemberOrganizer.class);
    }

    public List<Member> fetchMembersByOpportunityId(Long opportunityId, Integer page, Integer pageSize) {
        return dslContext
                .select(MEMBER.fields())
                .from(MEMBER)
                .innerJoin(OPPORTUNITY_PARTICIPANT).on(OPPORTUNITY_PARTICIPANT.MEMBER_ID.eq(MEMBER.ID))
                .where(OPPORTUNITY_PARTICIPANT.OPPORTUNITY_ID.eq(opportunityId))
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(Member.class);
    }

    public List<Opportunity> fetchOpportunitiesByMemberId(Long memberId, Integer page, Integer pageSize) {
        return dslContext
                .select(OPPORTUNITY.fields())
                .from(OPPORTUNITY)
                .innerJoin(OPPORTUNITY_PARTICIPANT).on(OPPORTUNITY_PARTICIPANT.OPPORTUNITY_ID.eq(OPPORTUNITY.ID))
                .where(OPPORTUNITY_PARTICIPANT.MEMBER_ID.eq(memberId))
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(Opportunity.class);
    }

    public List<Long> fetchOpportunityIdsByMemberId(Long memberId) {
        return dslContext
                .select(OPPORTUNITY.ID)
                .from(OPPORTUNITY)
                .innerJoin(OPPORTUNITY_PARTICIPANT).on(OPPORTUNITY_PARTICIPANT.OPPORTUNITY_ID.eq(OPPORTUNITY.ID))
                .where(OPPORTUNITY_PARTICIPANT.MEMBER_ID.eq(memberId))
                .fetchInto(Long.class);
    }

    public List<Organizer> fetchFavoriteOrganizersByMemberId(Long memberId, Integer page, Integer pageSize) {
        return dslContext.
                select(ORGANIZER.fields())
                .from(ORGANIZER)
                .innerJoin(MEMBER_ORGANIZER).on(MEMBER_ORGANIZER.ORGANIZER_ID.eq(ORGANIZER.ID))
                .where(MEMBER_ORGANIZER.MEMBER_ID.eq(memberId))
                .limit(pageSize)
                .offset((page-1)*pageSize)
                .fetchInto(Organizer.class);
    }
}
