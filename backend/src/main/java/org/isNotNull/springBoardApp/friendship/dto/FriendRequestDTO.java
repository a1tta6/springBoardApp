package org.isNotNull.springBoardApp.friendship.dto;

import lombok.Data;
import org.isNotNull.springBoardApp.friendship.enums.FriendRequestStatusEnum;

@Data
public class FriendRequestDTO {
    private RequesterDTO sender;
    private RequesterDTO recipient;
    private FriendRequestStatusEnum friendRequestStatus;
}

