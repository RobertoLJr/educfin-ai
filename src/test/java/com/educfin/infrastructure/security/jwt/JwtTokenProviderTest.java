package com.educfin.infrastructure.security.jwt;

import com.educfin.application.dto.AuthTokens;
import com.educfin.application.exception.InvalidTokenException;
import com.educfin.domain.user.Email;
import com.educfin.domain.user.User;
import com.educfin.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-with-at-least-32-characters!!";
    private static final String ISSUER = "educfin-test";
    private static final Duration ACCESS_TTL = Duration.ofMinutes(15);
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    private final JwtConfig config = new JwtConfig();
    private JwtProperties properties;
    private SecretKey key;
    private JwtTokenProvider provider;
    private User user;

    @BeforeEach
    void setUp() {
        properties = new JwtProperties(SECRET, ISSUER, ACCESS_TTL, REFRESH_TTL);
        key = config.jwtSecretKey(properties);
        provider = providerWithClock(Clock.systemUTC());
        user = User.create("Maria", Email.of("maria@example.com"), "hash", Instant.now());
    }

    private JwtTokenProvider providerWithClock(Clock clock) {
        return new JwtTokenProvider(config.jwtEncoder(key), key, properties, clock);
    }

    @Test
    @DisplayName("emite access e refresh tokens diferentes com as validades configuradas")
    void issuesTokens() {
        AuthTokens tokens = provider.issueTokens(user);

        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank().isNotEqualTo(tokens.accessToken());
        assertThat(tokens.accessTokenExpiresIn()).isEqualTo(ACCESS_TTL.toSeconds());
        assertThat(tokens.refreshTokenExpiresIn()).isEqualTo(REFRESH_TTL.toSeconds());
    }

    @Test
    @DisplayName("access token contém subject, issuer, tipo e dados básicos do usuário")
    void accessTokenClaims() {
        AuthTokens tokens = provider.issueTokens(user);
        JwtDecoder accessDecoder = config.jwtDecoder(key, properties);

        Jwt jwt = accessDecoder.decode(tokens.accessToken());

        assertThat(jwt.getSubject()).isEqualTo(user.getId().toString());
        assertThat(jwt.getClaimAsString("iss")).isEqualTo(ISSUER);
        assertThat(jwt.getClaimAsString(TokenType.CLAIM)).isEqualTo("access");
        assertThat(jwt.getClaimAsString("email")).isEqualTo("maria@example.com");
        assertThat(jwt.getClaimAsString("name")).isEqualTo("Maria");
        assertThat(jwt.getId()).isNotBlank();
        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(ACCESS_TTL);
    }

    @Test
    @DisplayName("refresh token não carrega dados pessoais")
    void refreshTokenHasNoPersonalData() {
        AuthTokens tokens = provider.issueTokens(user);
        JwtDecoder refreshDecoder = JwtConfig.buildDecoder(key, ISSUER, TokenType.REFRESH);

        Jwt jwt = refreshDecoder.decode(tokens.refreshToken());

        assertThat(jwt.getClaimAsString(TokenType.CLAIM)).isEqualTo("refresh");
        assertThat(jwt.getClaims()).doesNotContainKeys("email", "name");
    }

    @Test
    @DisplayName("valida refresh token e retorna o id do usuário")
    void validatesRefreshToken() {
        AuthTokens tokens = provider.issueTokens(user);

        UserId userId = provider.validateRefreshToken(tokens.refreshToken());

        assertThat(userId).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("access token não é aceito como refresh token")
    void rejectsAccessTokenAsRefresh() {
        AuthTokens tokens = provider.issueTokens(user);

        assertThatThrownBy(() -> provider.validateRefreshToken(tokens.accessToken()))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("refresh token não é aceito pelo decoder de access token (Resource Server)")
    void rejectsRefreshTokenAsAccess() {
        AuthTokens tokens = provider.issueTokens(user);
        JwtDecoder accessDecoder = config.jwtDecoder(key, properties);

        assertThatThrownBy(() -> accessDecoder.decode(tokens.refreshToken()))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("rejeita refresh token expirado")
    void rejectsExpiredToken() {
        Instant issuedLongAgo = Instant.now().minus(REFRESH_TTL).minus(Duration.ofDays(1));
        JwtTokenProvider pastProvider = providerWithClock(Clock.fixed(issuedLongAgo, ZoneOffset.UTC));
        String expired = pastProvider.issueTokens(user).refreshToken();

        assertThatThrownBy(() -> provider.validateRefreshToken(expired))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("rejeita token assinado com outro segredo")
    void rejectsForeignSignature() {
        JwtProperties otherProps = new JwtProperties("another-secret-with-at-least-32-chars!!", ISSUER, ACCESS_TTL, REFRESH_TTL);
        SecretKey otherKey = config.jwtSecretKey(otherProps);
        String foreign = new JwtTokenProvider(config.jwtEncoder(otherKey), otherKey, otherProps, Clock.systemUTC())
                .issueTokens(user).refreshToken();

        assertThatThrownBy(() -> provider.validateRefreshToken(foreign))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("rejeita token de outro issuer")
    void rejectsForeignIssuer() {
        JwtProperties otherProps = new JwtProperties(SECRET, "outro-issuer", ACCESS_TTL, REFRESH_TTL);
        String foreign = new JwtTokenProvider(config.jwtEncoder(key), key, otherProps, Clock.systemUTC())
                .issueTokens(user).refreshToken();

        assertThatThrownBy(() -> provider.validateRefreshToken(foreign))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("rejeita token adulterado")
    void rejectsTamperedToken() {
        String token = provider.issueTokens(user).refreshToken();
        String[] parts = token.split("\\.");
        String tampered = parts[0] + "." + parts[1] + "x." + parts[2];

        assertThatThrownBy(() -> provider.validateRefreshToken(tampered))
                .isInstanceOf(InvalidTokenException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "nao-e-um-jwt", "a.b.c"})
    @DisplayName("rejeita valores que não são JWT")
    void rejectsGarbage(String token) {
        assertThatThrownBy(() -> provider.validateRefreshToken(token))
                .isInstanceOf(InvalidTokenException.class);
    }
}
