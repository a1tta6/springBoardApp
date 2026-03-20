package org.isNotNull.springBoardApp.curator.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.auth.service.UserDetailsService;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.curator.repository.CuratorRepository;
import org.isNotNull.springBoardApp.enums.OpportunityVisibilityStatusType;
import org.isNotNull.springBoardApp.tables.pojos.Organizer;
import org.isNotNull.springBoardApp.tables.pojos.User;
import org.isNotNull.springBoardApp.user.dto.UserDTO;
import org.isNotNull.springBoardApp.user.dto.UserResponseDTO;
import org.isNotNull.springBoardApp.user.enums.RoleEnum;
import org.isNotNull.springBoardApp.user.service.AdminSecurityService;
import org.isNotNull.springBoardApp.user.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CuratorService {
    private final CuratorRepository curatorRepository;
    private final UserDetailsService userDetailsService;
    private final AdminSecurityService adminSecurityService;
    private final UserService userService;

    private void requireCurator() {
        User user = userDetailsService.getAuthenticatedUser();
        if (Objects.isNull(user) || !RoleEnum.MODERATOR.name().equals(user.getRole().getLiteral())) {
            throw new AccessDeniedException("Curator only");
        }
    }

    private void requireAdministrator() {
        requireCurator();
        if (!adminSecurityService.isAdmin(userDetailsService.getAuthenticatedUser())) {
            throw new AccessDeniedException("Administrator only");
        }
    }

    @Transactional
    public UserResponseDTO createModerator(UserDTO userDTO) {
        requireAdministrator();
        return userService.createModerator(userDTO);
    }

    @Transactional
    public void verifyEmployer(Long employerId, boolean accredited) {
        requireCurator();
        curatorRepository.setEmployerAccredited(employerId, accredited);
    }

    public ResponseList<Organizer> listEmployers(Boolean accreditedOnly, Integer page, Integer pageSize) {
        requireCurator();
        List<Organizer> list = curatorRepository.listEmployers(accreditedOnly, page, pageSize);
        ResponseList<Organizer> response = new ResponseList<>();
        response.setList(list);
        response.setTotal(curatorRepository.countEmployers(accreditedOnly));
        response.setCurrentPage(page);
        response.setPageSize(pageSize);
        return response;
    }

    @Transactional
    public void updateOpportunityVisibility(Long opportunityId, String status) {
        requireCurator();
        OpportunityVisibilityStatusType parsed = OpportunityVisibilityStatusType.valueOf(status.trim().toUpperCase());
        curatorRepository.setOpportunityVisibility(opportunityId, parsed);
    }
}
