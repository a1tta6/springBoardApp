package org.isNotNull.springBoardApp.opportunity.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OpportunityFileNotFoundException extends EntityNotFoundException {
    public OpportunityFileNotFoundException(Long fileId) {
        super(String.format("Opportunity file %d not found", fileId));
    }
}
