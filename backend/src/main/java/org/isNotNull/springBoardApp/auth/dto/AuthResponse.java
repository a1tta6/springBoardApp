package org.isNotNull.springBoardApp.auth.dto;

import lombok.Data;
import org.isNotNull.springBoardApp.user.dto.UserDTO;

@Data
public class AuthResponse {
    private UserDTO user;
    private Object customUser;
}
