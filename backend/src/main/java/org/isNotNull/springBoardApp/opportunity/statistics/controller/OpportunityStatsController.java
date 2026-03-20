package org.isNotNull.springBoardApp.opportunity.statistics.controller;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.opportunity.statistics.service.OpportunityStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/stats")
public class OpportunityStatsController {
    private final OpportunityStatsService statsService;

    @GetMapping({"/event/{eventId}/views", "/opportunities/{opportunityId}/views"})
    public int getOpportunityViews(
            @PathVariable(value = "eventId", required = false) Long eventId,
            @PathVariable(value = "opportunityId", required = false) Long opportunityId
    ) {
        return statsService.getOpportunityViews(opportunityId != null ? opportunityId : eventId);
    }

    @GetMapping("/user/{userId}/views")
    public int getUserViews(@PathVariable Long userId) {
        return statsService.getUserViews(userId);
    }

    @GetMapping({"/event/{eventId}/user/{userId}/views", "/opportunities/{opportunityId}/user/{userId}/views"})
    public int getUserViewsForOpportunity(
            @PathVariable(value = "userId") Long userId,
            @PathVariable(value = "eventId", required = false) Long eventId,
            @PathVariable(value = "opportunityId", required = false) Long opportunityId
    ) {
        return statsService.getUserViewsForOpportunity(userId, opportunityId != null ? opportunityId : eventId);
    }

    @GetMapping({"/organizers/{organizerId}/views", "/employers/{employerId}/views"})
    public Long getEmployerViews(
            @PathVariable(value = "organizerId", required = false) Long organizerId,
            @PathVariable(value = "employerId", required = false) Long employerId
    ) {
        return statsService.getEmployerViews(employerId != null ? employerId : organizerId);
    }

    @GetMapping({"/organizers/{organizerId}/favorites", "/employers/{employerId}/favorites"})
    public Long getEmployerFavorites(
            @PathVariable(value = "organizerId", required = false) Long organizerId,
            @PathVariable(value = "employerId", required = false) Long employerId
    ) {
        return statsService.getEmployerFavorites(employerId != null ? employerId : organizerId);
    }

    @GetMapping({"/organizers/{organizerId}/members", "/employers/{employerId}/candidates"})
    public Long getEmployerCandidates(
            @PathVariable(value = "organizerId", required = false) Long organizerId,
            @PathVariable(value = "employerId", required = false) Long employerId
    ) {
        return statsService.getEmployerCandidates(employerId != null ? employerId : organizerId);
    }

    @GetMapping({"/organizers/{organizerId}/events", "/employers/{employerId}/opportunities"})
    public Long getEmployerOpportunities(
            @PathVariable(value = "organizerId", required = false) Long organizerId,
            @PathVariable(value = "employerId", required = false) Long employerId
    ) {
        return statsService.getEmployerOpportunities(employerId != null ? employerId : organizerId);
    }
}
