package org.isNotNull.springBoardApp.subscribe.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.common.dto.ResponseDTO;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.subscribe.dto.OpportunitySubscriptionDTO;
import org.isNotNull.springBoardApp.subscribe.service.SubscribeService;
import org.isNotNull.springBoardApp.tables.pojos.MemberOrganizer;
import org.isNotNull.springBoardApp.tables.pojos.Organizer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/v1/members/{memberId}")
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "\u0423\u0447\u0430\u0441\u0442\u0438\u0435 \u0432 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044f\u0445",
        description = "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0443\u0447\u0430\u0441\u0442\u0438\u0435\u043c \u0432 \u0432\u0430\u043a\u0430\u043d\u0441\u0438\u044f\u0445, \u0441\u0442\u0430\u0436\u0438\u0440\u043e\u0432\u043a\u0430\u0445 \u0438 \u043a\u0430\u0440\u044c\u0435\u0440\u043d\u044b\u0445 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f\u0445"
)
public class SubscribeController {

    private final SubscribeService subscribeService;

    @Operation(
            summary = "\u041e\u0442\u043a\u043b\u0438\u043a\u043d\u0443\u0442\u044c\u0441\u044f \u043d\u0430 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c.",
            description = "\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0432 \u0438\u0441\u0442\u043e\u0440\u0438\u044e \u0443\u0447\u0430\u0441\u0442\u0438\u044f \u0432\u0430\u043a\u0430\u043d\u0441\u0438\u044e, \u0441\u0442\u0430\u0436\u0438\u0440\u043e\u0432\u043a\u0443 \u0438\u043b\u0438 \u043a\u0430\u0440\u044c\u0435\u0440\u043d\u043e\u0435 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u0435."
    )
    @ApiResponse(responseCode = "201", description = "\u041e\u0442\u043a\u043b\u0438\u043a \u0438\u043b\u0438 \u0443\u0447\u0430\u0441\u0442\u0438\u0435 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u044b", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u0438", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = {"/subscribe/{eventId}", "/subscriptions/opportunities/{opportunityId}"})
    public void subscribeToOpportunity(
            @PathVariable(value = "eventId", required = false) Long eventId,
            @PathVariable(value = "opportunityId", required = false) Long opportunityId,
            @PathVariable Long memberId
    ) {
        subscribeService.subscribeToOpportunity(opportunityId != null ? opportunityId : eventId, memberId);
    }

    @Operation(
            summary = "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0443\u0447\u0430\u0441\u0442\u0438\u0435 \u0432 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438.",
            description = "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0432\u0430\u043a\u0430\u043d\u0441\u0438\u044e, \u0441\u0442\u0430\u0436\u0438\u0440\u043e\u0432\u043a\u0443 \u0438\u043b\u0438 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u0435 \u0438\u0437 \u0438\u0441\u0442\u043e\u0440\u0438\u0438 \u0443\u0447\u0430\u0441\u0442\u0438\u044f."
    )
    @ApiResponse(responseCode = "201", description = "\u0423\u0447\u0430\u0441\u0442\u0438\u0435 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0434\u0430\u043b\u0435\u043d\u043e", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u0438", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = {"/subscribe/{eventId}", "/subscriptions/opportunities/{opportunityId}"})
    public void unsubscribeFromOpportunity(
            @PathVariable(value = "eventId", required = false) Long eventId,
            @PathVariable(value = "opportunityId", required = false) Long opportunityId,
            @PathVariable Long memberId
    ) {
        subscribeService.unsubscribeFromOpportunity(opportunityId != null ? opportunityId : eventId, memberId);
    }

    @Operation(
            summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0435\u0439 \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f.",
            description = "\u0412\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0435\u0442 \u0432\u0441\u0435 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438, \u0432 \u043a\u043e\u0442\u043e\u0440\u044b\u0445 \u0443\u0447\u0430\u0441\u0442\u0432\u0443\u0435\u0442 \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044c."
    )
    @ApiResponse(responseCode = "200", description = "\u0421\u043f\u0438\u0441\u043e\u043a \u0432\u0441\u0435\u0445 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0435\u0439.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OpportunityDTO.class)))
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"/events", "/opportunities"})
    public ResponseList<OpportunityDTO> getMemberOpportunities(
            @PathVariable Long memberId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "tags", required = false) List<String> tags
    ) {
        return subscribeService.getOpportunitiesByMemberId(memberId, page, pageSize, search, tags);
    }

