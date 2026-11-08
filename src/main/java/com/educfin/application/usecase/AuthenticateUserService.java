package com.educfin.application.usecase;

import com.educfin.application.dto.AuthTokens;
import com.educfin.application.exception.InvalidCredentialsException;
import com.educfin.application.port.in.AuthenticateUserUseCase;
import com.educfin.application.port.out.PasswordHasher;
import com.educfin.application.port.out.TokenProvider;
import com.educfin.application.port.out.UserRepository;
import com.educfin.domain.exception.DomainException;
import com.educfin.domain.user.Email;
import com.educfin.domain.user.User;

public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    public AuthenticateUserService(UserRepository userRepository,
                                   PasswordHasher passwordHasher,
                                   TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthTokens execute(Input input) {
        Email email = parseEmail(input.email());

        User user = userRepository.findByEmail(email)
                .filter(User::isActive)
                .filter(u -> passwordHasher.matches(input.password(), u.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        return tokenProvider.issueTokens(user);
    }

    private static Email parseEmail(String rawEmail) {
        try {
            return Email.of(rawEmail);
        } catch (DomainException | NullPointerException e) {
            throw new InvalidCredentialsException();
        }
    }
}
