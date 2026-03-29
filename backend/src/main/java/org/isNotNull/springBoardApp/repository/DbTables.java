package org.isNotNull.springBoardApp.repository;

import org.jooq.Field;
import org.jooq.Table;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.unquotedName;

/**
 * Explicit table and field declarations used by jOOQ without code generation.
 *
 * Example:
 * Repository classes reuse these declarations for every query.
 */
public final class DbTables {

    private DbTables() {
    }

    public static final Table<?> USERS = table(unquotedName("users"));
    public static final Field<UUID> USER_ID = field(unquotedName("users", "id"), UUID.class);
    public static final Field<String> USER_EMAIL = field(unquotedName("users", "email"), String.class);
    public static final Field<String> USER_USERNAME = field(unquotedName("users", "username"), String.class);
    public static final Field<String> USER_DISPLAY_NAME = field(unquotedName("users", "display_name"), String.class);
    public static final Field<String> USER_PASSWORD_HASH = field(unquotedName("users", "password_hash"), String.class);
    public static final Field<String> USER_ROLE = field(unquotedName("users", "role"), String.class);
    public static final Field<String> USER_FULL_NAME = field(unquotedName("users", "full_name"), String.class);
    public static final Field<String> USER_UNIVERSITY = field(unquotedName("users", "university"), String.class);
    public static final Field<String> USER_COURSE = field(unquotedName("users", "course"), String.class);
    public static final Field<String> USER_GRADUATION_YEAR = field(unquotedName("users", "graduation_year"), String.class);
    public static final Field<String> USER_SKILLS_JSON = field(unquotedName("users", "skills_json"), String.class);
    public static final Field<String> USER_PORTFOLIO_JSON = field(unquotedName("users", "portfolio_links_json"), String.class);
    public static final Field<String> USER_RESUME = field(unquotedName("users", "resume"), String.class);
    public static final Field<String> USER_CONTACTS_JSON = field(unquotedName("users", "contacts_json"), String.class);
    public static final Field<Boolean> USER_SHOW_APPLICATIONS = field(unquotedName("users", "show_applications"), Boolean.class);
    public static final Field<Boolean> USER_SHOW_RESUME = field(unquotedName("users", "show_resume"), Boolean.class);
    public static final Field<UUID> USER_COMPANY_ID = field(unquotedName("users", "company_id"), UUID.class);
    public static final Field<Boolean> USER_BLOCKED = field(unquotedName("users", "blocked"), Boolean.class);

    public static final Table<?> COMPANIES = table(unquotedName("companies"));
    public static final Field<UUID> COMPANY_ID = field(unquotedName("companies", "id"), UUID.class);
    public static final Field<String> COMPANY_NAME = field(unquotedName("companies", "name"), String.class);
    public static final Field<String> COMPANY_INN = field(unquotedName("companies", "inn"), String.class);
    public static final Field<String> COMPANY_OGRN = field(unquotedName("companies", "ogrn"), String.class);
    public static final Field<String> COMPANY_ADDRESS = field(unquotedName("companies", "address"), String.class);
    public static final Field<String> COMPANY_WEBSITE = field(unquotedName("companies", "website"), String.class);
    public static final Field<String> COMPANY_LOGO = field(unquotedName("companies", "logo"), String.class);
    public static final Field<String> COMPANY_SOCIAL_LINKS = field(unquotedName("companies", "social_links"), String.class);
    public static final Field<String> COMPANY_BIO = field(unquotedName("companies", "bio"), String.class);
    public static final Field<Boolean> COMPANY_VERIFIED = field(unquotedName("companies", "verified"), Boolean.class);
    public static final Field<String> COMPANY_EMAIL = field(unquotedName("companies", "email"), String.class);

    public static final Table<?> TAGS = table(unquotedName("tags"));
    public static final Field<UUID> TAG_ID = field(unquotedName("tags", "id"), UUID.class);
    public static final Field<String> TAG_NAME = field(unquotedName("tags", "name"), String.class);
    public static final Field<String> TAG_CATEGORY = field(unquotedName("tags", "category"), String.class);

