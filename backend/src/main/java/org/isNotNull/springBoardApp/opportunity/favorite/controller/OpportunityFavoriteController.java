package org.isNotNull.springBoardApp.opportunity.favorite.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.opportunity.favorite.service.OpportunityFavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/opportunities")
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "\u0418\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0435",
        description = "\u0418\u0437\u0431\u0440\u0430\u043d\u043d\u044b\u0435 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438 \u0442\u0435\u043a\u0443\u0449\u0435\u0433\u043e \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f"
)
public class OpportunityFavoriteController {
    private final OpportunityFavoriteService opportunityFavoriteService;

    @Operation(summary = "\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c \u0432 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0435")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/favorite")
    public void add(@PathVariable("id") Long opportunityId) {
        opportunityFavoriteService.addFavorite(opportunityId);
    }

    @Operation(summary = "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c \u0438\u0437 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0433\u043e")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/favorite")
    public void remove(@PathVariable("id") Long opportunityId) {
        opportunityFavoriteService.removeFavorite(opportunityId);
    }

    @Operation(summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u044b\u0445 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0435\u0439")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/favorites")
    public ResponseList<OpportunityDTO> list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return opportunityFavoriteService.listFavorites(page, pageSize);
    }
}
