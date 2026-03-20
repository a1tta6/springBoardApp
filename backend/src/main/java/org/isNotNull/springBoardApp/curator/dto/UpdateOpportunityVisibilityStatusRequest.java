package org.isNotNull.springBoardApp.curator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOpportunityVisibilityStatusRequest {
    @NotBlank
    private String status; // DRAFT/PUBLISHED/ARCHIVED/REJECTED
}

