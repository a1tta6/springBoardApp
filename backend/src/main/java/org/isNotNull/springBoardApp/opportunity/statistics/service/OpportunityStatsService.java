package org.isNotNull.springBoardApp.opportunity.statistics.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.opportunity.statistics.repository.OpportunityInteractionRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OpportunityStatsService {

    private final OpportunityInteractionRepository repository;

    public int getOpportunityViews(Long opportunityId) {
        return repository.countViewsByOpportunityId(opportunityId);
    }

    public int getUserViews(Long userId) {
        return repository.countViewsByUserId(userId);
    }

    public int getUserViewsForOpportunity(Long userId, Long opportunityId) {
        return repository.countViewsByUserAndOpportunity(userId, opportunityId);
    }

    public Long getEmployerViews(Long employerId) {
        return repository.countEmployerViews(employerId);
    }

    public Long getEmployerFavorites(Long employerId) {
        return repository.countEmployerFavorites(employerId);
    }

    public Long getEmployerCandidates(Long employerId) {
        return repository.countEmployerCandidates(employerId);
    }

    public Long getEmployerOpportunities(Long employerId) {
        return repository.countEmployerOpportunities(employerId);
    }
}
