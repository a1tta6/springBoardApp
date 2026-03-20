package org.isNotNull.springBoardApp.opportunity.response.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.auth.service.UserDetailsService;
import org.isNotNull.springBoardApp.opportunity.service.OpportunitySecurityService;
import org.isNotNull.springBoardApp.opportunity.response.dto.OpportunityResponseDTO;
import org.isNotNull.springBoardApp.opportunity.response.enums.OpportunityResponseStatusEnum;
import org.isNotNull.springBoardApp.opportunity.response.repository.OpportunityResponseRepository;
import org.isNotNull.springBoardApp.tables.daos.MemberDao;
import org.isNotNull.springBoardApp.tables.pojos.Member;
import org.isNotNull.springBoardApp.user.enums.RoleEnum;
import org.isNotNull.springBoardApp.tables.pojos.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class OpportunityResponseService {
    private final OpportunityResponseRepository opportunityResponseRepository;
    private final UserDetailsService userDetailsService;
    private final OpportunitySecurityService opportunitySecurityService;
    private final MemberDao memberDao;

    private User requireAuthenticatedUser() {
        User user = userDetailsService.getAuthenticatedUser();
        if (Objects.isNull(user)) {
            throw new AccessDeniedException("Authentication required");
        }
        return user;
    }

    private Member requireAuthenticatedMember(User user) {
        if (!RoleEnum.MEMBER.name().equals(user.getRole().getLiteral())) {
            throw new AccessDeniedException("Only applicants can apply");
        }

        return memberDao.fetchOptionalById(user.getId())
                .orElseThrow(() -> new AccessDeniedException("Applicant profile not found"));
    }

    @Transactional
    public void apply(Long opportunityId) {
        User user = requireAuthenticatedUser();
        Member member = requireAuthenticatedMember(user);
        opportunityResponseRepository.insertIgnoreDuplicate(opportunityId, member.getId());
    }

    public List<OpportunityResponseDTO> listForOpportunity(Long opportunityId) {
        User user = requireAuthenticatedUser();

        boolean isOrganizerOwner = opportunitySecurityService.isUserOwnOpportunity(opportunityId, user);
        boolean isCurator = RoleEnum.MODERATOR.name().equals(user.getRole().getLiteral());

        if (!isOrganizerOwner && !isCurator) {
            throw new AccessDeniedException("Not allowed");
        }

        return opportunityResponseRepository.fetchByOpportunityId(opportunityId);
    }

    public List<OpportunityResponseDTO> listMine() {
        User user = requireAuthenticatedUser();
        Member member = requireAuthenticatedMember(user);
        return opportunityResponseRepository.fetchByApplicantId(member.getId());
    }

    @Transactional
    public void updateStatus(Long opportunityId, Long applicantId, String status) {
        User user = requireAuthenticatedUser();

        boolean isOrganizerOwner = opportunitySecurityService.isUserOwnOpportunity(opportunityId, user);
        boolean isCurator = RoleEnum.MODERATOR.name().equals(user.getRole().getLiteral());

        if (!isOrganizerOwner && !isCurator) {
            throw new AccessDeniedException("Not allowed");
        }

        OpportunityResponseStatusEnum parsedStatus = OpportunityResponseStatusEnum.valueOf(status.trim().toUpperCase());
        opportunityResponseRepository.updateStatus(opportunityId, applicantId, parsedStatus.name(), LocalDateTime.now());
    }
}
