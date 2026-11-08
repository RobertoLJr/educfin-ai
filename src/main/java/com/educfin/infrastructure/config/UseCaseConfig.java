package com.educfin.infrastructure.config;

import com.educfin.application.port.in.AuthenticateUserUseCase;
import com.educfin.application.port.in.GetAuthenticatedUserUseCase;
import com.educfin.application.port.in.RefreshTokenUseCase;
import com.educfin.application.port.in.RegisterUserUseCase;
import com.educfin.application.port.out.PasswordHasher;
import com.educfin.application.port.out.TokenProvider;
import com.educfin.application.port.out.UserRepository;
import com.educfin.application.usecase.AuthenticateUserService;
import com.educfin.application.usecase.GetAuthenticatedUserService;
import com.educfin.application.usecase.RefreshTokenService;
import com.educfin.application.usecase.RegisterUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Composition root: registra os casos de uso como beans do Spring sem que a
 * camada de aplicação precise conhecer o framework (nenhuma anotação Spring nela).
 */
@Configuration
public class UseCaseConfig {

    @Bean
    Clock clock() {
        return Clock.tick(Clock.systemUTC(), Duration.of(1, ChronoUnit.MICROS));
    }

    @Bean
    RegisterUserUseCase registerUserUseCase(UserRepository userRepository,
                                            PasswordHasher passwordHasher,
                                            Clock clock) {
        return new RegisterUserService(userRepository, passwordHasher, clock);
    }

    @Bean
    AuthenticateUserUseCase authenticateUserUseCase(UserRepository userRepository,
                                                    PasswordHasher passwordHasher,
                                                    TokenProvider tokenProvider) {
        return new AuthenticateUserService(userRepository, passwordHasher, tokenProvider);
    }

    @Bean
    RefreshTokenUseCase refreshTokenUseCase(UserRepository userRepository, TokenProvider tokenProvider) {
        return new RefreshTokenService(userRepository, tokenProvider);
    }

    @Bean
    GetAuthenticatedUserUseCase getAuthenticatedUserUseCase(UserRepository userRepository) {
        return new GetAuthenticatedUserService(userRepository);
    }
}
