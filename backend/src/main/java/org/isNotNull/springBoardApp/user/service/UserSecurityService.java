package org.isNotNull.springBoardApp.user.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.enums.RoleType;
import org.isNotNull.springBoardApp.tables.daos.ModeratorDao;
import org.isNotNull.springBoardApp.tables.pojos.Moderator;
import org.isNotNull.springBoardApp.user.exception.UserNotFoundException;
import org.isNotNull.springBoardApp.tables.pojos.User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserSecurityService {

    private final ModeratorDao moderatorDao;

    public boolean isUserOwnData(Long userId, User authenticatedUser) {

        if (Objects.isNull(authenticatedUser))
            throw new UserNotFoundException(userId);

        if (RoleType.MODERATOR.equals(authenticatedUser.getRole())) {
            Optional<Moderator> moderator = moderatorDao.fetchOptionalById(authenticatedUser.getId());
            if (moderator.isPresent()) {
                return true;
            }
        }

        return authenticatedUser.getId().equals(userId);
    }
}
