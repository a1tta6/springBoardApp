package org.isNotNull.springBoardApp.repository;

import org.isNotNull.springBoardApp.domain.VerificationRequestEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.isNotNull.springBoardApp.repository.DbTables.*;

/**
 * Access to verification requests through jOOQ.
 */
@Repository
public class VerificationRepo {

    private final DSLContext dsl;

    public VerificationRepo(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public VerificationRequestEntity save(final VerificationRequestEntity item) {
        final UUID id = item.id() == null ? UUID.randomUUID() : item.id();
        if (item.id() == null || this.findById(id).isEmpty()) {
            this.dsl.insertInto(VERIFICATION_REQUESTS)
                .set(VERIFICATION_REQUEST_ID, id)
                .set(VERIFICATION_REQUEST_COMPANY_ID, item.companyId())
                .set(VERIFICATION_REQUEST_STATUS, item.status())
                .set(VERIFICATION_REQUEST_REJECTION_REASON, item.rejectionReason())
                .set(VERIFICATION_REQUEST_CREATED_AT, Timestamp.from(item.createdAt()))
                .set(VERIFICATION_REQUEST_PROCESSED_AT, item.processedAt() != null ? Timestamp.from(item.processedAt()) : null)
                .execute();
        } else {
            this.dsl.update(VERIFICATION_REQUESTS)
                .set(VERIFICATION_REQUEST_STATUS, item.status())
                .set(VERIFICATION_REQUEST_REJECTION_REASON, item.rejectionReason())
                .set(VERIFICATION_REQUEST_PROCESSED_AT, item.processedAt() != null ? Timestamp.from(item.processedAt()) : null)
                .where(VERIFICATION_REQUEST_ID.eq(id))
                .execute();
        }
        return new VerificationRequestEntity(
            id,
            item.companyId(),
            item.status(),
            item.rejectionReason(),
            item.createdAt(),
            item.processedAt()
        );
    }

    public Optional<VerificationRequestEntity> findById(final UUID id) {
        return this.dsl.selectFrom(VERIFICATION_REQUESTS).where(VERIFICATION_REQUEST_ID.eq(id)).fetchOptional(this::map);
    }

    public Optional<VerificationRequestEntity> findByCompanyId(final UUID companyId) {
        return this.dsl.selectFrom(VERIFICATION_REQUESTS)
            .where(VERIFICATION_REQUEST_COMPANY_ID.eq(companyId))
            .orderBy(VERIFICATION_REQUEST_CREATED_AT.desc())
            .fetchOptional(this::map);
    }

    public List<VerificationRequestEntity> findPending() {
        return this.dsl.selectFrom(VERIFICATION_REQUESTS)
            .where(VERIFICATION_REQUEST_STATUS.eq("pending"))
            .orderBy(VERIFICATION_REQUEST_CREATED_AT.desc())
            .fetch(this::map);
    }

    private VerificationRequestEntity map(final Record record) {
        return new VerificationRequestEntity(
            record.get(VERIFICATION_REQUEST_ID),
            record.get(VERIFICATION_REQUEST_COMPANY_ID),
            record.get(VERIFICATION_REQUEST_STATUS),
            record.get(VERIFICATION_REQUEST_REJECTION_REASON),
            record.get(VERIFICATION_REQUEST_CREATED_AT).toInstant(),
            record.get(VERIFICATION_REQUEST_PROCESSED_AT) != null ? record.get(VERIFICATION_REQUEST_PROCESSED_AT).toInstant() : null
        );
    }
}