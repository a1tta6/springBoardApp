package org.isNotNull.springBoardApp.friendship.dto;

import lombok.Data;
import org.isNotNull.springBoardApp.user.enums.PrivacyEnum;

@Data
public class FriendCheckDTO {
    private Boolean friendly;
    private PrivacyEnum privacy;
}
