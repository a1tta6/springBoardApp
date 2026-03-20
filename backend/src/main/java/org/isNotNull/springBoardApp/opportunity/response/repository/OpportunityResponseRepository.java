package org.isNotNull.springBoardApp.opportunity.response.repository;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.isNotNull.springBoardApp.opportunity.response.dto.OpportunityResponseDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.isNotNull.springBoardApp.Tables.OPPORTUNITY;
import static org.isNotNull.springBoardApp.Tables.USER;

@Repository
@AllArgsConstructor
public class OpportunityResponseRepository {
    private final DSLContext dslContext;

    private static final org.jooq.Table<?> OPPORTUNITY_RESPONSE = DSL.table(DSL.name("public", "opportunity_response"));
    private static final org.jooq.Field<Long> OPPORTUNITY_ID = DSL.field(DSL.name("opportunity_id"), Long.class);
    private static final org.jooq.Field<Long> APPLICANT_ID = DSL.field(DSL.name("applicant_id"), Long.class);
    private static final org.jooq.Field<String> STATUS = DSL.field(DSL.name("status"), String.class);
    private static final org.jooq.Field<LocalDateTime> CREATED_AT = DSL.field(DSL.name("created_at"), LocalDateTime.class);
    private static final org.jooq.Field<LocalDateTime> UPDATED_AT = DSL.field(DSL.name("updated_at"), LocalDateTime.class);

    public void insertIgnoreDuplicate(Long opportunityId, Long applicantId) {
        dslContext.insertInto(OPPORTUNITY_RESPONSE)
                .columns(OPPORTUNITY_ID, APPLICANT_ID)
                .values(opportunityId, applicantId)
                .onConflict(OPPORTUNITY_ID, APPLICANT_ID)
                .doNothing()
                .execute();
    }

    public List<OpportunityResponseDTO> fetchByOpportunityId(Long opportunityId) {
        var rows = dslContext
                .select(OPPORTUNITY_ID, OPPORTUNITY.TITLE, APPLICANT_ID, USER.USERNAME, USER.DISPLAY_NAME, USER.EMAIL, STATUS, CREATED_AT, UPDATED_AT)
                .from(OPPORTUNITY_RESPONSE)
                .join(OPPORTUNITY).on(OPPORTUNITY.ID.eq(OPPORTUNITY_ID))
                .join(USER).on(USER.ID.eq(APPLICANT_ID))
                .where(OPPORTUNITY_ID.eq(opportunityId))
                .orderBy(UPDATED_AT.desc())
                .fetch();

        return rows.map(record -> {
            OpportunityResponseDTO dto = new OpportunityResponseDTO();
            dto.setOpportunityId(record.get(OPPORTUNITY_ID));
            dto.setOpportunityTitle(record.get(OPPORTUNITY.TITLE));
            dto.setApplicantId(record.get(APPLICANT_ID));
            dto.setApplicantUsername(record.get(USER.USERNAME));
            dto.setApplicantDisplayName(record.get(USER.DISPLAY_NAME));
            dto.setApplicantEmail(record.get(USER.EMAIL));
            dto.setStatus(record.get(STATUS));
            dto.setCreatedAt(record.get(CREATED_AT));
            dto.setUpdatedAt(record.get(UPDATED_AT));
            return dto;
        });
    }

    public List<OpportunityResponseDTO> fetchByApplicantId(Long applicantId) {
        var rows = dslContext
                .select(OPPORTUNITY_ID, OPPORTUNITY.TITLE, APPLICANT_ID, USER.USERNAME, USER.DISPLAY_NAME, USER.EMAIL, STATUS, CREATED_AT, UPDATED_AT)
                .from(OPPORTUNITY_RESPONSE)
                .join(OPPORTUNITY).on(OPPORTUNITY.ID.eq(OPPORTUNITY_ID))
                .join(USER).on(USER.ID.eq(APPLICANT_ID))
                .where(APPLICANT_ID.eq(applicantId))
                .orderBy(UPDATED_AT.desc())
                .fetch();

        return rows.map(record -> {
            OpportunityResponseDTO dto = new OpportunityResponseDTO();
            dto.setOpportunityId(record.get(OPPORTUNITY_ID));
            dto.setOpportunityTitle(record.get(OPPORTUNITY.TITLE));
            dto.setApplicantId(record.get(APPLICANT_ID));
            dto.setApplicantUsername(record.get(USER.USERNAME));
            dto.setApplicantDisplayName(record.get(USER.DISPLAY_NAME));
            dto.setApplicantEmail(record.get(USER.EMAIL));
            dto.setStatus(record.get(STATUS));
            dto.setCreatedAt(record.get(CREATED_AT));
            dto.setUpdatedAt(record.get(UPDATED_AT));
            return dto;
        });
    }

    public int updateStatus(Long opportunityId, Long applicantId, String status, LocalDateTime updatedAt) {
        return dslContext.update(OPPORTUNITY_RESPONSE)
                .set(STATUS, status)
                .set(UPDATED_AT, updatedAt)
                .where(OPPORTUNITY_ID.eq(opportunityId).and(APPLICANT_ID.eq(applicantId)))
                .execute();
    }
}
