package org.isNotNull.springBoardApp.common.api;

/**
 * Error payload returned by REST endpoints.
 *
 * Example:
 * An unauthorized request receives a JSON object with a message.
 */
public record ErrorJson(String message) {
}
