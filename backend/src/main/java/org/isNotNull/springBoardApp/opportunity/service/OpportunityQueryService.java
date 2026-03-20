package org.isNotNull.springBoardApp.opportunity.service;

import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.isNotNull.springBoardApp.auth.service.UserDetailsService;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.enums.OpportunityType;
import org.isNotNull.springBoardApp.enums.OpportunityVisibilityStatusType;
import org.isNotNull.springBoardApp.enums.WorkModeType;
import org.isNotNull.springBoardApp.opportunity.adapter.OpportunityEventAdapter;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.opportunity.enums.OpportunityFormat;
import org.isNotNull.springBoardApp.opportunity.exception.OpportunityNotFoundException;
import org.isNotNull.springBoardApp.opportunity.repository.OpportunityRepository;
import org.isNotNull.springBoardApp.subscribe.repository.SubscribeRepository;
import org.isNotNull.springBoardApp.tables.daos.OpportunityDao;
import org.isNotNull.springBoardApp.tables.daos.OpportunityFileDao;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.isNotNull.springBoardApp.tag.mapper.TagMapper;
import org.isNotNull.springBoardApp.tag.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.jooq.impl.DSL.trueCondition;
import static org.isNotNull.springBoardApp.tables.Opportunity.OPPORTUNITY;

@Service
@AllArgsConstructor
public class OpportunityQueryService {

    private final OpportunityRepository opportunityRepository;
    private final TagRepository tagRepository;
    private final OpportunityDao opportunityDao;
    private final TagMapper tagMapper;
    private final OpportunityFileDao opportunityFileDao;
    private final SubscribeRepository subscribeRepository;
    private final UserDetailsService userDetailsService;
    private final OpportunityEventAdapter opportunityEventAdapter;

    public ResponseList<OpportunityDTO> listOpportunities(
            Integer page,
            Integer pageSize,
            String search,
            List<String> tags,
            List<String> opportunityTypes,
            List<String> workModes,
            Integer salaryMin,
            Integer salaryMax,
            boolean activeOnly
    ) {
        Condition condition = buildOpportunitiesCondition(search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly);
        return getOpportunityList(condition, page, pageSize, false);
    }

    public ResponseList<OpportunityDTO> listEmployerOpportunities(
            Integer page,
            Integer pageSize,
            String search,
            List<String> tags,
            List<String> opportunityTypes,
            List<String> workModes,
            Integer salaryMin,
            Integer salaryMax,
            boolean activeOnly,
            Long employerId
    ) {
        Condition condition = buildOpportunitiesCondition(search, tags, opportunityTypes, workModes, salaryMin, salaryMax, activeOnly);
        if (Objects.nonNull(employerId)) {
            condition = condition.and(OPPORTUNITY.ORGANIZER_ID.eq(employerId));
        }
        return getOpportunityList(condition, page, pageSize, false);
    }

    public ResponseList<OpportunityDTO> listMemberOpportunities(
            Integer page,
            Integer pageSize,
            String search,
            List<String> tags,
            Long memberId
    ) {
        Condition condition = buildCommonListCondition(search, tags);
        if (Objects.nonNull(memberId)) {
            condition = condition.and(OPPORTUNITY.ID.in(subscribeRepository.fetchOpportunityIdsByMemberId(memberId)));
        }
        return getOpportunityList(condition, page, pageSize, false);
    }

    public OpportunityDTO getOpportunity(Long id) {
        Long userId = userDetailsService.getAuthenticatedUser() != null
                ? userDetailsService.getAuthenticatedUser().getId()
                : null;
        Opportunity opportunity = opportunityDao.fetchOptionalById(id).orElseThrow(() -> new OpportunityNotFoundException(id));
        if (userId != null) {
            opportunityRepository.recordView(userId, id);
        }
        return enrichOpportunity(opportunity, true);
    }

    public OpportunityDTO enrichOpportunity(Opportunity opportunity) {
        return enrichOpportunity(opportunity, false);
    }

    public OpportunityDTO enrichOpportunity(Opportunity opportunity, boolean includeFiles) {
        OpportunityDTO opportunityDTO = opportunityEventAdapter.toOpportunityDto(opportunity);
        opportunityDTO.setViews(opportunityRepository.fetchViews(opportunity.getId()));
        opportunityDTO.setSubscribers(opportunityRepository.fetchSubscriptionsCount(opportunity.getId()));
        opportunityDTO.setTags(tagRepository.fetch(opportunity.getId()).stream().map(tagMapper::toDto).collect(java.util.stream.Collectors.toSet()));
        if (includeFiles) {
            opportunityDTO.setFiles(opportunityFileDao.fetchByOpportunityId(opportunity.getId()).stream()
                    .map(opportunityEventAdapter::toOpportunityFileDto)
                    .collect(java.util.stream.Collectors.toSet()));
        }
        return opportunityDTO;
    }

