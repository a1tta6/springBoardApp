package org.isNotNull.springBoardApp.tag.service;

import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.common.exception.MissingFieldException;
import org.isNotNull.springBoardApp.opportunity.exception.OpportunityNotFoundException;
import org.isNotNull.springBoardApp.tables.daos.OpportunityDao;
import org.isNotNull.springBoardApp.tables.daos.TagDao;
import org.isNotNull.springBoardApp.tables.daos.UserDao;
import org.isNotNull.springBoardApp.tables.pojos.Tag;
import org.isNotNull.springBoardApp.tag.dto.TagDTO;
import org.isNotNull.springBoardApp.tag.exception.TagNotFoundException;
import org.isNotNull.springBoardApp.tag.mapper.TagMapper;
import org.isNotNull.springBoardApp.tag.repository.TagRepository;
import org.isNotNull.springBoardApp.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.jooq.impl.DSL.trueCondition;

@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    private final TagDao tagDao;

    private final UserDao userDao;

    private final OpportunityDao opportunityDao;

    private final TagMapper tagMapper;

    public ResponseList<Tag> getList(Integer page, Integer pageSize) {
        ResponseList<Tag> responseList = new ResponseList<>();
        Condition condition = trueCondition();

        List<Tag> list =  tagRepository.fetch(condition);

        responseList.setList(list);
        responseList.setTotal(tagRepository.count(condition));
        responseList.setCurrentPage(page);
        responseList.setPageSize(pageSize);
        return responseList;
    }
    public ResponseList<Tag> getFavoriteList(Long userId, Integer page, Integer pageSize) {
        ResponseList<Tag> responseList = new ResponseList<>();
        List<Tag> list =  tagRepository.fetchFavorites(userId);
        responseList.setList(list);
        return responseList;
    }

    public Tag get(Long id) {
        return tagDao.fetchOptionalById(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    @Transactional
    public void addTagsToUser(Long tagId, Long userId) {
        userDao.findOptionalById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        tagDao.findOptionalById(tagId).orElseThrow(() -> new TagNotFoundException(tagId));

        assignTagsToUser(userId, tagId);
    }

    @Transactional
    public Tag addTagsToOpportunity(Long opportunityId, TagDTO tagDTO) {

        if (Objects.isNull(tagDTO.getName()) || tagDTO.getName().isEmpty())
            throw new MissingFieldException("name");

        Tag tag = tagMapper.toEntity(tagDTO);

        var existingTag = tagDao.fetchByName(tagDTO.getName());

        if (existingTag.isEmpty()) {
            tagDao.insert(tag);
        } else {
            tag.setId(existingTag.getFirst().getId());
        }

        opportunityDao.findOptionalById(opportunityId).orElseThrow(() -> new OpportunityNotFoundException(opportunityId));

        assignTagToOpportunity(opportunityId, tag);
        return tag;
    }

    @Deprecated(forRemoval = false)
    public Tag addTagsToEvent(Long eventId, TagDTO tagDTO) {
        return addTagsToOpportunity(eventId, tagDTO);
    }

    @Transactional
    public void deleteTagFromOpportunity(Long tagId, Long opportunityId) {
        if (tagDao.fetchOptionalById(tagId).isEmpty()) {
            throw new TagNotFoundException(tagId);
        }
        opportunityDao.fetchOptionalById(opportunityId).orElseThrow(() -> new OpportunityNotFoundException(opportunityId));

        tagRepository.deleteTagFromOpportunity(tagId, opportunityId);
        if (!tagRepository.tagIsUsed(tagId)) {
            tagDao.deleteById(tagId);
        }
    }

    @Deprecated(forRemoval = false)
    public void deleteTagFromEvent(Long tagId, Long eventId) {
        deleteTagFromOpportunity(tagId, eventId);
    }

    @Transactional
    public void deleteTagFromUser(Long tagId, Long userId) {
        if (tagDao.fetchOptionalById(tagId).isEmpty()) {
            throw new TagNotFoundException(tagId);
        }
        userDao.fetchOptionalById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        tagRepository.deleteTagFromUser(tagId, userId);
        if (!tagRepository.tagIsUsed(tagId)) {
            tagDao.deleteById(tagId);
        }
    }

    public List<String> createNewTags(List<String> tags) {
        List<String> existingTagNames = tagRepository.fetch(tags).stream().map(Tag::getName).toList();

        List<String> newTagNames = tags.stream()
                .filter(tag -> !existingTagNames.contains(tag))
                .distinct()
                .toList();

        tagRepository.createTags(newTagNames);

        return newTagNames;
    }

    public Set<Long> getUsedTagIdsForOpportunity(Long opportunityId) {
        return tagRepository.getUsedTagIdsForOpportunity(opportunityId);
    }

    @Deprecated(forRemoval = false)
    public Set<Long> getUsedTagIdsForEvent(Long eventId) {
        return getUsedTagIdsForOpportunity(eventId);
    }

    public void assignTagsToOpportunity(Long opportunityId, List<Tag> tags) {
         tagRepository.assignNewOpportunityTags(opportunityId, tags);
    }

    @Deprecated(forRemoval = false)
    public void assignTagsToEvent(Long eventId, List<Tag> tags) {
        assignTagsToOpportunity(eventId, tags);
    }

    public void assignTagToOpportunity(Long opportunityId, Tag tag) {
        tagRepository.assignNewOpportunityTag(opportunityId, tag);
    }

    @Deprecated(forRemoval = false)
    public void assignTagToEvent(Long eventId, Tag tag) {
        assignTagToOpportunity(eventId, tag);
    }


    public Set<Long> getUsedTagIdsForUser(Long userId) {
        return tagRepository.getUsedTagIdsForUser(userId);
    }

    public void assignTagsToUser(Long userId, Long tagId) {
        tagRepository.assignTagToUser(tagId, userId);
    }
}
