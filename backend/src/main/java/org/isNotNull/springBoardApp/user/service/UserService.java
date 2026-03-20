package org.isNotNull.springBoardApp.user.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.tables.Organizer;
import org.isNotNull.springBoardApp.tables.pojos.Member;
import org.isNotNull.springBoardApp.tables.pojos.Moderator;
import org.isNotNull.springBoardApp.tables.pojos.User;
import org.isNotNull.springBoardApp.user.dto.*;
import org.jooq.Condition;
import org.isNotNull.springBoardApp.auth.service.UserDetailsService;
import org.isNotNull.springBoardApp.common.exception.AlreadyExistsException;
import org.isNotNull.springBoardApp.common.exception.MissingFieldException;
import org.isNotNull.springBoardApp.common.exception.UnexpectedException;
import org.isNotNull.springBoardApp.common.dto.ResponseList;
import org.isNotNull.springBoardApp.friendship.repository.FriendRequestRepository;
import org.isNotNull.springBoardApp.user.dto.*;
import org.isNotNull.springBoardApp.user.exception.UserNotFoundException;
import org.isNotNull.springBoardApp.user.enums.PrivacyEnum;
import org.isNotNull.springBoardApp.enums.PrivacyType;
import org.isNotNull.springBoardApp.user.enums.RoleEnum;
import org.isNotNull.springBoardApp.enums.RoleType;
import org.isNotNull.springBoardApp.user.mapper.UserMapper;
import org.isNotNull.springBoardApp.user.repository.UserRepository;
import org.isNotNull.springBoardApp.tables.daos.MemberDao;
import org.isNotNull.springBoardApp.tables.daos.ModeratorDao;
import org.isNotNull.springBoardApp.tables.daos.OrganizerDao;
import org.isNotNull.springBoardApp.tables.pojos.*;
import org.isNotNull.springBoardApp.tables.daos.UserDao;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.isNotNull.springBoardApp.Tables.MEMBER;
import static org.isNotNull.springBoardApp.Tables.USER;
import static org.jooq.impl.DSL.trueCondition;

@Service
@AllArgsConstructor
public class  UserService {
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final UserRepository userRepository;

    private final UserDao userDao;

    private final OrganizerDao organizerDao;

    private final MemberDao memberDao;

    private final ModeratorDao moderatorDao;

    private final UserMapper userMapper;

    private final AdminSecurityService adminSecurityService;

    private final UserDetailsService userDetailsService;

    private final UserSecurityService userSecurityService;
    private final FriendRequestRepository friendRequestRepository;

    public ResponseList<UserResponseDTO> getList(Integer page, Integer pageSize, String search) {

        ResponseList<UserResponseDTO> responseList = new ResponseList<>();

        Condition condition = trueCondition();

        if (Objects.nonNull(search)) {
            condition = condition
                    .and(USER.USERNAME.eq(search))
                    .and(USER.ROLE.eq(RoleType.MEMBER));
        }

        List<UserResponseDTO> list =  userRepository.fetch(condition, page, pageSize);

        responseList.setList(list);
        responseList.setTotal(userRepository.count(condition));
        responseList.setCurrentPage(page);
        responseList.setPageSize(pageSize);
        return responseList;
    }

    public ResponseList<Member> getMembersList(Integer page, Integer pageSize, String search) {
        ResponseList<Member> responseList = new ResponseList<>();
        Condition condition = trueCondition();
        if (Objects.nonNull(search) && !search.trim().isEmpty()) {
            condition = condition.and(MEMBER.LAST_NAME.containsIgnoreCase(search));
            condition = condition.or(MEMBER.FIRST_NAME.containsIgnoreCase(search));
            condition = condition.or(MEMBER.PATRONYMIC.containsIgnoreCase(search));
        }

        List<Member> list =  userRepository.fetchMembers(condition, page, pageSize);

        responseList.setList(list);
        responseList.setTotal(userRepository.countMembers(condition));
        responseList.setCurrentPage(page);
        responseList.setPageSize(pageSize);
        return responseList;
    }

