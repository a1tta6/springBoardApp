package org.isNotNull.springBoardApp.repository;

import org.isNotNull.springBoardApp.domain.UserEntity;
import org.isNotNull.springBoardApp.domain.UserRole;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.isNotNull.springBoardApp.repository.DbTables.*;

/**
 * Access to persisted users through jOOQ.
 *
 * Example:
 * Authentication resolves a user by username.
 */
@Repository
public class UserRepo {

    private final DSLContext dsl;
    private final JsonStore json;

    public UserRepo(final DSLContext dsl, final JsonStore json) {
        this.dsl = dsl;
        this.json = json;
    }

    public Optional<UserEntity> findByUsername(final String username) {
        return this.dsl.selectFrom(USERS).where(USER_USERNAME.eq(username)).fetchOptional(this::map);
    }

    public Optional<UserEntity> findByEmail(final String email) {
        return this.dsl.selectFrom(USERS).where(USER_EMAIL.eq(email)).fetchOptional(this::map);
    }

    public Optional<UserEntity> findById(final UUID id) {
        return this.dsl.selectFrom(USERS).where(USER_ID.eq(id)).fetchOptional(this::map);
    }

    public List<UserEntity> findAll() {
        return this.dsl.selectFrom(USERS).fetch(this::map);
    }

    public List<UserEntity> findByRole(final UserRole role) {
        return this.dsl.selectFrom(USERS).where(USER_ROLE.eq(role.name())).fetch(this::map);
    }

    public UserEntity save(final UserEntity user) {
        final UUID id = user.id() == null ? UUID.randomUUID() : user.id();
        if (user.id() == null || this.findById(id).isEmpty()) {
            this.dsl.insertInto(USERS)
                .set(USER_ID, id)
                .set(USER_EMAIL, user.email())
                .set(USER_USERNAME, user.username())
                .set(USER_DISPLAY_NAME, user.displayName())
                .set(USER_PASSWORD_HASH, user.passwordHash())
                .set(USER_ROLE, user.role().name())
                .set(USER_FULL_NAME, user.fullName())
                .set(USER_UNIVERSITY, user.university())
                .set(USER_COURSE, user.course())
                .set(USER_GRADUATION_YEAR, user.graduationYear())
                .set(USER_SKILLS_JSON, this.json.textList(user.skills()))
                .set(USER_PORTFOLIO_JSON, this.json.textList(user.portfolioLinks()))
                .set(USER_RESUME, user.resume())
                .set(USER_CONTACTS_JSON, this.json.textList(user.contacts()))
                .set(USER_SHOW_APPLICATIONS, user.showApplications())
                .set(USER_SHOW_RESUME, user.showResume())
                .set(USER_COMPANY_ID, user.companyId())
                .set(USER_BLOCKED, user.blocked())
                .execute();
        } else {
            this.dsl.update(USERS)
                .set(USER_EMAIL, user.email())
                .set(USER_USERNAME, user.username())
                .set(USER_DISPLAY_NAME, user.displayName())
                .set(USER_PASSWORD_HASH, user.passwordHash())
                .set(USER_ROLE, user.role().name())
                .set(USER_FULL_NAME, user.fullName())
                .set(USER_UNIVERSITY, user.university())
                .set(USER_COURSE, user.course())
                .set(USER_GRADUATION_YEAR, user.graduationYear())
                .set(USER_SKILLS_JSON, this.json.textList(user.skills()))
                .set(USER_PORTFOLIO_JSON, this.json.textList(user.portfolioLinks()))
                .set(USER_RESUME, user.resume())
                .set(USER_CONTACTS_JSON, this.json.textList(user.contacts()))
                .set(USER_SHOW_APPLICATIONS, user.showApplications())
                .set(USER_SHOW_RESUME, user.showResume())
                .set(USER_COMPANY_ID, user.companyId())
                .set(USER_BLOCKED, user.blocked())
                .where(USER_ID.eq(id))
                .execute();
        }
        return new UserEntity(
            id,
            user.email(),
            user.username(),
            user.displayName(),
            user.passwordHash(),
            user.role(),
            user.fullName(),
            user.university(),
            user.course(),
            user.graduationYear(),
            user.skills(),
            user.portfolioLinks(),
            user.resume(),
            user.contacts(),
            user.showApplications(),
            user.showResume(),
            user.companyId(),
            user.blocked()
        );
    }

    public long count() {
        return this.dsl.fetchCount(USERS);
    }

    private UserEntity map(final Record record) {
        return new UserEntity(
            record.get(USER_ID),
            record.get(USER_EMAIL),
            record.get(USER_USERNAME),
            record.get(USER_DISPLAY_NAME),
            record.get(USER_PASSWORD_HASH),
            UserRole.valueOf(record.get(USER_ROLE)),
            record.get(USER_FULL_NAME),
            record.get(USER_UNIVERSITY),
            record.get(USER_COURSE),
            record.get(USER_GRADUATION_YEAR),
            this.json.textList(record.get(USER_SKILLS_JSON)),
            this.json.textList(record.get(USER_PORTFOLIO_JSON)),
            record.get(USER_RESUME),
            this.json.textList(record.get(USER_CONTACTS_JSON)),
            Boolean.TRUE.equals(record.get(USER_SHOW_APPLICATIONS)),
            !Boolean.FALSE.equals(record.get(USER_SHOW_RESUME)),
            record.get(USER_COMPANY_ID),
            Boolean.TRUE.equals(record.get(USER_BLOCKED))
        );
    }
}
