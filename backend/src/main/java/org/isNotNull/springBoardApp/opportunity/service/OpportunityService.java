package org.isNotNull.springBoardApp.opportunity.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OpportunityService {
    private final OpportunityFacade opportunityFacade;

    public OpportunityDTO createOpportunity(OpportunityDTO opportunityDTO) {
        Long id = opportunityFacade.createOpportunity(opportunityDTO);
        return getOpportunity(id);
    }

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
        return opportunityFacade.listOpportunities(page, pageSize, search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly);
    }

    public OpportunityDTO getOpportunity(Long id) {
        return opportunityFacade.getOpportunity(id);
    }

    public OpportunityDTO updateOpportunity(Long id, OpportunityDTO opportunityDTO) {
        opportunityFacade.updateOpportunity(id, opportunityDTO);
        return getOpportunity(id);
    }

    public Long deleteOpportunity(Long id) {
        return opportunityFacade.deleteOpportunity(id);
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
        return opportunityFacade.listEmployerOpportunities(page, pageSize, search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly, employerId);
    }

    @Deprecated(forRemoval = false)
    public OpportunityDTO create(OpportunityDTO opportunityDTO) {
        return createOpportunity(opportunityDTO);
    }

    @Deprecated(forRemoval = false)
    public ResponseList<OpportunityDTO> getList(
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
        return listOpportunities(page, pageSize, search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly);
    }

    @Deprecated(forRemoval = false)
    public OpportunityDTO get(Long id) {
        return getOpportunity(id);
    }

    @Deprecated(forRemoval = false)
    public OpportunityDTO update(Long id, OpportunityDTO opportunityDTO) {
        return updateOpportunity(id, opportunityDTO);
    }

    @Deprecated(forRemoval = false)
    public Long delete(Long id) {
        return deleteOpportunity(id);
    }

    @Deprecated(forRemoval = false)
    public ResponseList<OpportunityDTO> getListByEmployer(
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
        return listEmployerOpportunities(page, pageSize, search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly, employerId);
    }
}
