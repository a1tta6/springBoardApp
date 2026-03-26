package org.isNotNull.springBoardApp.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * View payloads sent to the React frontend.
 *
 * Example:
 * Controllers serialize records from this class directly to JSON.
 */
public final class ViewJson {

    private ViewJson() {
    }

    public record Social(String linkedin, String vk, String telegram) {
    }

    public record Privacy(boolean showApplications, boolean showResume) {
    }

    public record User(
        String id,
        String email,
        String username,
        String displayName,
        String role,
        String fullName,
        String university,
        String course,
        String graduationYear,
        List<String> skills,
        List<String> portfolioLinks,
        String resume,
        List<String> contacts,
        Privacy privacySettings,
        String companyId
    ) {
    }

    public record Company(
        String id,
        String name,
        String description,
        String industry,
        String website,
        Social socialLinks,
        boolean verified,
        String email
    ) {
    }

    public record Tag(String id, String name, String category) {
    }

    public record Location(String city, String address, List<Double> coordinates) {
    }

    public record Salary(Integer min, Integer max, String currency) {
    }

    public record Contact(String email, String phone, String website) {
    }

    public record Opportunity(
        String id,
        String title,
        String description,
        String type,
        String companyId,
        String workFormat,
        Location location,
        Salary salary,
        Instant publishedDate,
        LocalDate expiryDate,
        LocalDate eventDate,
        Contact contactInfo,
        List<String> tags,
        String status,
        String requirements,
        List<String> mediaContent
    ) {
    }

    public record Application(
        String id,
        String opportunityId,
        String applicantId,
        String status,
        Instant appliedDate,
        String message
    ) {
    }

    public record Dashboard(
        List<User> users,
        List<Company> companies,
        List<Opportunity> opportunities,
        List<Application> applications,
        List<Opportunity> favorites,
        List<Tag> tags
    ) {
    }

    public record ProfileUpdate(
        String displayName,
        String fullName,
        String university,
        String course,
        String graduationYear,
        List<String> skills,
        List<String> portfolioLinks,
        String resume,
        List<String> contacts
    ) {
    }

    public record PrivacyUpdate(boolean showApplications, boolean showResume) {
    }

    public record OpportunityCreate(
        String title,
        String description,
        String type,
        String workFormat,
        String city,
        String address,
        Double latitude,
        Double longitude,
        Integer salaryMin,
        Integer salaryMax,
        String currency,
        LocalDate expiryDate,
        LocalDate eventDate,
        String contactEmail,
        String contactPhone,
        String contactWebsite,
        List<String> tags,
        String requirements
    ) {
    }

    public record ApplicationCreate(String message) {
    }

    public record StatusUpdate(String status) {
    }

    public record Friend(
        String id,
        String userId,
        String email,
        String displayName,
        String fullName,
        String university,
        String status,
        Instant createdAt
    ) {
    }

    public record FriendStatus(String status, String friendId) {
    }

    public record UserProfile(
        User user,
        boolean isFriend,
        boolean showResume,
        boolean showApplications,
        List<Opportunity> favorites,
        List<Application> applications
    ) {
    }
}
