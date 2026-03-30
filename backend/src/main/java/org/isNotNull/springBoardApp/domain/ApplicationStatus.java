package org.isNotNull.springBoardApp.domain;

/**
 * Review state of an application.
 *
 * Example:
 * Employers can switch an application to accepted.
 */
public enum ApplicationStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    RESERVED
}
