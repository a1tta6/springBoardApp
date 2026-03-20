package org.isNotNull.springBoardApp.subscribe.mapper;

import org.isNotNull.springBoardApp.subscribe.dto.OpportunitySubscriptionDTO;
import org.isNotNull.springBoardApp.tables.pojos.OpportunityParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpportunitySubscriptionMapper {

    @Mapping(source = "opportunityId", target = "opportunityId")
    @Mapping(source = "memberId", target = "userId")
    OpportunitySubscriptionDTO toDto(OpportunityParticipant opportunityParticipant);
}
