package org.isNotNull.springBoardApp.opportunity.response.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OpportunityResponseDTO {
    private Long opportunityId;
    private String opportunityTitle;
    private Long applicantId;
    private String applicantUsername;
    private String applicantDisplayName;
    private String applicantEmail;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
