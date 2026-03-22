package org.isNotNull.springBoardApp.repository;

import org.isNotNull.springBoardApp.domain.ApplicationEntity;
import org.isNotNull.springBoardApp.domain.ApplicationStatus;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.isNotNull.springBoardApp.repository.DbTables.*;

/**
 * Access to persisted applications through jOOQ.
 *
 * Example:
 * Applicants read their own applications from this repository.
 */
@Repository
public class ApplicationRepo {

    private final DSLContext dsl;

    public ApplicationRepo(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public ApplicationEntity save(final ApplicationEntity item) {
        final UUID id = item.id() == null ? UUID.randomUUID() : item.id();
        if (item.id() == null || this.findById(id).isEmpty()) {
            this.dsl.insertInto(APPLICATIONS)
                .set(APPLICATION_ID, id)
                .set(APPLICATION_OPPORTUNITY_ID, item.opportunityId())
                .set(APPLICATION_APPLICANT_ID, item.applicantId())
                .set(APPLICATION_STATUS, item.status().name())
                .set(APPLICATION_APPLIED_DATE, Timestamp.from(item.appliedDate()))
                .set(APPLICATION_MESSAGE, item.message())
                .execute();
        } else {
            this.dsl.update(APPLICATIONS)
                .set(APPLICATION_STATUS, item.status().name())
                .set(APPLICATION_MESSAGE, item.message())
                .set(APPLICATION_APPLIED_DATE, Timestamp.from(item.appliedDate()))
                .where(APPLICATION_ID.eq(id))
                .execute();
        }
        return new ApplicationEntity(id, item.opportunityId(), item.applicantId(), item.status(), item.appliedDate(), item.message());
    }

    public Optional<ApplicationEntity> findById(final UUID id) {
        return this.dsl.selectFrom(APPLICATIONS).where(APPLICATION_ID.eq(id)).fetchOptional(this::map);
    }

    public List<ApplicationEntity> findAll() {
        return this.dsl.selectFrom(APPLICATIONS).fetch(this::map);
    }

    public List<ApplicationEntity> findByApplicantId(final UUID applicantId) {
        return this.dsl.selectFrom(APPLICATIONS).where(APPLICATION_APPLICANT_ID.eq(applicantId)).fetch(this::map);
    }

    public Optional<ApplicationEntity> findByOpportunityIdAndApplicantId(final UUID opportunityId, final UUID applicantId) {
        return this.dsl.selectFrom(APPLICATIONS)
            .where(APPLICATION_OPPORTUNITY_ID.eq(opportunityId).and(APPLICATION_APPLICANT_ID.eq(applicantId)))
            .fetchOptional(this::map);
    }

    private ApplicationEntity map(final Record record) {
        return new ApplicationEntity(
            record.get(APPLICATION_ID),
            record.get(APPLICATION_OPPORTUNITY_ID),
            record.get(APPLICATION_APPLICANT_ID),
            ApplicationStatus.valueOf(record.get(APPLICATION_STATUS)),
            record.get(APPLICATION_APPLIED_DATE).toInstant(),
            record.get(APPLICATION_MESSAGE)
        );
    }
}
