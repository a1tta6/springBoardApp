package org.isNotNull.springBoardApp.domain;

import java.util.UUID;

/**
 * Search tag exposed to the landing page filter bar.
 *
 * Example:
 * Java is a technology tag while Junior is a level tag.
 */
public final class TagEntity {

    private final UUID id;
    private final String name;
    private final TagCategory category;

    public TagEntity(final UUID id, final String name, final TagCategory category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public TagEntity(final String name, final TagCategory category) {
        this(null, name, category);
    }

    public UUID id() { return this.id; }
    public String name() { return this.name; }
    public TagCategory category() { return this.category; }
}
