package org.isNotNull.springBoardApp.domain;

import java.util.UUID;

/**
 * Company profile displayed in employer and curator dashboards.
 *
 * Example:
 * A company becomes verified after curator approval.
 */
public final class CompanyEntity {

    private final UUID id;
    private final String name;
    private final String description;
    private final String industry;
    private final String website;
    private final String linkedin;
    private final String vk;
    private final String telegram;
    private final boolean verified;
    private final String email;

    public CompanyEntity(
        final UUID id,
        final String name,
        final String description,
        final String industry,
        final String website,
        final String linkedin,
        final String vk,
        final String telegram,
        final boolean verified,
        final String email
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.website = website;
        this.linkedin = linkedin;
        this.vk = vk;
        this.telegram = telegram;
        this.verified = verified;
        this.email = email;
    }

    public CompanyEntity(
        final String name,
        final String description,
        final String industry,
        final String website,
        final String linkedin,
        final String vk,
        final String telegram,
        final boolean verified,
        final String email
    ) {
        this(null, name, description, industry, website, linkedin, vk, telegram, verified, email);
    }

    public UUID id() { return this.id; }
    public String name() { return this.name; }
    public String description() { return this.description; }
    public String industry() { return this.industry; }
    public String website() { return this.website; }
    public String linkedin() { return this.linkedin; }
    public String vk() { return this.vk; }
    public String telegram() { return this.telegram; }
    public boolean verified() { return this.verified; }
    public String email() { return this.email; }

    public CompanyEntity verify() {
        return new CompanyEntity(
            this.id,
            this.name,
            this.description,
            this.industry,
            this.website,
            this.linkedin,
            this.vk,
            this.telegram,
            true,
            this.email
        );
    }
}
