package org.isNotNull.springBoardApp.tag.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.common.dto.ResponseDTO;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.tables.pojos.Tag;
import org.isNotNull.springBoardApp.tag.dto.TagDTO;
import org.isNotNull.springBoardApp.tag.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/v1/tags")
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "\u0422\u0435\u0433\u0438",
        description = "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0442\u0435\u0433\u0430\u043c\u0438"
)
public class TagController {

    private final TagService tagService;

    @Operation(
            summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0432\u0441\u0435\u0445 \u0442\u0435\u0433\u043e\u0432",
            description = "\u0412\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0435\u0442 \u0432\u0441\u0435 \u0442\u0435\u0433\u0438."
    )
    @ApiResponse(
            responseCode = "200",
            description = "\u0421\u043f\u0438\u0441\u043e\u043a \u0432\u0441\u0435\u0445 \u0442\u0435\u0433\u043e\u0432",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))
    )
    @GetMapping
    public ResponseList<Tag> getList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return tagService.getList(page, pageSize);
    }

    @Operation(
            summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u044b\u0445 \u0442\u0435\u0433\u043e\u0432 \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f",
            description = "\u0412\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0435\u0442 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u044b\u0435 \u0442\u0435\u0433\u0438."
    )
    @ApiResponse(
            responseCode = "200",
            description = "\u0421\u043f\u0438\u0441\u043e\u043a \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u044b\u0445 \u0442\u0435\u0433\u043e\u0432 \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))
    )
    @GetMapping("/{userId}")
    public ResponseList<Tag> getFavoriteList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @PathVariable Long userId
    ) {
        return tagService.getFavoriteList(userId, page, pageSize);
    }

    @Operation(
            summary = "\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0442\u0435\u0433\u0430 \u0432 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0435 \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f.",
            description = "\u0414\u043e\u0431\u0430\u0432\u043b\u044f\u0435\u0442 \u0442\u0435\u0433 \u0432 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0435 \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f."
    )
    @ApiResponse(
            responseCode = "201",
            description = "\u0422\u0435\u0433\u0438 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u044b",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u0438",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
    )
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/{id}/users/{userId}")
    public void addTagsToUser(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        tagService.addTagsToUser(id, userId);
    }

    @Operation(
            summary = "\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043d\u043e\u0432\u044b\u0445 \u0442\u0435\u0433\u043e\u0432 \u043a \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438.",
            description = "\u0414\u043e\u0431\u0430\u0432\u043b\u044f\u0435\u0442 \u043d\u043e\u0432\u044b\u0435 \u0442\u0435\u0433\u0438 \u043a \u0432\u0430\u043a\u0430\u043d\u0441\u0438\u0438, \u0441\u0442\u0430\u0436\u0438\u0440\u043e\u0432\u043a\u0435 \u0438\u043b\u0438 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044e."
    )
    @ApiResponse(
            responseCode = "201",
            description = "\u0422\u0435\u0433\u0438 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u044b",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u0438",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
    )
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping({"/events/{id}", "/opportunities/{id}"})
    public Tag addTagsToOpportunity(
            @PathVariable Long id,
            @RequestBody TagDTO tagDTO
    ) {
        return tagService.addTagsToOpportunity(id, tagDTO);
    }

    @Operation(
            summary = "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0442\u0435\u0433 \u0443 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438.",
            description = "\u0423\u0434\u0430\u043b\u044f\u0435\u0442 \u0442\u0435\u0433 \u043f\u043e ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "\u0422\u0435\u0433 \u0443\u0434\u0430\u043b\u0435\u043d.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "\u0422\u0435\u0433 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
    )
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = {"/{id}/events/{eventId}", "/{id}/opportunities/{opportunityId}"})
    public void deleteTagFromOpportunity(
            @PathVariable Long id,
            @PathVariable(value = "eventId", required = false) Long eventId,
            @PathVariable(value = "opportunityId", required = false) Long opportunityId
    ) {
        tagService.deleteTagFromOpportunity(id, opportunityId != null ? opportunityId : eventId);
    }

    @Operation(
            summary = "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0442\u0435\u0433 \u0438\u0437 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u043e\u0433\u043e \u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f.",
            description = "\u0423\u0434\u0430\u043b\u044f\u0435\u0442 \u0442\u0435\u0433 \u043f\u043e ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "\u0422\u0435\u0433 \u0443\u0434\u0430\u043b\u0435\u043d.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "\u0422\u0435\u0433 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
    )
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}/users/{userId}")
    public void deleteTagFromUser(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        tagService.deleteTagFromUser(id, userId);
    }
}
