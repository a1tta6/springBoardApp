package org.isNotNull.springBoardApp.user.dto;

import lombok.Data;
import org.isNotNull.springBoardApp.user.enums.RoleEnum;
import org.isNotNull.springBoardApp.user.validation.ValidUserContact;

@Data
@ValidUserContact
public class UserDTO {
    private Long id;
    private String username;
    private String displayName;
    private String password;
    private String email;
    private Boolean isActive;
    private RoleEnum role;
}