    public ResponseList<org.isNotNull.springBoardApp.tables.pojos.Organizer> getOrganizersList(Integer page, Integer pageSize, String search) {
        ResponseList<org.isNotNull.springBoardApp.tables.pojos.Organizer> responseList = new ResponseList<>();
        Condition condition = trueCondition();
        if (Objects.nonNull(search) && !search.trim().isEmpty()) {
            condition = condition.and(Organizer.ORGANIZER.NAME.containsIgnoreCase(search));
            condition = condition.or(Organizer.ORGANIZER.ADDRESS.containsIgnoreCase(search));
            condition = condition.or(Organizer.ORGANIZER.DESCRIPTION.containsIgnoreCase(search));
            condition = condition.or(Organizer.ORGANIZER.INDUSTRY.containsIgnoreCase(search));
        }

        List<org.isNotNull.springBoardApp.tables.pojos.Organizer> list =  userRepository.fetchOrganizers(condition, page, pageSize);

        responseList.setList(list);
        responseList.setTotal(userRepository.countOrgs(condition));
        responseList.setCurrentPage(page);
        responseList.setPageSize(pageSize);
        return responseList;
    }

    public ResponseList<User> getModeratorsList(Integer page, Integer pageSize, String search) {

        ResponseList<User> responseList = new ResponseList<>();

        if (adminSecurityService.isAdmin(userDetailsService.getAuthenticatedUser())) {
             responseList = new ResponseList<>();

            List<User> list = userRepository.fetchModerators(search, page, pageSize);

            responseList.setList(list);
            responseList.setTotal((long) list.size());
            responseList.setCurrentPage(page);
            responseList.setPageSize(pageSize);
            return responseList;
        }
        return responseList;
    }

    @Transactional
    public UserResponseDTO create(UserDTO userDTO) {
        return createUser(userDTO, false, false);
    }

    @Transactional
    public UserResponseDTO createModerator(UserDTO userDTO) {
        if (!adminSecurityService.isAdmin(userDetailsService.getAuthenticatedUser())) {
            throw new AccessDeniedException("Only administrator can create curator accounts");
        }
        return createUser(userDTO, true, false);
    }

    private UserResponseDTO createUser(UserDTO userDTO, boolean moderatorCreationAllowed, boolean isAdminModerator) {
        if (Objects.isNull(userDTO.getUsername()))
            throw new MissingFieldException("username");
        if (Objects.isNull(userDTO.getDisplayName()))
            throw new MissingFieldException("displayName");
        if (Objects.isNull(userDTO.getPassword()))
            throw new MissingFieldException("password");
        if (Objects.isNull(userDTO.getEmail()))
            throw new MissingFieldException("email");
        if (!moderatorCreationAllowed && Objects.isNull(userDTO.getRole()))
            throw new MissingFieldException("role");

        if (!userDao.fetchByUsername(userDTO.getUsername()).isEmpty())
            throw new AlreadyExistsException("user with username " + userDTO.getUsername());

        if (!userDao.fetchByEmail(userDTO.getEmail()).isEmpty())
            throw new AlreadyExistsException("user with email " + userDTO.getEmail());

        RoleEnum requestedRole = moderatorCreationAllowed ? RoleEnum.MODERATOR : userDTO.getRole();
        if (RoleEnum.MODERATOR.equals(requestedRole) && !moderatorCreationAllowed) {
            throw new AccessDeniedException("Curator registration is available only for platform administrators");
        }

        User user = userMapper.toEntity(userDTO);
        user.setRole(RoleType.valueOf(requestedRole.name()));
        user.setIsActive(true);
        user.setPassword(PASSWORD_ENCODER.encode(user.getPassword()));
        userDao.insert(user);
        if (RoleEnum.ORGANIZER.equals(requestedRole)) {
            org.isNotNull.springBoardApp.tables.pojos.Organizer organizer = userMapper.toOrganizer(user);
            organizer.setIsAccredited(false);
            organizerDao.insert(organizer);
        }
        if (RoleEnum.MEMBER.equals(requestedRole)) {
            Member member = userMapper.toMember(user);
            member.setPrivacy(PrivacyType.ONLY_FRIENDS);
            memberDao.insert(member);
        }
        if (RoleEnum.MODERATOR.equals(requestedRole)) {
            Moderator moderator = userMapper.toModerator(user);
            moderator.setIsAdmin(isAdminModerator);
            moderatorDao.insert(moderator);
        }
        return userMapper.toResponse(user);
    }

