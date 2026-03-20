package org.isNotNull.springBoardApp.opportunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CoordinatesDTO {
    private BigDecimal latitude;
    private BigDecimal longitude;
}
