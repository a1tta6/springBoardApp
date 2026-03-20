package org.isNotNull.springBoardApp.opportunity.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class OpportunityFileDTO {
    private Long fileId;
    @JsonAlias("eventId")
    private Long opportunityId;
    private byte[] fileContent;
    private String fileName;
    private String fileType;
    private Long fileSize;

    @Deprecated(forRemoval = false)
    public Long getEventId() {
        return opportunityId;
    }

    @Deprecated(forRemoval = false)
    public void setEventId(Long eventId) {
        this.opportunityId = eventId;
    }
}
