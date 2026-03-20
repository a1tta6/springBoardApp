package org.isNotNull.springBoardApp.opportunity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.isNotNull.springBoardApp.opportunity.enums.OpportunityFormat;
import org.isNotNull.springBoardApp.tag.dto.TagDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class OpportunityDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private String description;
    private OpportunityFormat format;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime startDateTime;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime endDateTime;
    private Long organizerId;

    private String opportunityType;
    private String workMode;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
    private String contacts;
    private String resources;
    private String visibilityStatus;

    private Set<OpportunityFileDTO> files;
    private Set<TagDTO> tags;
    private byte[] pictures;
    private Long views;
    private Long subscribers;
}
