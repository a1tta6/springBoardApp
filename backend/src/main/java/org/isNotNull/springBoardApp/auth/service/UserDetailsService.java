package org.isNotNull.springBoardApp.auth.service;

import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.auth.dto.AuthResponse;
import org.isNotNull.springBoardApp.auth.util.CustomUserDetails;
import org.isNotNull.springBoardApp.config.jwt.JwtProperties;
import org.isNotNull.springBoardApp.config.jwt.JwtUtil;
import org.isNotNull.springBoardApp.enums.RoleType;
import org.isNotNull.springBoardApp.user.exception.UserNotFoundException;
import org.isNotNull.springBoardApp.user.mapper.UserMapper;
import org.isNotNull.springBoardApp.user.repository.UserRepository;
import org.isNotNull.springBoardApp.tables.daos.MemberDao;
import org.isNotNull.springBoardApp.tables.daos.ModeratorDao;
import org.isNotNull.springBoardApp.tables.daos.OrganizerDao;
import org.isNotNull.springBoardApp.tables.pojos.Member;
import org.isNotNull.springBoardApp.tables.pojos.Moderator;
import org.isNotNull.springBoardApp.tables.pojos.Organizer;
import org.isNotNull.springBoardApp.tables.pojos.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private UserRepository userRepository;

    private final OrganizerDao organizerDao;
    private final MemberDao memberDao;
    private final ModeratorDao moderatorDao;

    private final UserMapper userMapper;

    private JwtUtil jwtUtil;

    private JwtProperties jwtProperties;

    @Override
    public UserDetails loadUserByUsername(String username)  {

        User user = userRepository.fetchByUsername(username);

        if (Objects.isNull(user))
                throw new UserNotFoundException(-1L);

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()))
        );
    }

    public UserDetails loadUserByEmail(String email)  {

        User user = userRepository.fetchByEmail(email);

        if (Objects.isNull(user))
            throw new UserNotFoundException(-1L);

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()))
        );
    }
    
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return null;
        }

        String authenticatedUsername = authentication.getName();
        if (Objects.isNull(authenticatedUsername) || "anonymousUser".equalsIgnoreCase(authenticatedUsername)) {
            return null;
        }
        return userRepository.fetchByUsername(authenticatedUsername);
    }

    public AuthResponse createAuthResponse(UserDetails userDetails) {

        User user = userRepository.fetchByUsername(userDetails.getUsername());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setUser(userMapper.toDto(user));

        if (user.getRole().equals(RoleType.ORGANIZER)) {
            Organizer organizer = organizerDao.fetchOneById(user.getId());
            authResponse.setCustomUser(organizer);
        }

        if (user.getRole().equals(RoleType.MEMBER)) {
            Member member = memberDao.fetchOneById(user.getId());
            authResponse.setCustomUser(member);
        }

        if (user.getRole().equals(RoleType.MODERATOR)) {
            Moderator moderator = moderatorDao.fetchOneById(user.getId());
            authResponse.setCustomUser(moderator);
        }

        return authResponse;
    }
}
