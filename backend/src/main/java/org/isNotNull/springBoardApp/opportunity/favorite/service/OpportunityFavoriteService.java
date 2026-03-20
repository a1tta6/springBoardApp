package org.isNotNull.springBoardApp.opportunity.favorite.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.auth.service.UserDetailsService;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.opportunity.favorite.repository.OpportunityFavoriteRepository;
import org.isNotNull.springBoardApp.opportunity.service.OpportunityFacade;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.isNotNull.springBoardApp.tables.pojos.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class OpportunityFavoriteService {
    private final OpportunityFavoriteRepository opportunityFavoriteRepository;
    private final UserDetailsService userDetailsService;
    private final OpportunityFacade opportunityFacade;

    @Transactional
    public void addFavorite(Long opportunityId) {
        User user = userDetailsService.getAuthenticatedUser();
        if (Objects.isNull(user)) throw new AccessDeniedException("Authentication required");
        opportunityFavoriteRepository.insertIgnoreDuplicate(user.getId(), opportunityId);
    }

    @Transactional
    public void removeFavorite(Long opportunityId) {
        User user = userDetailsService.getAuthenticatedUser();
        if (Objects.isNull(user)) throw new AccessDeniedException("Authentication required");
        opportunityFavoriteRepository.delete(user.getId(), opportunityId);
    }

    public ResponseList<OpportunityDTO> listFavorites(Integer page, Integer pageSize) {
        User user = userDetailsService.getAuthenticatedUser();
        if (Objects.isNull(user)) throw new AccessDeniedException("Authentication required");

        List<Opportunity> opportunities = opportunityFavoriteRepository.listFavorites(user.getId(), page, pageSize);
        List<OpportunityDTO> dtoList = new ArrayList<>();
        opportunities.forEach(opportunity -> dtoList.add(opportunityFacade.enrichOpportunity(opportunity)));

        ResponseList<OpportunityDTO> response = new ResponseList<>();
        response.setList(dtoList);
        response.setTotal(opportunityFavoriteRepository.countFavorites(user.getId()));
        response.setCurrentPage(page);
        response.setPageSize(pageSize);
        return response;
    }
}
