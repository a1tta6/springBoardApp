package org.isNotNull.springBoardApp.user.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.user.enums.RoleEnum;
import org.isNotNull.springBoardApp.user.exception.UserNotFoundException;
import org.isNotNull.springBoardApp.user.repository.ModeratorRepository;
import org.isNotNull.springBoardApp.tables.pojos.Moderator;
import org.isNotNull.springBoardApp.tables.pojos.User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class AdminSecurityService {

    private ModeratorRepository moderatorRepository;

    public boolean isAdmin(User authenticatedUser) {
        long id = authenticatedUser.getId();
        Moderator moderator = moderatorRepository.findById(id);

        if (Objects.isNull(moderator))
            throw new UserNotFoundException(id);

        return moderator.getIsAdmin() &&
                RoleEnum.MODERATOR.name().equals(authenticatedUser.getRole().getLiteral());

    }
}
