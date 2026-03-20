package org.isNotNull.springBoardApp.opportunity.recommendation.controller;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.opportunity.recommendation.service.OpportunityRecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping
public class OpportunityRecommendationController {

    private final OpportunityRecommendationService opportunityRecommendationService;

    @GetMapping({"/v1/recommend", "/v1/opportunities/recommendations"})
    public ResponseList<OpportunityDTO> listRecommendations(
            @RequestParam(value = "lat") double latitude,
            @RequestParam(value = "lon") double longitude,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "tags", required = false) List<String> tags
    ) {
        return opportunityRecommendationService.listRecommendedOpportunities(page, pageSize, search, tags, latitude, longitude);
    }
}
