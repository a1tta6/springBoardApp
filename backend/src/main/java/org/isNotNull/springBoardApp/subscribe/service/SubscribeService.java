package org.isNotNull.springBoardApp.subscribe.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.auth.service.UserDetailsService;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.opportunity.exception.OpportunityNotFoundException;
import org.isNotNull.springBoardApp.opportunity.service.OpportunityFacade;
import org.isNotNull.springBoardApp.subscribe.dto.OpportunitySubscriptionDTO;
import org.isNotNull.springBoardApp.subscribe.exception.MemberNotFound;
import org.isNotNull.springBoardApp.subscribe.exception.OpportunitySubscribeException;
import org.isNotNull.springBoardApp.subscribe.exception.OpportunitySubscriptionNotFound;
import org.isNotNull.springBoardApp.subscribe.mapper.OpportunitySubscriptionMapper;
import org.isNotNull.springBoardApp.subscribe.repository.SubscribeRepository;
import org.isNotNull.springBoardApp.tables.daos.MemberDao;
import org.isNotNull.springBoardApp.tables.daos.MemberOrganizerDao;
import org.isNotNull.springBoardApp.tables.pojos.Member;
import org.isNotNull.springBoardApp.tables.pojos.MemberOrganizer;
import org.isNotNull.springBoardApp.tables.daos.OpportunityDao;
import org.isNotNull.springBoardApp.tables.daos.OpportunityParticipantDao;
import org.isNotNull.springBoardApp.tables.pojos.OpportunityParticipant;
import org.isNotNull.springBoardApp.tables.pojos.Organizer;
import org.isNotNull.springBoardApp.user.dto.MemberDTO;
import org.isNotNull.springBoardApp.user.mapper.UserMapper;
import org.isNotNull.springBoardApp.user.service.UserSecurityService;
import org.isNotNull.springBoardApp.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SubscribeService {

    private final OpportunityFacade opportunityFacade;
    private final UserSecurityService userSecurityService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SubscribeRepository subscribeRepository;
    private final OpportunityParticipantDao opportunityParticipantDao;
    private final MemberOrganizerDao memberOrganizerDao;
    private final OpportunityDao opportunityDao;
    private final OpportunitySubscriptionMapper opportunitySubscriptionMapper;
    private final MemberDao memberDao;

    public void subscribeToOpportunity(Long opportunityId, Long memberId) {
        opportunityFacade.getOpportunity(opportunityId);
        //userMapper.toMemberDto(userService.getMember(memberId));
        //userService.getMember(memberId);

        if (Objects.nonNull(subscribeRepository.fetchOptionalByMemberIdAndOpportunityId(memberId, opportunityId, 1, 1))) {
            throw new OpportunitySubscribeException(String.format("User %d already subscribed to opportunity %d", memberId, opportunityId));
        }

        OpportunityParticipant participant = new OpportunityParticipant();
        participant.setOpportunityId(opportunityId);
        participant.setMemberId(memberId);
        opportunityParticipantDao.insert(participant);
    }

    public void unsubscribeFromOpportunity(Long opportunityId, Long memberId) {
        if (userSecurityService.isUserOwnData(memberId, userDetailsService.getAuthenticatedUser())) {
            OpportunityParticipant participant = subscribeRepository.fetchOptionalByMemberIdAndOpportunityId(memberId, opportunityId, 1, 1);
            opportunityParticipantDao.delete(participant);
        }
    }

    public void subscribeToOrganizer(Long organizerId, Long memberId) {
        if (userSecurityService.isUserOwnData(memberId, userDetailsService.getAuthenticatedUser())) {
            MemberOrganizer memberOrganizer = new MemberOrganizer();
            memberOrganizer.setMemberId(memberId);
            memberOrganizer.setOrganizerId(organizerId);
            memberOrganizerDao.insert(memberOrganizer);
        }
    }

    public void unsubscribeFromOrganizer(Long organizerId, Long memberId) {
        if (userSecurityService.isUserOwnData(memberId, userDetailsService.getAuthenticatedUser())) {
            MemberOrganizer memberOrganizer = subscribeRepository.fetchOptionalByMemberIdAndOrganizerId(memberId, organizerId, 1, 1);
            memberOrganizerDao.delete(memberOrganizer);
        }
    }

    public MemberOrganizer checkSubscriptionToOrganizer(Long organizerId, Long memberId) {
        if (userSecurityService.isUserOwnData(memberId, userDetailsService.getAuthenticatedUser())) {
            return subscribeRepository.fetchOptionalByMemberIdAndOrganizerId(memberId, organizerId, 1, 1);
        }
        return null;
    }

    public ResponseList<OpportunityDTO> getOpportunitiesByMemberId(
            Long memberId,
            Integer page,
            Integer pageSize,
            String search,
            List<String> tags
    ) {
        return opportunityFacade.listMemberOpportunities(page, pageSize, search, tags, memberId);
    }

    public ResponseList<Member> getMembersByOpportunityId(Long opportunityId, Integer page, Integer pageSize) {
        ResponseList<Member> responseList = new ResponseList<>();
        List<Member> list = subscribeRepository.fetchMembersByOpportunityId(opportunityId, page, pageSize);
        responseList.setList(list);
        responseList.setCurrentPage(page);
        responseList.setPageSize(pageSize);
        return responseList;
    }

    public OpportunitySubscriptionDTO checkOpportunitySubscription(Long opportunityId, Long memberId) {
        opportunityDao.fetchOptionalById(opportunityId)
                .orElseThrow(() -> new OpportunityNotFoundException(opportunityId));

        MemberDTO memberDTO = userMapper.toMemberDto(memberDao.fetchOptionalById(memberId)
                .orElseThrow(() -> new MemberNotFound(memberId)));

        OpportunitySubscriptionDTO opportunitySubscriptionDTO = opportunitySubscriptionMapper.toDto(
                subscribeRepository.fetchOptionalByMemberIdAndOpportunityId(memberId, opportunityId, 1, 1)
        );

        if (Objects.isNull(opportunitySubscriptionDTO)) {
            throw new OpportunitySubscriptionNotFound(memberId, opportunityId);
        }

        opportunitySubscriptionDTO.setUserId(memberDTO.getId());
        return opportunitySubscriptionDTO;
    }

    public ResponseList<Organizer> getFavoriteOrganizersList(Long memberId, Integer page, Integer pageSize) {
        ResponseList<Organizer> responseList = new ResponseList<>();
        if (userSecurityService.isUserOwnData(memberId, userDetailsService.getAuthenticatedUser())) {
            var orgList = subscribeRepository.fetchFavoriteOrganizersByMemberId(memberId, page, pageSize);
            responseList.setList(orgList);
            responseList.setTotal((long) orgList.size());
        }

        responseList.setCurrentPage(page);
        responseList.setPageSize(pageSize);
        return responseList;
    }
}
