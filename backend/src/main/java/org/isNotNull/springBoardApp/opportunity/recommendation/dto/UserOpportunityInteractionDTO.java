package org.isNotNull.springBoardApp.opportunity.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.isNotNull.springBoardApp.opportunity.recommendation.enums.OpportunityInteractionType;

import java.time.LocalDateTime;

@Data
public class UserOpportunityInteractionDTO {
    private Long id;
    private Long userId;
    private Long opportunityId;
    private OpportunityInteractionType interactionType;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;
}
