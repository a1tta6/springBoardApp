package org.isNotNull.springBoardApp.curator.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.curator.dto.EmployerVerificationRequest;
import org.isNotNull.springBoardApp.curator.dto.UpdateOpportunityVisibilityStatusRequest;
import org.isNotNull.springBoardApp.curator.service.CuratorService;
import org.isNotNull.springBoardApp.tables.pojos.Organizer;
import org.isNotNull.springBoardApp.user.dto.UserDTO;
import org.isNotNull.springBoardApp.user.dto.UserResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/curator")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Куратор", description = "Модерация и верификация работодателей")
public class CuratorController {
    private final CuratorService curatorService;

    @Operation(summary = "Create curator account (administrator only)")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/moderators")
    public UserResponseDTO createModerator(@RequestBody @Valid UserDTO userDTO) {
        return curatorService.createModerator(userDTO);
    }

    @Operation(summary = "Список работодателей (для куратора)")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/employers")
    public ResponseList<Organizer> listEmployers(
            @RequestParam(value = "accredited", required = false) Boolean accredited,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return curatorService.listEmployers(accredited, page, pageSize);
    }

    @Operation(summary = "Верифицировать/снять верификацию работодателя")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/employers/{id}/verification")
    public void verifyEmployer(
            @PathVariable("id") Long employerId,
            @RequestBody @Valid EmployerVerificationRequest request
    ) {
        curatorService.verifyEmployer(employerId, request.getIsAccredited());
    }

    @Operation(summary = "Модерация возможности: смена статуса видимости")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/opportunities/{id}/visibility")
    public void updateOpportunityVisibility(
            @PathVariable("id") Long opportunityId,
            @RequestBody @Valid UpdateOpportunityVisibilityStatusRequest request
    ) {
        curatorService.updateOpportunityVisibility(opportunityId, request.getStatus());
    }
}
