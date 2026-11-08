package com.educfin.application.fakes;

import com.educfin.application.dto.AuthTokens;
import com.educfin.application.exception.InvalidTokenException;
import com.educfin.application.port.out.TokenProvider;
import com.educfin.domain.user.User;
import com.educfin.domain.user.UserId;

/**
 * Emite tokens legíveis no formato {@code access:<id>} / {@code refresh:<id>}.
 */
public class FakeTokenProvider implements TokenProvider {

    public static final long ACCESS_TTL = 900;
    public static final long REFRESH_TTL = 604_800;

    private static final String ACCESS_PREFIX = "access:";
    private static final String REFRESH_PREFIX = "refresh:";

    @Override
    public AuthTokens issueTokens(User user) {
        return new AuthTokens(
                accessTokenFor(user.getId()),
                refreshTokenFor(user.getId()),
                ACCESS_TTL,
                REFRESH_TTL
        );
    }

    @Override
    public UserId validateRefreshToken(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith(REFRESH_PREFIX)) {
            throw new InvalidTokenException();
        }
        try {
            return UserId.of(refreshToken.substring(REFRESH_PREFIX.length()));
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    public static String accessTokenFor(UserId id) {
        return ACCESS_PREFIX + id;
    }

    public static String refreshTokenFor(UserId id) {
        return REFRESH_PREFIX + id;
    }
}
