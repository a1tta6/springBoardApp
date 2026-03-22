package org.isNotNull.springBoardApp.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Persisted user account used for authentication and dashboards.
 *
 * Example:
 * An applicant stores profile fields while an employer stores a company link.
 */
public final class UserEntity {

    private final UUID id;

    private final String email;

    private final String username;

    private final String displayName;

    private final String passwordHash;

    private final UserRole role;

    private final String fullName;

    private final String university;

    private final String course;

    private final String graduationYear;

    private final List<String> skills;

    private final List<String> portfolioLinks;

    private final String resume;

    private final List<String> contacts;

    private final boolean showApplications;

    private final boolean showResume;

    private final UUID companyId;

    private final boolean blocked;

    public UserEntity(
        final UUID id,
        final String email,
        final String username,
        final String displayName,
        final String passwordHash,
        final UserRole role,
        final String fullName,
        final String university,
        final String course,
        final String graduationYear,
        final List<String> skills,
        final List<String> portfolioLinks,
        final String resume,
        final List<String> contacts,
        final boolean showApplications,
        final boolean showResume,
        final UUID companyId,
        final boolean blocked
    ) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.role = role;
        this.fullName = fullName;
        this.university = university;
        this.course = course;
        this.graduationYear = graduationYear;
        this.skills = new ArrayList<>(skills);
        this.portfolioLinks = new ArrayList<>(portfolioLinks);
        this.resume = resume;
        this.contacts = new ArrayList<>(contacts);
        this.showApplications = showApplications;
        this.showResume = showResume;
        this.companyId = companyId;
        this.blocked = blocked;
    }

    public UserEntity(
        final String email,
        final String username,
        final String displayName,
        final String passwordHash,
        final UserRole role
    ) {
        this(
            null,
            email,
            username,
            displayName,
            passwordHash,
            role,
            null,
            null,
            null,
            null,
            List.of(),
            List.of(),
            null,
            List.of(),
            false,
            true,
            null,
            false
        );
    }

    public UUID id() { return this.id; }
    public String email() { return this.email; }
    public String username() { return this.username; }
    public String displayName() { return this.displayName; }
    public String passwordHash() { return this.passwordHash; }
    public UserRole role() { return this.role; }
    public String fullName() { return this.fullName; }
    public String university() { return this.university; }
    public String course() { return this.course; }
    public String graduationYear() { return this.graduationYear; }
    public List<String> skills() { return List.copyOf(this.skills); }
    public List<String> portfolioLinks() { return List.copyOf(this.portfolioLinks); }
    public String resume() { return this.resume; }
    public List<String> contacts() { return List.copyOf(this.contacts); }
    public boolean showApplications() { return this.showApplications; }
    public boolean showResume() { return this.showResume; }
    public UUID companyId() { return this.companyId; }
    public boolean blocked() { return this.blocked; }

    public UserEntity profile(
        final String displayName,
        final String name,
        final String school,
        final String education,
        final String year,
        final List<String> stack,
        final List<String> links,
        final String cv,
        final List<String> phonebook
    ) {
        return new UserEntity(
            this.id,
            this.email,
            this.username,
            displayName,
            this.passwordHash,
            this.role,
            name,
            school,
            education,
            year,
            stack,
            links,
            cv,
            phonebook,
            this.showApplications,
            this.showResume,
            this.companyId,
            this.blocked
        );
    }

    public UserEntity privacy(final boolean applications, final boolean resumeVisibility) {
        return new UserEntity(
            this.id,
            this.email,
            this.username,
            this.displayName,
            this.passwordHash,
            this.role,
            this.fullName,
            this.university,
            this.course,
            this.graduationYear,
            this.skills,
            this.portfolioLinks,
            this.resume,
            this.contacts,
            applications,
            resumeVisibility,
            this.companyId,
            this.blocked
        );
    }

    public UserEntity company(final UUID reference) {
        return new UserEntity(
            this.id,
            this.email,
            this.username,
            this.displayName,
            this.passwordHash,
            this.role,
            this.fullName,
            this.university,
            this.course,
            this.graduationYear,
            this.skills,
            this.portfolioLinks,
            this.resume,
            this.contacts,
            this.showApplications,
            this.showResume,
            reference,
            this.blocked
        );
    }

    public UserEntity block() {
        return new UserEntity(
            this.id,
            this.email,
            this.username,
            this.displayName,
            this.passwordHash,
            this.role,
            this.fullName,
            this.university,
            this.course,
            this.graduationYear,
            this.skills,
            this.portfolioLinks,
            this.resume,
            this.contacts,
            this.showApplications,
            this.showResume,
            this.companyId,
            true
        );
    }
}
