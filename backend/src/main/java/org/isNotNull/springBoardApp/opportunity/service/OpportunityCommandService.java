package org.isNotNull.springBoardApp.opportunity.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.auth.service.UserDetailsService;
import org.isNotNull.springBoardApp.common.exception.AlreadyExistsException;
import org.isNotNull.springBoardApp.common.exception.MissingFieldException;
import org.isNotNull.springBoardApp.enums.FormatType;
import org.isNotNull.springBoardApp.enums.OpportunityType;
import org.isNotNull.springBoardApp.enums.OpportunityVisibilityStatusType;
import org.isNotNull.springBoardApp.enums.RoleType;
import org.isNotNull.springBoardApp.enums.WorkModeType;
import org.isNotNull.springBoardApp.opportunity.adapter.OpportunityEventAdapter;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.opportunity.enums.OpportunityFormat;
import org.isNotNull.springBoardApp.opportunity.exception.OpportunityNotFoundException;
import org.isNotNull.springBoardApp.tables.daos.OrganizerDao;
import org.isNotNull.springBoardApp.tables.daos.OpportunityDao;
import org.isNotNull.springBoardApp.tables.daos.OpportunityFileDao;
import org.isNotNull.springBoardApp.tables.pojos.Organizer;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.isNotNull.springBoardApp.tables.pojos.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@AllArgsConstructor
public class OpportunityCommandService {

    private final OpportunityDao opportunityDao;
    private final OpportunityFileDao opportunityFileDao;
    private final OpportunitySecurityService opportunitySecurityService;
    private final UserDetailsService userDetailsService;
    private final OrganizerDao organizerDao;
    private final LocationService locationService;
    private final OpportunityEventAdapter opportunityEventAdapter;

    @Transactional
    public Long createOpportunity(OpportunityDTO opportunityDTO) {
        User authenticatedUser = requireAuthenticatedUser();
        validateOpportunityPayload(opportunityDTO);

        if (!isModerator(authenticatedUser)) {
            ensureOrganizerOwnsTarget(authenticatedUser, opportunityDTO.getOrganizerId());
            ensureOrganizerAccredited(opportunityDTO.getOrganizerId());
            OpportunityVisibilityStatusType requestedVisibility = resolveVisibilityStatus(opportunityDTO.getVisibilityStatus());
            if (Objects.nonNull(requestedVisibility) && !OpportunityVisibilityStatusType.DRAFT.equals(requestedVisibility)) {
                throw new AccessDeniedException("Employer can create opportunity only as draft");
            }
        }

        fillLocationOrCoordinates(opportunityDTO);

        Opportunity opportunity = opportunityEventAdapter.toOpportunity(opportunityDTO);
        if (!isModerator(authenticatedUser)) {
            opportunity.setVisibilityStatus(OpportunityVisibilityStatusType.DRAFT);
        } else if (Objects.nonNull(resolveVisibilityStatus(opportunityDTO.getVisibilityStatus()))) {
            opportunity.setVisibilityStatus(resolveVisibilityStatus(opportunityDTO.getVisibilityStatus()));
        }
        if (!opportunityDao.fetchByTitle(opportunityDTO.getTitle()).isEmpty()) {
            throw new AlreadyExistsException(String.format("Opportunity with title %s", opportunityDTO.getTitle()));
        }
        opportunityDao.insert(opportunity);

        if (Objects.nonNull(opportunityDTO.getFiles())) {
            opportunityDTO.getFiles().forEach(file -> file.setOpportunityId(opportunity.getId()));
            opportunityFileDao.insert(opportunityDTO.getFiles().stream()
                    .map(opportunityEventAdapter::toOpportunityFile)
                    .toList());
        }
        return opportunity.getId();
    }

    @Transactional
    public void updateOpportunity(Long id, OpportunityDTO opportunityDTO) {
        Opportunity opportunity = opportunityDao.findOptionalById(id).orElseThrow(() -> new OpportunityNotFoundException(id));
        User authenticatedUser = requireAuthenticatedUser();

        if (!isModerator(authenticatedUser)) {
            ensureOrganizerOwnsTarget(authenticatedUser, opportunity.getOrganizerId());
            ensureOrganizerAccredited(opportunity.getOrganizerId());
        }

        if (Objects.nonNull(opportunityDTO.getDescription()))
            opportunity.setDescription(opportunityDTO.getDescription());
        if (Objects.nonNull(opportunityDTO.getShortDescription()))
            opportunity.setShortDescription(opportunityDTO.getShortDescription());
        if (Objects.nonNull(opportunityDTO.getTitle()))
            opportunity.setTitle(opportunityDTO.getTitle());
        if (Objects.nonNull(opportunityDTO.getFormat()))
            opportunity.setFormat(FormatType.valueOf(String.valueOf(opportunityDTO.getFormat())));
        if (Objects.nonNull(opportunityDTO.getStartDateTime()))
            opportunity.setStartDateTime(opportunityDTO.getStartDateTime());
        if (Objects.nonNull(opportunityDTO.getEndDateTime()))
            opportunity.setEndDateTime(opportunityDTO.getEndDateTime());
        if (Objects.nonNull(opportunityDTO.getLocation()))
            opportunity.setLocation(opportunityDTO.getLocation());
        if (Objects.nonNull(opportunityDTO.getLatitude()))
            opportunity.setLatitude(opportunityDTO.getLatitude());
        if (Objects.nonNull(opportunityDTO.getLongitude()))
            opportunity.setLongitude(opportunityDTO.getLongitude());
        if (Objects.nonNull(opportunityDTO.getOpportunityType()))
            opportunity.setOpportunityType(OpportunityType.valueOf(opportunityDTO.getOpportunityType().trim().toUpperCase()));
        if (Objects.nonNull(opportunityDTO.getWorkMode()))
            opportunity.setWorkMode(WorkModeType.valueOf(opportunityDTO.getWorkMode().trim().toUpperCase()));
        if (Objects.nonNull(opportunityDTO.getSalaryMin()))
            opportunity.setSalaryMin(opportunityDTO.getSalaryMin());
        if (Objects.nonNull(opportunityDTO.getSalaryMax()))
            opportunity.setSalaryMax(opportunityDTO.getSalaryMax());
        if (Objects.nonNull(opportunityDTO.getPublishedAt()))
            opportunity.setPublishedAt(opportunityDTO.getPublishedAt());
        if (Objects.nonNull(opportunityDTO.getExpiresAt()))
            opportunity.setExpiresAt(opportunityDTO.getExpiresAt());
        if (Objects.nonNull(opportunityDTO.getContacts()))
            opportunity.setContacts(opportunityDTO.getContacts());
        if (Objects.nonNull(opportunityDTO.getResources()))
            opportunity.setResources(opportunityDTO.getResources());

        OpportunityVisibilityStatusType requestedVisibility = resolveVisibilityStatus(opportunityDTO.getVisibilityStatus());
        if (Objects.nonNull(requestedVisibility)) {
            if (!isModerator(authenticatedUser) && !OpportunityVisibilityStatusType.DRAFT.equals(requestedVisibility)) {
                throw new AccessDeniedException("Employer can save opportunity only as draft");
            }
            opportunity.setVisibilityStatus(requestedVisibility);
        }

        if (Objects.nonNull(opportunityDTO.getPictures()))
            opportunity.setPictures(opportunityDTO.getPictures());

        opportunity.setId(id);
        opportunityDao.update(opportunity);
    }

