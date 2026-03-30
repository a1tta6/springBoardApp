package org.isNotNull.springBoardApp.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Serializes small collection fields to JSON text columns.
 *
 * Example:
 * Skill lists and tag identifiers are stored as JSON arrays.
 */
@Component
public final class JsonStore {

    private final ObjectMapper mapper;

    public JsonStore(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String textList(final List<String> values) {
        return this.write(values == null ? List.of() : values);
    }

    public List<String> textList(final String payload) {
        return this.read(payload, new TypeReference<List<String>>() { });
    }

    public String uuidList(final List<UUID> values) {
        return this.write((values == null ? List.<UUID>of() : values).stream().map(UUID::toString).toList());
    }

    public List<UUID> uuidList(final String payload) {
        return this.textList(payload).stream().map(UUID::fromString).toList();
    }

    private String write(final Object value) {
        try {
            return this.mapper.writeValueAsString(value);
        } catch (JsonProcessingException failure) {
            throw new IllegalStateException("Cannot write json payload", failure);
        }
    }

    private <T> T read(final String payload, final TypeReference<T> type) {
        if (payload == null || payload.isBlank()) {
            try {
                return this.mapper.readValue("[]", type);
            } catch (JsonProcessingException failure) {
                throw new IllegalStateException("Cannot read empty json payload", failure);
            }
        }
        try {
            return this.mapper.readValue(payload, type);
        } catch (JsonProcessingException failure) {
            throw new IllegalStateException("Cannot read json payload", failure);
        }
    }
}
