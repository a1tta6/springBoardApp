package org.isNotNull.springBoardApp.domain;

/**
 * Publication state of an opportunity.
 *
 * Example:
 * A curator can move a planned opportunity to active.
 */
public enum OpportunityStatus {
    PLANNED,
    ACTIVE,
    CLOSED
}
