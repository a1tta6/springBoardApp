package org.isNotNull.springBoardApp.opportunity.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OpportunityNotFoundException extends EntityNotFoundException {
    public OpportunityNotFoundException(Long opportunityId) {
        super(String.format("Opportunity %d not found", opportunityId));
    }
}