    public static final Table<?> OPPORTUNITIES = table(unquotedName("opportunities"));
    public static final Field<UUID> OPPORTUNITY_ID = field(unquotedName("opportunities", "id"), UUID.class);
    public static final Field<String> OPPORTUNITY_TITLE = field(unquotedName("opportunities", "title"), String.class);
    public static final Field<String> OPPORTUNITY_DESCRIPTION = field(unquotedName("opportunities", "description"), String.class);
    public static final Field<String> OPPORTUNITY_TYPE = field(unquotedName("opportunities", "type"), String.class);
    public static final Field<UUID> OPPORTUNITY_COMPANY_ID = field(unquotedName("opportunities", "company_id"), UUID.class);
    public static final Field<String> OPPORTUNITY_WORK_FORMAT = field(unquotedName("opportunities", "work_format"), String.class);
    public static final Field<String> OPPORTUNITY_CITY = field(unquotedName("opportunities", "city"), String.class);
    public static final Field<String> OPPORTUNITY_ADDRESS = field(unquotedName("opportunities", "address"), String.class);
    public static final Field<Double> OPPORTUNITY_LATITUDE = field(unquotedName("opportunities", "latitude"), Double.class);
    public static final Field<Double> OPPORTUNITY_LONGITUDE = field(unquotedName("opportunities", "longitude"), Double.class);
    public static final Field<Integer> OPPORTUNITY_SALARY_MIN = field(unquotedName("opportunities", "salary_min"), Integer.class);
    public static final Field<Integer> OPPORTUNITY_SALARY_MAX = field(unquotedName("opportunities", "salary_max"), Integer.class);
    public static final Field<String> OPPORTUNITY_CURRENCY = field(unquotedName("opportunities", "currency"), String.class);
    public static final Field<Timestamp> OPPORTUNITY_PUBLISHED_DATE = field(unquotedName("opportunities", "published_date"), Timestamp.class);
    public static final Field<Date> OPPORTUNITY_EXPIRY_DATE = field(unquotedName("opportunities", "expiry_date"), Date.class);
    public static final Field<Date> OPPORTUNITY_EVENT_DATE = field(unquotedName("opportunities", "event_date"), Date.class);
    public static final Field<String> OPPORTUNITY_CONTACT_EMAIL = field(unquotedName("opportunities", "contact_email"), String.class);
    public static final Field<String> OPPORTUNITY_CONTACT_PHONE = field(unquotedName("opportunities", "contact_phone"), String.class);
    public static final Field<String> OPPORTUNITY_CONTACT_WEBSITE = field(unquotedName("opportunities", "contact_website"), String.class);
    public static final Field<String> OPPORTUNITY_TAGS_JSON = field(unquotedName("opportunities", "tags_json"), String.class);
    public static final Field<String> OPPORTUNITY_STATUS = field(unquotedName("opportunities", "status"), String.class);
    public static final Field<String> OPPORTUNITY_REQUIREMENTS = field(unquotedName("opportunities", "requirements"), String.class);
    public static final Field<String> OPPORTUNITY_MEDIA_JSON = field(unquotedName("opportunities", "media_content_json"), String.class);

    public static final Table<?> APPLICATIONS = table(unquotedName("applications"));
    public static final Field<UUID> APPLICATION_ID = field(unquotedName("applications", "id"), UUID.class);
    public static final Field<UUID> APPLICATION_OPPORTUNITY_ID = field(unquotedName("applications", "opportunity_id"), UUID.class);
    public static final Field<UUID> APPLICATION_APPLICANT_ID = field(unquotedName("applications", "applicant_id"), UUID.class);
    public static final Field<String> APPLICATION_STATUS = field(unquotedName("applications", "status"), String.class);
    public static final Field<Timestamp> APPLICATION_APPLIED_DATE = field(unquotedName("applications", "applied_date"), Timestamp.class);
    public static final Field<String> APPLICATION_MESSAGE = field(unquotedName("applications", "message"), String.class);

    public static final Table<?> FAVORITES = table(unquotedName("favorites"));
    public static final Field<UUID> FAVORITE_ID = field(unquotedName("favorites", "id"), UUID.class);
    public static final Field<UUID> FAVORITE_USER_ID = field(unquotedName("favorites", "user_id"), UUID.class);
    public static final Field<UUID> FAVORITE_OPPORTUNITY_ID = field(unquotedName("favorites", "opportunity_id"), UUID.class);

    public static final Table<?> FRIENDS = table(unquotedName("friends"));
    public static final Field<UUID> FRIEND_ID = field(unquotedName("friends", "id"), UUID.class);
    public static final Field<UUID> FRIEND_REQUESTER_ID = field(unquotedName("friends", "requester_id"), UUID.class);
    public static final Field<UUID> FRIEND_ADDRESSEE_ID = field(unquotedName("friends", "addressee_id"), UUID.class);
    public static final Field<String> FRIEND_STATUS = field(unquotedName("friends", "status"), String.class);
    public static final Field<Timestamp> FRIEND_CREATED_AT = field(unquotedName("friends", "created_at"), Timestamp.class);

    public static final Table<?> VERIFICATION_REQUESTS = table(unquotedName("verification_requests"));
    public static final Field<UUID> VERIFICATION_REQUEST_ID = field(unquotedName("verification_requests", "id"), UUID.class);
    public static final Field<UUID> VERIFICATION_REQUEST_COMPANY_ID = field(unquotedName("verification_requests", "company_id"), UUID.class);
    public static final Field<String> VERIFICATION_REQUEST_STATUS = field(unquotedName("verification_requests", "status"), String.class);
    public static final Field<String> VERIFICATION_REQUEST_REJECTION_REASON = field(unquotedName("verification_requests", "rejection_reason"), String.class);
    public static final Field<Timestamp> VERIFICATION_REQUEST_CREATED_AT = field(unquotedName("verification_requests", "created_at"), Timestamp.class);
    public static final Field<Timestamp> VERIFICATION_REQUEST_PROCESSED_AT = field(unquotedName("verification_requests", "processed_at"), Timestamp.class);
}
