package org.isNotNull.springBoardApp.opportunity.adapter;

import org.isNotNull.springBoardApp.opportunity.dto.OpportunityDTO;
import org.isNotNull.springBoardApp.opportunity.dto.OpportunityFileDTO;
import org.isNotNull.springBoardApp.tables.pojos.Opportunity;
import org.isNotNull.springBoardApp.tables.pojos.OpportunityFile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OpportunityEventAdapter {

    OpportunityDTO toOpportunityDto(Opportunity opportunity);

    Opportunity toOpportunity(OpportunityDTO opportunityDTO);

    OpportunityFileDTO toOpportunityFileDto(OpportunityFile opportunityFile);

    OpportunityFile toOpportunityFile(OpportunityFileDTO opportunityFileDTO);
}
