package org.isNotNull.springBoardApp.common.api;

/**
 * Signals that a requested entity does not exist.
 *
 * Example:
 * Services throw this exception when an identifier cannot be resolved.
 */
public final class MissingEntityException extends RuntimeException {

    public MissingEntityException(final String message) {
        super(message);
    }
}