    @Transactional
    public org.isNotNull.springBoardApp.tables.pojos.Organizer updateOrganizer(Long id, OrganizerDTO organizerDTO) {

        if (!userSecurityService.isUserOwnData(id, userDetailsService.getAuthenticatedUser()))
            throw new AccessDeniedException(String.format("У вас нет прав для редактирования пользователя с id %d", id));

        userDao.findOptionalById(id)
                .filter(e -> e.getIsActive() && RoleEnum.ORGANIZER.name().equals(e.getRole().toString()))
                .orElseThrow(() -> new UserNotFoundException(id));

        org.isNotNull.springBoardApp.tables.pojos.Organizer organizer = organizerDao.fetchOptionalById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (Objects.nonNull(organizerDTO.getName()))
            organizer.setName(organizerDTO.getName());
        if (Objects.nonNull(organizerDTO.getDescription()))
            organizer.setDescription(organizerDTO.getDescription());
        if (Objects.nonNull(organizerDTO.getIndustry()))
            organizer.setIndustry(organizerDTO.getIndustry());
        if (Objects.nonNull(organizerDTO.getAddress()))
            organizer.setAddress(organizerDTO.getAddress());
        if (Objects.nonNull(organizerDTO.getIsAccredited())) {
            User authenticatedUser = userDetailsService.getAuthenticatedUser();
            boolean isModerator = Objects.nonNull(authenticatedUser) && RoleType.MODERATOR.equals(authenticatedUser.getRole());
            if (!isModerator) {
                throw new AccessDeniedException("Employer verification can be changed only by a curator");
            }
            organizer.setIsAccredited(organizerDTO.getIsAccredited());
        }

        organizerDao.update(organizer);
        return organizer;
    }

    @Transactional
    public Member updateMember(Long id, MemberDTO memberDTO) {

        if (!userSecurityService.isUserOwnData(id, userDetailsService.getAuthenticatedUser()))
            throw new AccessDeniedException(String.format("У вас нет прав для редактирования пользователя с id %d", id));

        userDao.findOptionalById(id)
                .filter(e -> e.getIsActive() && RoleEnum.MEMBER.name().equals(e.getRole().toString()))
                .orElseThrow(() -> new UserNotFoundException(id));

        Member member = memberDao.fetchOptionalById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (Objects.nonNull(memberDTO.getFirstName()))
            member.setFirstName(memberDTO.getFirstName());
        if (Objects.nonNull(memberDTO.getLastName()))
            member.setLastName(memberDTO.getLastName());
        if (Objects.nonNull(memberDTO.getPatronymic()))
            member.setPatronymic(memberDTO.getPatronymic());
        if (Objects.nonNull(memberDTO.getBirthDate()))
            member.setBirthDate(memberDTO.getBirthDate());
        if (Objects.nonNull(memberDTO.getBirthCity()))
            member.setBirthCity(memberDTO.getBirthCity());
        if (Objects.nonNull(memberDTO.getPrivacy())) {
            if (PrivacyEnum.PRIVATE.equals(memberDTO.getPrivacy()))
                member.setPrivacy(PrivacyType.PRIVATE);
            if (PrivacyEnum.PUBLIC.equals(memberDTO.getPrivacy()))
                member.setPrivacy(PrivacyType.PUBLIC);
            if (PrivacyEnum.ONLY_FRIENDS.equals(memberDTO.getPrivacy()))
                member.setPrivacy(PrivacyType.ONLY_FRIENDS);
        }

        memberDao.update(member);
        return member;
    }

    @Transactional
    public Moderator updateModerator(Long id, ModeratorDTO moderatorDTO) {

        if (!userSecurityService.isUserOwnData(id, userDetailsService.getAuthenticatedUser()))
            throw new AccessDeniedException(String.format("У вас нет прав для редактирования пользователя с id %d", id));

        if (!adminSecurityService.isAdmin(userDetailsService.getAuthenticatedUser()))
            throw new AccessDeniedException("Only administrator can update curator privileges");

        if (Objects.isNull(moderatorDTO.getIsAdmin()))
            throw new MissingFieldException("isAdmin");

        userDao.findOptionalById(id)
                .filter(e -> e.getIsActive() && RoleEnum.MODERATOR.name().equals(e.getRole().toString()))
                .orElseThrow(() -> new UserNotFoundException(id));
        Moderator moderator = userMapper.dtoToModerator(moderatorDTO);
        moderator.setId(id);
        moderatorDao.update(moderator);
        return moderator;
    }

