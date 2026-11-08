package com.educfin.presentation.controller;

import com.educfin.application.port.in.GetAuthenticatedUserUseCase;
import com.educfin.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final GetAuthenticatedUserUseCase getAuthenticatedUser;

    public UserController(GetAuthenticatedUserUseCase getAuthenticatedUser) {
        this.getAuthenticatedUser = getAuthenticatedUser;
    }

    @Operation(summary = "Retorna os dados do usuário autenticado")
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        var output = getAuthenticatedUser.execute(
                new GetAuthenticatedUserUseCase.Input(UUID.fromString(jwt.getSubject())));
        return UserResponse.from(output);
    }
}