    @Transactional
    public Long deleteOpportunity(Long opportunityId) {
        User authenticatedUser = requireAuthenticatedUser();
        if (isModerator(authenticatedUser) || opportunitySecurityService.isUserOwnOpportunity(opportunityId, authenticatedUser)) {
            opportunityDao.findOptionalById(opportunityId).orElseThrow(() -> new OpportunityNotFoundException(opportunityId));
            opportunityDao.deleteById(opportunityId);
        }
        return opportunityId;
    }

    private User requireAuthenticatedUser() {
        User authenticatedUser = userDetailsService.getAuthenticatedUser();
        if (Objects.isNull(authenticatedUser)) {
            throw new AccessDeniedException("Authentication required");
        }
        return authenticatedUser;
    }

    private boolean isModerator(User user) {
        return RoleType.MODERATOR.equals(user.getRole());
    }

    private OpportunityVisibilityStatusType resolveVisibilityStatus(String visibilityStatus) {
        if (Objects.isNull(visibilityStatus) || visibilityStatus.isBlank()) {
            return null;
        }
        return OpportunityVisibilityStatusType.valueOf(visibilityStatus.trim().toUpperCase());
    }

    private void ensureOrganizerOwnsTarget(User user, Long organizerId) {
        if (!RoleType.ORGANIZER.equals(user.getRole()) || !user.getId().equals(organizerId)) {
            throw new AccessDeniedException("Opportunity can be managed only by its employer account");
        }
    }

    private void ensureOrganizerAccredited(Long organizerId) {
        Organizer organizer = organizerDao.fetchOptionalById(organizerId)
                .orElseThrow(() -> new AccessDeniedException("Employer profile not found"));

        if (!Boolean.TRUE.equals(organizer.getIsAccredited())) {
            throw new AccessDeniedException("Only verified employers can publish opportunities");
        }
    }

    private void validateOpportunityPayload(OpportunityDTO opportunityDTO) {
        if (Objects.isNull(opportunityDTO.getTitle()) || opportunityDTO.getTitle().isEmpty())
            throw new MissingFieldException("title");
        if (Objects.isNull(opportunityDTO.getFormat()))
            throw new MissingFieldException("format");
        if (Objects.isNull(opportunityDTO.getShortDescription()))
            throw new MissingFieldException("shortDescription");
        if (Objects.isNull(opportunityDTO.getStartDateTime()))
            throw new MissingFieldException("startDateTime");
        if (Objects.isNull(opportunityDTO.getEndDateTime()))
            throw new MissingFieldException("endDateTime");
        if (Objects.isNull(opportunityDTO.getOrganizerId()))
            throw new MissingFieldException("organizerId");
        if (opportunityDTO.getStartDateTime().isAfter(opportunityDTO.getEndDateTime()))
            throw new IllegalArgumentException("startDateTime > endDateTime");
    }

    private void fillLocationOrCoordinates(OpportunityDTO opportunityDTO) {
        if ((Objects.isNull(opportunityDTO.getLocation()) || opportunityDTO.getLocation().isEmpty())
                && Objects.nonNull(opportunityDTO.getLatitude()) && Objects.nonNull(opportunityDTO.getLongitude())) {
            opportunityDTO.setLocation(locationService.getAddress(
                    opportunityDTO.getLatitude(),
                    opportunityDTO.getLongitude(),
                    Objects.equals(opportunityDTO.getFormat(), OpportunityFormat.ONLINE)
            ));
        }

        if (Objects.nonNull(opportunityDTO.getLocation()) && !opportunityDTO.getLocation().isEmpty()
                && Objects.isNull(opportunityDTO.getLatitude()) && Objects.isNull(opportunityDTO.getLongitude())) {
            var coordinates = locationService.getCoordinates(opportunityDTO.getLocation(), opportunityDTO.getFormat() == OpportunityFormat.ONLINE);
            opportunityDTO.setLatitude(coordinates.getLatitude());
            opportunityDTO.setLongitude(coordinates.getLongitude());
        }
    }
}
