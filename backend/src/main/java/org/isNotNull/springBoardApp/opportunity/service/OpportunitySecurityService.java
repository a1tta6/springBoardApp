package org.isNotNull.springBoardApp.opportunity.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.opportunity.exception.OpportunityNotFoundException;
import org.isNotNull.springBoardApp.opportunity.repository.OpportunityRepository;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.isNotNull.springBoardApp.tables.pojos.User;
import org.isNotNull.springBoardApp.user.enums.RoleEnum;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class OpportunitySecurityService {
    private OpportunityRepository opportunityRepository;

    public boolean isUserOwnOpportunity(Long opportunityId, User authenticatedUser) {
        Opportunity opportunity = opportunityRepository.fetchById(opportunityId);

        if (Objects.isNull(opportunity)) {
            throw new OpportunityNotFoundException(opportunityId);
        }

        return authenticatedUser.getId().equals(opportunity.getOrganizerId()) &&
                RoleEnum.ORGANIZER.name().equals(authenticatedUser.getRole().getLiteral());
    }
}