    @Operation(
            summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043f\u0440\u0438\u0437\u043d\u0430\u043a \u0443\u0447\u0430\u0441\u0442\u0438\u044f \u0432 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438.",
            description = "\u0412\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0435\u0442 id \u0441\u043e\u0438\u0441\u043a\u0430\u0442\u0435\u043b\u044f \u0438 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438, \u0435\u0441\u043b\u0438 \u043e\u0442\u043a\u043b\u0438\u043a \u0438\u043b\u0438 \u0443\u0447\u0430\u0441\u0442\u0438\u0435 \u0443\u0436\u0435 \u0441\u043e\u0437\u0434\u0430\u043d\u044b."
    )
    @ApiResponse(responseCode = "200", description = "Id \u0441\u043e\u0438\u0441\u043a\u0430\u0442\u0435\u043b\u044f \u0438 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OpportunitySubscriptionDTO.class)))
    @ApiResponse(responseCode = "404", description = "\u0412\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"/subscribe/{eventId}", "/subscriptions/opportunities/{opportunityId}"})
    public OpportunitySubscriptionDTO getOpportunityIfSubscribed(
            @PathVariable(value = "eventId", required = false) Long eventId,
            @PathVariable(value = "opportunityId", required = false) Long opportunityId,
            @PathVariable Long memberId
    ) {
        return subscribeService.checkOpportunitySubscription(opportunityId != null ? opportunityId : eventId, memberId);
    }

    @Operation(
            summary = "\u041f\u043e\u0434\u043f\u0438\u0441\u0430\u0442\u044c\u0441\u044f \u043d\u0430 \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u0430.",
            description = "\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u0430 \u0432 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0435."
    )
    @ApiResponse(responseCode = "201", description = "\u041e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440 \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u0432 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0435", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u0438", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/organizers/{organizerId}")
    public void subscribeToOrganizer(@PathVariable Long organizerId, @PathVariable Long memberId) {
        subscribeService.subscribeToOrganizer(organizerId, memberId);
    }

    @Operation(
            summary = "\u041e\u0442\u043f\u0438\u0441\u0430\u0442\u044c\u0441\u044f \u043e\u0442 \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u0430.",
            description = "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u0430 \u0438\u0437 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0433\u043e."
    )
    @ApiResponse(responseCode = "201", description = "\u041e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0434\u0430\u043b\u0435\u043d \u0438\u0437 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0433\u043e", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u0438", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "/organizers/{organizerId}")
    public void unsubscribeFromOrganizer(@PathVariable Long organizerId, @PathVariable Long memberId) {
        subscribeService.unsubscribeFromOrganizer(organizerId, memberId);
    }

    @Operation(
            summary = "\u041f\u0440\u043e\u0432\u0435\u0440\u0438\u0442\u044c \u043f\u043e\u0434\u043f\u0438\u0441\u043a\u0443 \u043d\u0430 \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u0430.",
            description = "\u041f\u0440\u043e\u0432\u0435\u0440\u0438\u0442\u044c \u043f\u043e\u0434\u043f\u0438\u0441\u043a\u0443 \u043d\u0430 \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u0430."
    )
    @ApiResponse(responseCode = "201", description = "\u0423\u0447\u0430\u0441\u0442\u043d\u0438\u043a \u043f\u043e\u0434\u043f\u0438\u0441\u0430\u043d \u043d\u0430 \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u0430", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u0438", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/organizers/{organizerId}")
    public MemberOrganizer checkOrganizerSubscribe(@PathVariable Long organizerId, @PathVariable Long memberId) {
        return subscribeService.checkSubscriptionToOrganizer(organizerId, memberId);
    }

    @Operation(
            summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u044b\u0445 \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u043e\u0432.",
            description = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u044b\u0445 \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u043e\u0432 \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f."
    )
    @ApiResponse(responseCode = "201", description = "\u0421\u043f\u0438\u0441\u043e\u043a \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u044b\u0445 \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u043e\u0432", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u0438", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/favorite-organizers")
    public ResponseList<Organizer> getFavoriteOrganizers(
            @PathVariable Long memberId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return subscribeService.getFavoriteOrganizersList(memberId, page, pageSize);
    }
}
