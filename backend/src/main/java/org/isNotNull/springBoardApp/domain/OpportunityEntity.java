package org.isNotNull.springBoardApp.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Opportunity published by an employer and moderated by a curator.
 *
 * Example:
 * A vacancy may have salary values while an event may have an event date.
 */
public final class OpportunityEntity {

    private final UUID id;
    private final String title;
    private final String description;
    private final OpportunityType type;
    private final UUID companyId;
    private final WorkFormat workFormat;
    private final String city;
    private final String address;
    private final double latitude;
    private final double longitude;
    private final Integer salaryMin;
    private final Integer salaryMax;
    private final String currency;
    private final Instant publishedDate;
    private final LocalDate expiryDate;
    private final LocalDate eventDate;
    private final String contactEmail;
    private final String contactPhone;
    private final String contactWebsite;
    private final List<UUID> tags;
    private final OpportunityStatus status;
    private final String requirements;
    private final List<String> mediaContent;

    public OpportunityEntity(
        final UUID id,
        final String title,
        final String description,
        final OpportunityType type,
        final UUID companyId,
        final WorkFormat workFormat,
        final String city,
        final String address,
        final double latitude,
        final double longitude,
        final Integer salaryMin,
        final Integer salaryMax,
        final String currency,
        final Instant publishedDate,
        final LocalDate expiryDate,
        final LocalDate eventDate,
        final String contactEmail,
        final String contactPhone,
        final String contactWebsite,
        final List<UUID> tags,
        final OpportunityStatus status,
        final String requirements,
        final List<String> mediaContent
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.companyId = companyId;
        this.workFormat = workFormat;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.currency = currency;
        this.publishedDate = publishedDate;
        this.expiryDate = expiryDate;
        this.eventDate = eventDate;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.contactWebsite = contactWebsite;
        this.tags = List.copyOf(tags);
        this.status = status;
        this.requirements = requirements;
        this.mediaContent = List.copyOf(mediaContent);
    }

    public OpportunityEntity(
        final String title,
        final String description,
        final OpportunityType type,
        final UUID companyId,
        final WorkFormat workFormat,
        final String city,
        final String address,
        final double latitude,
        final double longitude,
        final Integer salaryMin,
        final Integer salaryMax,
        final String currency,
        final Instant publishedDate,
        final LocalDate expiryDate,
        final LocalDate eventDate,
        final String contactEmail,
        final String contactPhone,
        final String contactWebsite,
        final List<UUID> tags,
        final OpportunityStatus status,
        final String requirements,
        final List<String> mediaContent
    ) {
        this(
            null,
            title,
            description,
            type,
            companyId,
            workFormat,
            city,
            address,
            latitude,
            longitude,
            salaryMin,
            salaryMax,
            currency,
            publishedDate,
            expiryDate,
            eventDate,
            contactEmail,
            contactPhone,
            contactWebsite,
            tags,
            status,
            requirements,
            mediaContent
        );
    }

    public UUID id() { return this.id; }
    public String title() { return this.title; }
    public String description() { return this.description; }
    public OpportunityType type() { return this.type; }
    public UUID companyId() { return this.companyId; }
    public WorkFormat workFormat() { return this.workFormat; }
    public String city() { return this.city; }
    public String address() { return this.address; }
    public double latitude() { return this.latitude; }
    public double longitude() { return this.longitude; }
    public Integer salaryMin() { return this.salaryMin; }
    public Integer salaryMax() { return this.salaryMax; }
    public String currency() { return this.currency; }
    public Instant publishedDate() { return this.publishedDate; }
    public LocalDate expiryDate() { return this.expiryDate; }
    public LocalDate eventDate() { return this.eventDate; }
    public String contactEmail() { return this.contactEmail; }
    public String contactPhone() { return this.contactPhone; }
    public String contactWebsite() { return this.contactWebsite; }
    public List<UUID> tags() { return this.tags; }
    public OpportunityStatus status() { return this.status; }
    public String requirements() { return this.requirements; }
    public List<String> mediaContent() { return this.mediaContent; }

    public OpportunityEntity status(final OpportunityStatus state) {
        return new OpportunityEntity(
            this.id,
            this.title,
            this.description,
            this.type,
            this.companyId,
            this.workFormat,
            this.city,
            this.address,
            this.latitude,
            this.longitude,
            this.salaryMin,
            this.salaryMax,
            this.currency,
            this.publishedDate,
            this.expiryDate,
            this.eventDate,
            this.contactEmail,
            this.contactPhone,
            this.contactWebsite,
            this.tags,
            state,
            this.requirements,
            this.mediaContent
        );
    }
}
