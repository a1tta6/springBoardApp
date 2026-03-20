package org.isNotNull.springBoardApp.subscribe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OpportunitySubscriptionNotFound extends RuntimeException {
    public OpportunitySubscriptionNotFound(Long userId, Long opportunityId) {
        super(String.format("User %d is not subscribed to opportunity %d", userId, opportunityId));
    }
}
