package org.isNotNull.springBoardApp.opportunity.response.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOpportunityResponseStatusRequest {
    @NotBlank
    private String status;
}

