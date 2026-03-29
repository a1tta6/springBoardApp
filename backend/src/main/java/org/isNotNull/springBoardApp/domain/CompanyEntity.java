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
    private final String inn;
    private final String ogrn;
    private final String address;
    private final String website;
    private final String logo;
    private final String socialLinks;
    private final boolean verified;
    private final String email;
    private final String bio;

    public CompanyEntity(
        final UUID id,
        final String name,
        final String inn,
        final String ogrn,
        final String address,
        final String website,
        final String logo,
        final String socialLinks,
        final String bio,
        final boolean verified,
        final String email
    ) {
        this.id = id;
        this.name = name;
        this.inn = inn;
        this.ogrn = ogrn;
        this.address = address;
        this.website = website;
        this.logo = logo;
        this.socialLinks = socialLinks;
        this.bio = bio;
        this.verified = verified;
        this.email = email;
    }

    public CompanyEntity(
        final String name,
        final String inn,
        final String ogrn,
        final String address,
        final String website,
        final String logo,
        final String socialLinks,
        final String bio,
        final boolean verified,
        final String email
    ) {
        this(null, name, inn, ogrn, address, website, logo, socialLinks, bio, verified, email);
    }

    public UUID id() { return this.id; }
    public String name() { return this.name; }
    public String inn() { return this.inn; }
    public String ogrn() { return this.ogrn; }
    public String address() { return this.address; }
    public String website() { return this.website; }
    public String logo() { return this.logo; }
    public String socialLinks() { return this.socialLinks; }
    public boolean verified() { return this.verified; }
    public String email() { return this.email; }
    public String bio() { return this.bio; }

    public CompanyEntity verify() {
        return new CompanyEntity(
            this.id,
            this.name,
            this.inn,
            this.ogrn,
            this.address,
            this.website,
            this.logo,
            this.socialLinks,
            this.bio,
            true,
            this.email
        );
    }
}
