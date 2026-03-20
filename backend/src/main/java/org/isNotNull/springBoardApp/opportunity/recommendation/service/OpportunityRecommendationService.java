package org.isNotNull.springBoardApp.opportunity.recommendation.service;

import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.isNotNull.springBoardApp.auth.service.UserDetailsService;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.opportunity.enums.OpportunityFormat;
import org.isNotNull.springBoardApp.opportunity.recommendation.repository.OpportunityRecommendationRepository;
import org.isNotNull.springBoardApp.opportunity.service.OpportunityFacade;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.jooq.impl.DSL.trueCondition;
import static org.isNotNull.springBoardApp.tables.Opportunity.OPPORTUNITY;

@Service
@AllArgsConstructor
public class OpportunityRecommendationService {

    private final UserDetailsService userDetailsService;
    private final OpportunityRecommendationRepository opportunityRecommendationRepository;
    private final OpportunityFacade opportunityFacade;

    private Condition getCommonListCondition(String search, List<String> tags) {
        Condition condition = trueCondition();

        if (Objects.nonNull(search) && !search.trim().isEmpty()) {
            condition = condition.and(OPPORTUNITY.TITLE.containsIgnoreCase(search));
            condition = condition.or(OPPORTUNITY.SHORT_DESCRIPTION.containsIgnoreCase(search));
            condition = condition.or(OPPORTUNITY.LOCATION.containsIgnoreCase(search));

            Map<String, String> formatRuMap = Map.of(
                    "ONLINE", "\u041e\u043d\u043b\u0430\u0439\u043d",
                    "OFFLINE", "\u041e\u0444\u043b\u0430\u0439\u043d"
            );

            List<OpportunityFormat> matchingFormats = formatRuMap.entrySet().stream()
                    .filter(entry -> entry.getValue().toLowerCase().contains(search.toLowerCase()))
                    .map(entry -> OpportunityFormat.valueOf(entry.getKey()))
                    .toList();

            if (!matchingFormats.isEmpty()) {
                condition = condition.or(OPPORTUNITY.FORMAT.in(matchingFormats));
            }
        }

        if (Objects.nonNull(tags) && !tags.isEmpty()) {
            condition = condition.and(OPPORTUNITY.ID.in(opportunityRecommendationRepository.fetchOpportunityIdsBySelectedTags(tags)));
        }

        return condition;
    }

    public ResponseList<OpportunityDTO> listRecommendedOpportunities(
            Integer page,
            Integer pageSize,
            String search,
            List<String> tags,
            double lat,
            double lon
    ) {
        Long userId = userDetailsService.getAuthenticatedUser().getId();
        Condition condition = getCommonListCondition(search, tags);
        List<Opportunity> recommendations = opportunityRecommendationRepository.findRecommendedOpportunities(userId, lon, lat, page, pageSize, condition);

        ResponseList<OpportunityDTO> responseList = new ResponseList<>();
        List<OpportunityDTO> opportunityDTOList = new ArrayList<>();
        recommendations.forEach(opportunity -> opportunityDTOList.add(opportunityFacade.enrichOpportunity(opportunity)));

        responseList.setList(opportunityDTOList);
        responseList.setTotal(opportunityRecommendationRepository.countRecommendedOpportunities(condition));
        responseList.setCurrentPage(page);
        responseList.setPageSize(pageSize);
        return responseList;
    }
}
