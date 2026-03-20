package org.isNotNull.springBoardApp.opportunity.response.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.opportunity.response.dto.OpportunityResponseDTO;
import org.isNotNull.springBoardApp.opportunity.response.dto.UpdateOpportunityResponseStatusRequest;
import org.isNotNull.springBoardApp.opportunity.response.service.OpportunityResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/opportunities")
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "\u041e\u0442\u043a\u043b\u0438\u043a\u0438",
        description = "\u041e\u0442\u043a\u043b\u0438\u043a\u0438 \u0441\u043e\u0438\u0441\u043a\u0430\u0442\u0435\u043b\u0435\u0439 \u043d\u0430 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438"
)
public class OpportunityResponseController {
    private final OpportunityResponseService opportunityResponseService;

    @Operation(summary = "\u041e\u0442\u043a\u043b\u0438\u043a\u043d\u0443\u0442\u044c\u0441\u044f \u043d\u0430 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/responses")
    public void apply(@PathVariable("id") Long opportunityId) {
        opportunityResponseService.apply(opportunityId);
    }

    @Operation(summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043e\u0442\u043a\u043b\u0438\u043a\u0438 \u043f\u043e \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/responses")
    public List<OpportunityResponseDTO> list(@PathVariable("id") Long opportunityId) {
        return opportunityResponseService.listForOpportunity(opportunityId);
    }

    @Operation(summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043c\u043e\u0438 \u043e\u0442\u043a\u043b\u0438\u043a\u0438")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/responses/me")
    public List<OpportunityResponseDTO> listMine() {
        return opportunityResponseService.listMine();
    }

    @Operation(summary = "\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u0441\u0442\u0430\u0442\u0443\u0441 \u043e\u0442\u043a\u043b\u0438\u043a\u0430")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/responses/{applicantId}")
    public void updateStatus(
            @PathVariable("id") Long opportunityId,
            @PathVariable Long applicantId,
            @RequestBody @Valid UpdateOpportunityResponseStatusRequest request
    ) {
        opportunityResponseService.updateStatus(opportunityId, applicantId, request.getStatus());
    }
}