    public User get(Long id) {
        return userDao.fetchOptionalById(id).
                filter(User::getIsActive)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getByUsername(String username) {
        return userDao.fetchOptionalByUsername(username).
                filter(User::getIsActive)
                .orElseThrow(() -> new UserNotFoundException(-1L));
    }

    public User getByEmail(String email) {
        return userDao.fetchOptionalByEmail(email).
                filter(User::getIsActive)
                .orElseThrow(() -> new UserNotFoundException(-1L));
    }

    public MemberProfileDTO getMember(Long id) {
        User profileUser = userDao.fetchOptionalById(id)
                .filter(User::getIsActive)
                .orElseThrow(() -> new UserNotFoundException(id));
        Member member = memberDao.fetchOptionalById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User viewer = userDetailsService.getAuthenticatedUser();
        boolean isOwner = Objects.nonNull(viewer) && id.equals(viewer.getId());
        boolean isModerator = Objects.nonNull(viewer) && RoleType.MODERATOR.equals(viewer.getRole());
        boolean isOrganizer = Objects.nonNull(viewer) && RoleType.ORGANIZER.equals(viewer.getRole());
        boolean isFriend = Objects.nonNull(viewer)
                && RoleType.MEMBER.equals(viewer.getRole())
                && !isOwner
                && friendRequestRepository.isFriend(id, viewer.getId()) > 0L;

        boolean profileVisible = isOwner
                || isModerator
                || isOrganizer
                || PrivacyType.PUBLIC.equals(member.getPrivacy())
                || (PrivacyType.ONLY_FRIENDS.equals(member.getPrivacy()) && isFriend);

        return toMemberProfile(profileUser, member, isFriend, isOwner, profileVisible);
    }

    public org.isNotNull.springBoardApp.tables.pojos.Organizer getOrganizer(Long id) {
        return organizerDao.fetchOptionalById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

    }

    public Moderator getModerator(Long id) {
        return moderatorDao.fetchOptionalById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public User update(Long id, UserDTO userDTO) {

        if (!userSecurityService.isUserOwnData(id, userDetailsService.getAuthenticatedUser()))
            throw new AccessDeniedException(String.format("У вас нет прав для редактирования пользователя с id %d", id));

        User user = userDao.findOptionalById(id)
                .filter(User::getIsActive)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (Objects.nonNull(userDTO.getRole())) {
            if (RoleEnum.MEMBER.equals(userDTO.getRole()))
                user.setRole(RoleType.MEMBER);
            if (RoleEnum.ORGANIZER.equals(userDTO.getRole()))
                user.setRole(RoleType.ORGANIZER);
            if (RoleEnum.MODERATOR.equals(userDTO.getRole()))
                user.setRole(RoleType.MODERATOR);
        }
        if (Objects.nonNull(userDTO.getEmail())) {
            user.setEmail(userDTO.getEmail());
        }
        if (Objects.nonNull(userDTO.getIsActive())) {
            user.setIsActive(userDTO.getIsActive());
        }
        if (Objects.nonNull(userDTO.getUsername()))
            user.setUsername(userDTO.getUsername());
        if (Objects.nonNull(userDTO.getDisplayName()))
            user.setDisplayName(userDTO.getDisplayName());
        if (Objects.nonNull(userDTO.getPassword()) && !userDTO.getPassword().isEmpty())
            user.setPassword(PASSWORD_ENCODER.encode(userDTO.getPassword()));

        userDao.update(user);
        return user;
    }

    @Transactional
    public Long delete(Long id) {
        if (Objects.isNull(userRepository.fetchActive(id))) {
            throw new UserNotFoundException(id);
        }
        Long userDeletedId = userRepository.setInactive(id);
        if (Objects.isNull(userDeletedId)) {
            throw new UnexpectedException("The user was not deleted due to an unexpected error.");
        }
        return id;
    }

    private MemberProfileDTO toMemberProfile(
            User profileUser,
            Member member,
            boolean isFriend,
            boolean isOwner,
            boolean profileVisible
    ) {
        MemberProfileDTO profileDTO = new MemberProfileDTO();
        profileDTO.setId(member.getId());
        profileDTO.setUsername(profileUser.getUsername());
        profileDTO.setDisplayName(profileUser.getDisplayName());
        profileDTO.setPrivacy(mapPrivacy(member.getPrivacy()));
        profileDTO.setIsFriend(isFriend);
        profileDTO.setIsOwner(isOwner);
        profileDTO.setProfileVisible(profileVisible);

        if (profileVisible) {
            profileDTO.setFirstName(member.getFirstName());
            profileDTO.setLastName(member.getLastName());
            profileDTO.setPatronymic(member.getPatronymic());
            profileDTO.setBirthDate(member.getBirthDate());
            profileDTO.setBirthCity(member.getBirthCity());
        }

        return profileDTO;
    }

    private PrivacyEnum mapPrivacy(PrivacyType privacyType) {
        if (Objects.isNull(privacyType)) {
            return PrivacyEnum.ONLY_FRIENDS;
        }
        return switch (privacyType) {
            case PUBLIC -> PrivacyEnum.PUBLIC;
            case PRIVATE -> PrivacyEnum.PRIVATE;
            case ONLY_FRIENDS -> PrivacyEnum.ONLY_FRIENDS;
        };
    }
}
