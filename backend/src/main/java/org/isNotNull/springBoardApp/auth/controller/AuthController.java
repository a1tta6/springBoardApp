package org.isNotNull.springBoardApp.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.isNotNull.springBoardApp.auth.dto.AuthRequest;
import org.isNotNull.springBoardApp.auth.dto.AuthResponse;
import org.isNotNull.springBoardApp.auth.service.AuthService;
import org.isNotNull.springBoardApp.user.dto.UserDTO;
import org.isNotNull.springBoardApp.user.dto.UserResponseDTO;
import org.isNotNull.springBoardApp.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Авторизация", description = "Аутентификация и авторизация пользователей")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public UserResponseDTO create(@RequestBody @Valid UserDTO userDTO) {
        return userService.create(userDTO);
    }

    @PostMapping("/login")
    public void login(@RequestBody AuthRequest request, HttpServletResponse response) {
        authService.login(request, response);
    }

    @PostMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
       authService.refreshToken(request, response);
    }

    @GetMapping("/me")
    public AuthResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return authService.getCurrentUser(userDetails);
    }
}
