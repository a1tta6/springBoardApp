package org.isNotNull.springBoardApp.user.dto;

import lombok.Data;
import org.isNotNull.springBoardApp.user.enums.RoleEnum;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String displayName;
    private String email;
    private RoleEnum role;
}
