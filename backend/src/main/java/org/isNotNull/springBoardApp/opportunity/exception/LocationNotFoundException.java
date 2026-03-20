package org.isNotNull.springBoardApp.opportunity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(String location) {
        super(String.format("%s not found", location));
    }
}
