package org.isNotNull.springBoardApp.curator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployerVerificationRequest {
    @NotNull
    private Boolean isAccredited;
}

