package org.isNotNull.springBoardApp.repository;

import org.isNotNull.springBoardApp.domain.OpportunityEntity;
import org.isNotNull.springBoardApp.domain.OpportunityStatus;
import org.isNotNull.springBoardApp.domain.OpportunityType;
import org.isNotNull.springBoardApp.domain.WorkFormat;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.isNotNull.springBoardApp.repository.DbTables.*;

/**
 * Access to persisted opportunities through jOOQ.
 *
 * Example:
 * Employers read only their own opportunities from this repository.
 */
@Repository
public class OpportunityRepo {

    private final DSLContext dsl;
    private final JsonStore json;

    public OpportunityRepo(final DSLContext dsl, final JsonStore json) {
        this.dsl = dsl;
        this.json = json;
    }

    public OpportunityEntity save(final OpportunityEntity item) {
        final UUID id = item.id() == null ? UUID.randomUUID() : item.id();
        if (item.id() == null || this.findById(id).isEmpty()) {
            this.dsl.insertInto(OPPORTUNITIES)
                .set(OPPORTUNITY_ID, id)
                .set(OPPORTUNITY_TITLE, item.title())
                .set(OPPORTUNITY_DESCRIPTION, item.description())
                .set(OPPORTUNITY_TYPE, item.type().name())
                .set(OPPORTUNITY_COMPANY_ID, item.companyId())
                .set(OPPORTUNITY_WORK_FORMAT, item.workFormat().name())
                .set(OPPORTUNITY_CITY, item.city())
                .set(OPPORTUNITY_ADDRESS, item.address())
                .set(OPPORTUNITY_LATITUDE, item.latitude())
                .set(OPPORTUNITY_LONGITUDE, item.longitude())
                .set(OPPORTUNITY_SALARY_MIN, item.salaryMin())
                .set(OPPORTUNITY_SALARY_MAX, item.salaryMax())
                .set(OPPORTUNITY_CURRENCY, item.currency())
                .set(OPPORTUNITY_PUBLISHED_DATE, Timestamp.from(item.publishedDate()))
                .set(OPPORTUNITY_EXPIRY_DATE, this.date(item.expiryDate()))
                .set(OPPORTUNITY_EVENT_DATE, this.date(item.eventDate()))
                .set(OPPORTUNITY_CONTACT_EMAIL, item.contactEmail())
                .set(OPPORTUNITY_CONTACT_PHONE, item.contactPhone())
                .set(OPPORTUNITY_CONTACT_WEBSITE, item.contactWebsite())
                .set(OPPORTUNITY_TAGS_JSON, this.json.uuidList(item.tags()))
                .set(OPPORTUNITY_STATUS, item.status().name())
                .set(OPPORTUNITY_REQUIREMENTS, item.requirements())
                .set(OPPORTUNITY_MEDIA_JSON, this.json.textList(item.mediaContent()))
                .execute();
        } else {
            this.dsl.update(OPPORTUNITIES)
                .set(OPPORTUNITY_TITLE, item.title())
                .set(OPPORTUNITY_DESCRIPTION, item.description())
                .set(OPPORTUNITY_TYPE, item.type().name())
                .set(OPPORTUNITY_COMPANY_ID, item.companyId())
                .set(OPPORTUNITY_WORK_FORMAT, item.workFormat().name())
                .set(OPPORTUNITY_CITY, item.city())
                .set(OPPORTUNITY_ADDRESS, item.address())
                .set(OPPORTUNITY_LATITUDE, item.latitude())
                .set(OPPORTUNITY_LONGITUDE, item.longitude())
                .set(OPPORTUNITY_SALARY_MIN, item.salaryMin())
                .set(OPPORTUNITY_SALARY_MAX, item.salaryMax())
                .set(OPPORTUNITY_CURRENCY, item.currency())
                .set(OPPORTUNITY_PUBLISHED_DATE, Timestamp.from(item.publishedDate()))
                .set(OPPORTUNITY_EXPIRY_DATE, this.date(item.expiryDate()))
                .set(OPPORTUNITY_EVENT_DATE, this.date(item.eventDate()))
                .set(OPPORTUNITY_CONTACT_EMAIL, item.contactEmail())
                .set(OPPORTUNITY_CONTACT_PHONE, item.contactPhone())
                .set(OPPORTUNITY_CONTACT_WEBSITE, item.contactWebsite())
                .set(OPPORTUNITY_TAGS_JSON, this.json.uuidList(item.tags()))
                .set(OPPORTUNITY_STATUS, item.status().name())
                .set(OPPORTUNITY_REQUIREMENTS, item.requirements())
                .set(OPPORTUNITY_MEDIA_JSON, this.json.textList(item.mediaContent()))
                .where(OPPORTUNITY_ID.eq(id))
                .execute();
        }
        return new OpportunityEntity(
            id,
            item.title(),
            item.description(),
            item.type(),
            item.companyId(),
            item.workFormat(),
            item.city(),
            item.address(),
            item.latitude(),
            item.longitude(),
            item.salaryMin(),
            item.salaryMax(),
            item.currency(),
            item.publishedDate(),
            item.expiryDate(),
            item.eventDate(),
            item.contactEmail(),
            item.contactPhone(),
            item.contactWebsite(),
            item.tags(),
            item.status(),
            item.requirements(),
            item.mediaContent()
        );
    }

