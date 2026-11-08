package com.educfin.application.usecase;

import com.educfin.application.dto.AuthTokens;
import com.educfin.application.exception.InvalidTokenException;
import com.educfin.application.port.in.RefreshTokenUseCase;
import com.educfin.application.port.out.TokenProvider;
import com.educfin.application.port.out.UserRepository;
import com.educfin.domain.user.User;
import com.educfin.domain.user.UserId;

public class RefreshTokenService implements RefreshTokenUseCase {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public RefreshTokenService(UserRepository userRepository, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthTokens execute(Input input) {
        UserId userId = tokenProvider.validateRefreshToken(input.refreshToken());

        User user = userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(InvalidTokenException::new);

        return tokenProvider.issueTokens(user);
    }
}
