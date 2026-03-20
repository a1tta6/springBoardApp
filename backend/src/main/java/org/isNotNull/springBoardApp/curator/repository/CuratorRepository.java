package org.isNotNull.springBoardApp.curator.repository;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.isNotNull.springBoardApp.enums.OpportunityVisibilityStatusType;
import org.isNotNull.springBoardApp.tables.pojos.Organizer;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.isNotNull.springBoardApp.Tables.ORGANIZER;
import static org.isNotNull.springBoardApp.Tables.OPPORTUNITY;

@Repository
@AllArgsConstructor
public class CuratorRepository {
    private final DSLContext dsl;

    public int setEmployerAccredited(Long employerId, boolean isAccredited) {
        return dsl.update(ORGANIZER)
                .set(ORGANIZER.IS_ACCREDITED, isAccredited)
                .where(ORGANIZER.ID.eq(employerId))
                .execute();
    }

    public List<Organizer> listEmployers(Boolean accreditedOnly, Integer page, Integer pageSize) {
        var condition = ORGANIZER.ID.isNotNull();
        if (accreditedOnly != null) {
            condition = condition.and(ORGANIZER.IS_ACCREDITED.eq(accreditedOnly));
        }
        return dsl.selectFrom(ORGANIZER)
                .where(condition)
                .orderBy(ORGANIZER.ID.desc())
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(Organizer.class);
    }

    public Long countEmployers(Boolean accreditedOnly) {
        var condition = ORGANIZER.ID.isNotNull();
        if (accreditedOnly != null) {
            condition = condition.and(ORGANIZER.IS_ACCREDITED.eq(accreditedOnly));
        }
        return dsl.selectCount()
                .from(ORGANIZER)
                .where(condition)
                .fetchOneInto(Long.class);
    }

    public int setOpportunityVisibility(Long opportunityId, OpportunityVisibilityStatusType status) {
        return dsl.update(OPPORTUNITY)
                .set(OPPORTUNITY.VISIBILITY_STATUS, status)
                .set(OPPORTUNITY.PUBLISHED_AT, LocalDateTime.now())
                .where(OPPORTUNITY.ID.eq(opportunityId))
                .execute();
    }
}
