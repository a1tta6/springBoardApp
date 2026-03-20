package org.isNotNull.springBoardApp.opportunity.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OpportunityFacade {

    private final OpportunityQueryService opportunityQueryService;
    private final OpportunityCommandService opportunityCommandService;

    public ResponseList<OpportunityDTO> listOpportunities(
            Integer page,
            Integer pageSize,
            String search,
            List<String> tags,
            List<String> opportunityTypes,
            List<String> workModes,
            Integer salaryMin,
            Integer salaryMax,
            boolean activeOnly
    ) {
        return opportunityQueryService.listOpportunities(page, pageSize, search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly);
    }

    public ResponseList<OpportunityDTO> listEmployerOpportunities(
            Integer page,
            Integer pageSize,
            String search,
            List<String> tags,
            List<String> opportunityTypes,
            List<String> workModes,
            Integer salaryMin,
            Integer salaryMax,
            boolean activeOnly,
            Long employerId
    ) {
        return opportunityQueryService.listEmployerOpportunities(page, pageSize, search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly, employerId);
    }

    public ResponseList<OpportunityDTO> listMemberOpportunities(
            Integer page,
            Integer pageSize,
            String search,
            List<String> tags,
            Long memberId
    ) {
        return opportunityQueryService.listMemberOpportunities(page, pageSize, search, tags, memberId);
    }

    public OpportunityDTO getOpportunity(Long id) {
        return opportunityQueryService.getOpportunity(id);
    }

    public Long createOpportunity(OpportunityDTO opportunityDTO) {
        return opportunityCommandService.createOpportunity(opportunityDTO);
    }

    public void updateOpportunity(Long id, OpportunityDTO opportunityDTO) {
        opportunityCommandService.updateOpportunity(id, opportunityDTO);
    }

    public Long deleteOpportunity(Long opportunityId) {
        return opportunityCommandService.deleteOpportunity(opportunityId);
    }

    public OpportunityDTO enrichOpportunity(Opportunity opportunity) {
        return opportunityQueryService.enrichOpportunity(opportunity);
    }

    public OpportunityDTO enrichOpportunity(Opportunity opportunity, boolean includeFiles) {
        return opportunityQueryService.enrichOpportunity(opportunity, includeFiles);
    }
}
