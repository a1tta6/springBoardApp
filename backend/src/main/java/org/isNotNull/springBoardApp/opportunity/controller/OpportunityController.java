package org.isNotNull.springBoardApp.opportunity.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityFileDTO;
import org.isNotNull.springBoardApp.opportunity.service.OpportunityFileService;
import org.isNotNull.springBoardApp.opportunity.service.OpportunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/opportunities")
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "\u0412\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438",
        description = "\u0412\u0430\u043a\u0430\u043d\u0441\u0438\u0438, \u0441\u0442\u0430\u0436\u0438\u0440\u043e\u0432\u043a\u0438, \u043c\u0435\u043d\u0442\u043e\u0440\u0441\u043a\u0438\u0435 \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u044b \u0438 \u043a\u0430\u0440\u044c\u0435\u0440\u043d\u044b\u0435 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f"
)
public class OpportunityController {
    private final OpportunityService opportunityService;
    private final OpportunityFileService opportunityFileService;

    @Operation(summary = "\u0421\u043e\u0437\u0434\u0430\u0442\u044c \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OpportunityDTO create(@RequestBody @Valid OpportunityDTO dto) {
        return opportunityService.createOpportunity(dto);
    }

    @Operation(summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0435\u0439")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseList<OpportunityDTO> list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "opportunityTypes", required = false) List<String> opportunityTypes,
            @RequestParam(value = "workModes", required = false) List<String> workModes,
            @RequestParam(value = "salaryMin", required = false) Integer salaryMin,
            @RequestParam(value = "salaryMax", required = false) Integer salaryMax,
            @RequestParam(value = "activeOnly", defaultValue = "true") boolean activeOnly
    ) {
        return opportunityService.listOpportunities(page, pageSize, search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly);
    }

    @Operation(summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043a\u0430\u0440\u0442\u043e\u0447\u043a\u0443 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public OpportunityDTO get(@PathVariable Long id) {
        return opportunityService.getOpportunity(id);
    }

    @Operation(summary = "\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    public OpportunityDTO update(@PathVariable Long id, @RequestBody @Valid OpportunityDTO dto) {
        return opportunityService.updateOpportunity(id, dto);
    }

    @Operation(summary = "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        return opportunityService.deleteOpportunity(id);
    }

    @Operation(summary = "\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0435\u0439 \u0440\u0430\u0431\u043e\u0442\u043e\u0434\u0430\u0442\u0435\u043b\u044f")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/employers/{id}")
    public ResponseList<OpportunityDTO> listByEmployer(
            @PathVariable("id") Long employerId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "opportunityTypes", required = false) List<String> opportunityTypes,
            @RequestParam(value = "workModes", required = false) List<String> workModes,
            @RequestParam(value = "salaryMin", required = false) Integer salaryMin,
            @RequestParam(value = "salaryMax", required = false) Integer salaryMax,
            @RequestParam(value = "activeOnly", defaultValue = "true") boolean activeOnly
    ) {
        return opportunityService.listEmployerOpportunities(page, pageSize, search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly, employerId);
    }

    @Operation(summary = "\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0444\u0430\u0439\u043b \u043a \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}/files")
    public Long addFile(@PathVariable Long id, @RequestBody @Valid OpportunityFileDTO dto) {
        return opportunityFileService.createOpportunityFile(id, dto);
    }

    @Operation(summary = "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0444\u0430\u0439\u043b \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/files")
    public Long deleteFile(@PathVariable Long id, @RequestBody @Valid OpportunityFileDTO dto) {
        return opportunityFileService.deleteOpportunityFile(id, dto);
    }

    @Operation(summary = "\u0421\u043a\u0430\u0447\u0430\u0442\u044c \u0444\u0430\u0439\u043b \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u0438")
    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        return opportunityFileService.downloadOpportunityFile(fileId);
    }
}