    public Condition buildCommonListCondition(String search, List<String> tags) {
        Condition condition = trueCondition();

        if (Objects.nonNull(search) && !search.trim().isEmpty()) {
            condition = condition.and(OPPORTUNITY.TITLE.containsIgnoreCase(search));
            condition = condition.or(OPPORTUNITY.SHORT_DESCRIPTION.containsIgnoreCase(search));
            condition = condition.or(OPPORTUNITY.LOCATION.containsIgnoreCase(search));

            Map<String, String> formatRuMap = Map.of(
                    "ONLINE", "\u041e\u043d\u043b\u0430\u0439\u043d",
                    "OFFLINE", "\u041e\u0444\u043b\u0430\u0439\u043d"
            );

            List<OpportunityFormat> matchingFormats = formatRuMap.entrySet().stream()
                    .filter(entry -> entry.getValue().toLowerCase().contains(search.toLowerCase()))
                    .map(entry -> OpportunityFormat.valueOf(entry.getKey()))
                    .toList();

            if (!matchingFormats.isEmpty()) {
                condition = condition.or(OPPORTUNITY.FORMAT.in(matchingFormats));
            }
        }
        if (Objects.nonNull(tags) && !tags.isEmpty()) {
            condition = condition.and(OPPORTUNITY.ID.in(opportunityRepository.fetchOpportunityIdsBySelectedTags(tags)));
        }

        return condition;
    }

    public Condition buildOpportunitiesCondition(
            String search,
            List<String> tags,
            List<String> opportunityTypes,
            List<String> workModes,
            Integer salaryMin,
            Integer salaryMax,
            boolean activeOnly
    ) {
        Condition condition = buildCommonListCondition(search, tags);

        if (Objects.nonNull(opportunityTypes) && !opportunityTypes.isEmpty()) {
            List<OpportunityType> parsed = opportunityTypes.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .map(OpportunityType::valueOf)
                    .toList();
            if (!parsed.isEmpty()) {
                condition = condition.and(OPPORTUNITY.OPPORTUNITY_TYPE.in(parsed));
            }
        }
        if (Objects.nonNull(workModes) && !workModes.isEmpty()) {
            List<WorkModeType> parsed = workModes.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .map(WorkModeType::valueOf)
                    .toList();
            if (!parsed.isEmpty()) {
                condition = condition.and(OPPORTUNITY.WORK_MODE.in(parsed));
            }
        }
        if (Objects.nonNull(salaryMin)) {
            condition = condition.and(
                    OPPORTUNITY.SALARY_MAX.isNull()
                            .or(OPPORTUNITY.SALARY_MAX.ge(new java.math.BigDecimal(salaryMin)))
            );
        }
        if (Objects.nonNull(salaryMax)) {
            condition = condition.and(
                    OPPORTUNITY.SALARY_MIN.isNull()
                            .or(OPPORTUNITY.SALARY_MIN.le(new java.math.BigDecimal(salaryMax)))
            );
        }
        if (activeOnly) {
            condition = condition.and(OPPORTUNITY.VISIBILITY_STATUS.eq(OpportunityVisibilityStatusType.PUBLISHED));
            condition = condition.and(
                    OPPORTUNITY.EXPIRES_AT.isNull()
                            .or(OPPORTUNITY.EXPIRES_AT.ge(java.time.LocalDateTime.now()))
            );
        }

        return condition;
    }

    private ResponseList<OpportunityDTO> getOpportunityList(Condition condition, Integer page, Integer pageSize, boolean includeFiles) {
        ResponseList<OpportunityDTO> response = new ResponseList<>();
        List<Opportunity> opportunitiesData = opportunityRepository.fetch(condition, page, pageSize);
        List<OpportunityDTO> opportunities = new ArrayList<>();
        opportunitiesData.forEach(opportunity -> opportunities.add(enrichOpportunity(opportunity, includeFiles)));
        response.setList(opportunities);
        response.setTotal(opportunityRepository.count(condition));
        response.setCurrentPage(page);
        response.setPageSize(pageSize);
        return response;
    }
}
