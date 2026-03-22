package org.isNotNull.springBoardApp.repository;

import org.isNotNull.springBoardApp.domain.CompanyEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.isNotNull.springBoardApp.repository.DbTables.*;

/**
 * Access to persisted companies through jOOQ.
 *
 * Example:
 * Curators list unverified companies from this repository.
 */
@Repository
public class CompanyRepo {

    private final DSLContext dsl;

    public CompanyRepo(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public CompanyEntity save(final CompanyEntity company) {
        final UUID id = company.id() == null ? UUID.randomUUID() : company.id();
        if (company.id() == null || this.findById(id).isEmpty()) {
            this.dsl.insertInto(COMPANIES)
                .set(COMPANY_ID, id)
                .set(COMPANY_NAME, company.name())
                .set(COMPANY_DESCRIPTION, company.description())
                .set(COMPANY_INDUSTRY, company.industry())
                .set(COMPANY_WEBSITE, company.website())
                .set(COMPANY_LINKEDIN, company.linkedin())
                .set(COMPANY_VK, company.vk())
                .set(COMPANY_TELEGRAM, company.telegram())
                .set(COMPANY_VERIFIED, company.verified())
                .set(COMPANY_EMAIL, company.email())
                .execute();
        } else {
            this.dsl.update(COMPANIES)
                .set(COMPANY_NAME, company.name())
                .set(COMPANY_DESCRIPTION, company.description())
                .set(COMPANY_INDUSTRY, company.industry())
                .set(COMPANY_WEBSITE, company.website())
                .set(COMPANY_LINKEDIN, company.linkedin())
                .set(COMPANY_VK, company.vk())
                .set(COMPANY_TELEGRAM, company.telegram())
                .set(COMPANY_VERIFIED, company.verified())
                .set(COMPANY_EMAIL, company.email())
                .where(COMPANY_ID.eq(id))
                .execute();
        }
        return new CompanyEntity(id, company.name(), company.description(), company.industry(), company.website(), company.linkedin(), company.vk(), company.telegram(), company.verified(), company.email());
    }

    public Optional<CompanyEntity> findById(final UUID id) {
        return this.dsl.selectFrom(COMPANIES).where(COMPANY_ID.eq(id)).fetchOptional(this::map);
    }

    public List<CompanyEntity> findAll() {
        return this.dsl.selectFrom(COMPANIES).fetch(this::map);
    }

    public List<CompanyEntity> findByVerified(final boolean verified) {
        return this.dsl.selectFrom(COMPANIES).where(COMPANY_VERIFIED.eq(verified)).fetch(this::map);
    }

    private CompanyEntity map(final Record record) {
        return new CompanyEntity(
            record.get(COMPANY_ID),
            record.get(COMPANY_NAME),
            record.get(COMPANY_DESCRIPTION),
            record.get(COMPANY_INDUSTRY),
            record.get(COMPANY_WEBSITE),
            record.get(COMPANY_LINKEDIN),
            record.get(COMPANY_VK),
            record.get(COMPANY_TELEGRAM),
            Boolean.TRUE.equals(record.get(COMPANY_VERIFIED)),
            record.get(COMPANY_EMAIL)
        );
    }
}
