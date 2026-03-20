package org.isNotNull.springBoardApp.tag.repository;

import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Insert;
import org.isNotNull.springBoardApp.tables.pojos.Tag;
import org.isNotNull.springBoardApp.tables.records.TagRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static org.isNotNull.springBoardApp.Tables.*;

@Repository
@AllArgsConstructor
public class TagRepository {

    private final DSLContext dslContext;

    public List<Tag> fetch(Condition condition) {
        return dslContext
                .selectFrom(TAG)
                .where(condition)
                .fetchInto(Tag.class);
    }

    public List<Tag> fetchByOpportunityId(Long opportunityId) {
        return dslContext
                .select(TAG.fields())
                .from(TAG)
                .innerJoin(OPPORTUNITY_TAG).on(OPPORTUNITY_TAG.TAG_ID.eq(TAG.ID))
                .where(OPPORTUNITY_TAG.OPPORTUNITY_ID.eq(opportunityId))
                .fetchInto(Tag.class);
    }

    @Deprecated(forRemoval = false)
    public List<Tag> fetch(Long eventId) {
        return fetchByOpportunityId(eventId);
    }

    public List<Tag> fetchFavorites(Long userId) {
        return dslContext
                .select(TAG.fields())
                .from(TAG)
                .innerJoin(USER_TAGS).on(USER_TAGS.TAG_ID.eq(TAG.ID))
                .where(USER_TAGS.USER_ID.eq(userId))
                .fetchInto(Tag.class);
    }

    public List<Tag> fetch(List<String> tagNames) {
        return dslContext.selectFrom(TAG)
                .where(TAG.NAME.in(tagNames))
                .fetchInto(Tag.class);
    }

    public Long count(Condition condition) {
        return dslContext
                .selectCount()
                .from(TAG)
                .where(condition)
                .fetchOneInto(Long.class);
    }

    public boolean tagIsUsed(Long id) {
        return dslContext.fetchExists(
                dslContext.selectOne()
                        .from(OPPORTUNITY_TAG)
                        .where(OPPORTUNITY_TAG.TAG_ID.eq(id))
                        .limit(1)
        ) || dslContext.fetchExists(
                dslContext.selectOne()
                        .from(USER_TAGS)
                        .where(USER_TAGS.TAG_ID.eq(id))
                        .limit(1)
        );
    }


    public Tag findTagByName(String name) {
        return dslContext.selectFrom(TAG)
                .where(TAG.NAME.equalIgnoreCase(name))
                .fetchOneInto(Tag.class);
    }

    public Tag createTag(String name){
        return dslContext.insertInto(TAG)
                .set(TAG.NAME, name)
                .returning()
                .fetchOneInto(Tag.class);
    }

    public void createTags(List<String> tagNames) {
        List<TagRecord> records = tagNames.stream()
                .map(name -> dslContext.newRecord(TAG).setName(name))
                .toList();

        dslContext.batchInsert(records).execute();
    }

    public Set<Long> getUsedTagIdsForOpportunity(Long opportunityId) {
        return dslContext.select(OPPORTUNITY_TAG.TAG_ID)
                .from(OPPORTUNITY_TAG)
                .where(OPPORTUNITY_TAG.OPPORTUNITY_ID.eq(opportunityId))
                .fetchSet(OPPORTUNITY_TAG.TAG_ID);
    }

    @Deprecated(forRemoval = false)
    public Set<Long> getUsedTagIdsForEvent(Long eventId) {
        return getUsedTagIdsForOpportunity(eventId);
    }

    public Set<Long> getUsedTagIdsForUser(Long userId) {
        return dslContext.select(USER_TAGS.TAG_ID)
                .from(USER_TAGS)
                .where(USER_TAGS.USER_ID.eq(userId))
                .fetchSet(USER_TAGS.TAG_ID);
    }

    public void assignNewOpportunityTags(Long opportunityId, List<Tag> tags) {
        var batch = dslContext.batch(
                tags.stream()
                        .map(tag -> dslContext.insertInto(OPPORTUNITY_TAG)
                                .set(OPPORTUNITY_TAG.OPPORTUNITY_ID, opportunityId)
                                .set(OPPORTUNITY_TAG.TAG_ID, tag.getId()))
                        .toArray(Insert[]::new)
        );
        batch.execute();
    }

    @Deprecated(forRemoval = false)
    public void assignNewEventTags(Long eventId, List<Tag> tags) {
        assignNewOpportunityTags(eventId, tags);
    }

    public void assignNewOpportunityTag(Long opportunityId, Tag tag) {
        dslContext.insertInto(OPPORTUNITY_TAG)
                .set(OPPORTUNITY_TAG.OPPORTUNITY_ID, opportunityId)
                .set(OPPORTUNITY_TAG.TAG_ID, tag.getId())
                .execute();
    }

    @Deprecated(forRemoval = false)
    public void assignNewEventTag(Long eventId, Tag tag) {
        assignNewOpportunityTag(eventId, tag);
    }



    public void assignTagToUser(Long tagId, Long userId) {
        dslContext.insertInto(USER_TAGS)
                .set(USER_TAGS.USER_ID, userId)
                .set(USER_TAGS.TAG_ID, tagId)
                .execute();
    }

    public void deleteTagFromOpportunity(Long tagId, Long opportunityId) {
        dslContext.deleteFrom(OPPORTUNITY_TAG)
                .where(OPPORTUNITY_TAG.OPPORTUNITY_ID.eq(opportunityId))
                .and(OPPORTUNITY_TAG.TAG_ID.eq(tagId))
                .execute();
    }

    @Deprecated(forRemoval = false)
    public void deleteTagFromEvent(Long tagId, Long eventId) {
        deleteTagFromOpportunity(tagId, eventId);
    }

    public void deleteTagFromUser(Long tagId, Long userId) {
        dslContext.deleteFrom(USER_TAGS)
                .where(USER_TAGS.USER_ID.eq(userId))
                .and(USER_TAGS.TAG_ID.eq(tagId))
                .execute();
    }
}
