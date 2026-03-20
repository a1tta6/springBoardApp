package org.isNotNull.springBoardApp.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.isNotNull.springBoardApp.user.enums.PrivacyEnum;

import java.time.LocalDate;

@Data
public class MemberDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String birthCity;
    private PrivacyEnum privacy;
}