    public Optional<OpportunityEntity> findById(final UUID id) {
        return this.dsl.selectFrom(OPPORTUNITIES).where(OPPORTUNITY_ID.eq(id)).fetchOptional(this::map);
    }

    public List<OpportunityEntity> findAll() {
        return this.dsl.selectFrom(OPPORTUNITIES).fetch(this::map);
    }

    public List<OpportunityEntity> findByCompanyId(final UUID companyId) {
        return this.dsl.selectFrom(OPPORTUNITIES).where(OPPORTUNITY_COMPANY_ID.eq(companyId)).fetch(this::map);
    }

    public List<OpportunityEntity> findByStatus(final OpportunityStatus status) {
        return this.dsl.selectFrom(OPPORTUNITIES).where(OPPORTUNITY_STATUS.eq(status.name())).fetch(this::map);
    }

    private OpportunityEntity map(final Record record) {
        return new OpportunityEntity(
            record.get(OPPORTUNITY_ID),
            record.get(OPPORTUNITY_TITLE),
            record.get(OPPORTUNITY_DESCRIPTION),
            OpportunityType.valueOf(record.get(OPPORTUNITY_TYPE)),
            record.get(OPPORTUNITY_COMPANY_ID),
            WorkFormat.valueOf(record.get(OPPORTUNITY_WORK_FORMAT)),
            record.get(OPPORTUNITY_CITY),
            record.get(OPPORTUNITY_ADDRESS),
            record.get(OPPORTUNITY_LATITUDE),
            record.get(OPPORTUNITY_LONGITUDE),
            record.get(OPPORTUNITY_SALARY_MIN),
            record.get(OPPORTUNITY_SALARY_MAX),
            record.get(OPPORTUNITY_CURRENCY),
            record.get(OPPORTUNITY_PUBLISHED_DATE).toInstant(),
            record.get(OPPORTUNITY_EXPIRY_DATE) == null ? null : record.get(OPPORTUNITY_EXPIRY_DATE).toLocalDate(),
            record.get(OPPORTUNITY_EVENT_DATE) == null ? null : record.get(OPPORTUNITY_EVENT_DATE).toLocalDate(),
            record.get(OPPORTUNITY_CONTACT_EMAIL),
            record.get(OPPORTUNITY_CONTACT_PHONE),
            record.get(OPPORTUNITY_CONTACT_WEBSITE),
            this.json.uuidList(record.get(OPPORTUNITY_TAGS_JSON)),
            OpportunityStatus.valueOf(record.get(OPPORTUNITY_STATUS)),
            record.get(OPPORTUNITY_REQUIREMENTS),
            this.json.textList(record.get(OPPORTUNITY_MEDIA_JSON))
        );
    }

    private Date date(final java.time.LocalDate value) {
        return value == null ? null : Date.valueOf(value);
    }
}
