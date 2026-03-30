package org.isNotNull.springBoardApp.repository;

import org.isNotNull.springBoardApp.domain.TagCategory;
import org.isNotNull.springBoardApp.domain.TagEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static org.isNotNull.springBoardApp.repository.DbTables.*;

/**
 * Access to persisted tags through jOOQ.
 *
 * Example:
 * The home page loads all available tags.
 */
@Repository
public class TagRepo {

    private final DSLContext dsl;

    public TagRepo(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public TagEntity save(final TagEntity tag) {
        final UUID id = tag.id() == null ? UUID.randomUUID() : tag.id();
        final boolean exists = this.dsl.fetchExists(this.dsl.selectOne().from(TAGS).where(TAG_ID.eq(id)));
        if (!exists) {
            this.dsl.insertInto(TAGS)
                .set(TAG_ID, id)
                .set(TAG_NAME, tag.name())
                .set(TAG_CATEGORY, tag.category().name())
                .execute();
        } else {
            this.dsl.update(TAGS)
                .set(TAG_NAME, tag.name())
                .set(TAG_CATEGORY, tag.category().name())
                .where(TAG_ID.eq(id))
                .execute();
        }
        return new TagEntity(id, tag.name(), tag.category());
    }

    public List<TagEntity> findAll() {
        return this.dsl.selectFrom(TAGS).fetch(this::map);
    }

    private TagEntity map(final Record record) {
        return new TagEntity(record.get(TAG_ID), record.get(TAG_NAME), TagCategory.valueOf(record.get(TAG_CATEGORY)));
    }
}
