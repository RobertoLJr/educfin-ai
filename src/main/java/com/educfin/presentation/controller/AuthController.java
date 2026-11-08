package com.educfin.presentation.controller;

import com.educfin.application.port.in.AuthenticateUserUseCase;
import com.educfin.application.port.in.RefreshTokenUseCase;
import com.educfin.application.port.in.RegisterUserUseCase;
import com.educfin.presentation.dto.request.LoginRequest;
import com.educfin.presentation.dto.request.RefreshTokenRequest;
import com.educfin.presentation.dto.request.RegisterRequest;
import com.educfin.presentation.dto.response.TokenResponse;
import com.educfin.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller "magro": apenas traduz HTTP -> caso de uso -> HTTP.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação")
public class AuthController {

    private final RegisterUserUseCase registerUser;
    private final AuthenticateUserUseCase authenticateUser;
    private final RefreshTokenUseCase refreshToken;

    public AuthController(RegisterUserUseCase registerUser,
                          AuthenticateUserUseCase authenticateUser,
                          RefreshTokenUseCase refreshToken) {
        this.registerUser = registerUser;
        this.authenticateUser = authenticateUser;
        this.refreshToken = refreshToken;
    }

    @Operation(summary = "Cadastra um novo usuário")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        var output = registerUser.execute(
                new RegisterUserUseCase.Input(request.name(), request.email(), request.password()));
        return UserResponse.from(output);
    }

    @Operation(summary = "Autentica com e-mail e senha e retorna os tokens")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        var tokens = authenticateUser.execute(
                new AuthenticateUserUseCase.Input(request.email(), request.password()));
        return TokenResponse.from(tokens);
    }

    @Operation(summary = "Gera um novo par de tokens a partir de um refresh token")
    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        var tokens = refreshToken.execute(new RefreshTokenUseCase.Input(request.refreshToken()));
        return TokenResponse.from(tokens);
    }
}
