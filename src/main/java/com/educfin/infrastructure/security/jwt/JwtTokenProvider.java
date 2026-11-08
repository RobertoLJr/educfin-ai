package com.educfin.infrastructure.security.jwt;

import com.educfin.application.dto.AuthTokens;
import com.educfin.application.exception.InvalidTokenException;
import com.educfin.application.port.out.TokenProvider;
import com.educfin.domain.user.User;
import com.educfin.domain.user.UserId;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Adapter que implementa a porta {@link TokenProvider} usando JWT assinado (HS256).
 */
@Component
public class JwtTokenProvider implements TokenProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder refreshTokenDecoder;
    private final JwtProperties properties;
    private final Clock clock;

    public JwtTokenProvider(JwtEncoder jwtEncoder, SecretKey jwtSecretKey, JwtProperties properties, Clock clock) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
        this.clock = clock;
        this.refreshTokenDecoder = JwtConfig.buildDecoder(jwtSecretKey, properties.issuer(), TokenType.REFRESH);
    }

    @Override
    public AuthTokens issueTokens(User user) {
        Instant now = Instant.now(clock);
        Duration accessTtl = properties.accessTokenTtl();
        Duration refreshTtl = properties.refreshTokenTtl();

        String accessToken = encode(user, TokenType.ACCESS, now, accessTtl, Map.of(
                "name", user.getName(),
                "email", user.getEmail().value()
        ));
        String refreshToken = encode(user, TokenType.REFRESH, now, refreshTtl, Map.of());

        return new AuthTokens(accessToken, refreshToken, accessTtl.toSeconds(), refreshTtl.toSeconds());
    }

    @Override
    public UserId validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException();
        }
        try {
            Jwt jwt = refreshTokenDecoder.decode(refreshToken);
            return UserId.of(jwt.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    private String encode(User user, TokenType type, Instant now, Duration ttl, Map<String, Object> extraClaims) {
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer(properties.issuer())
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .claim(TokenType.CLAIM, type.value());
        extraClaims.forEach(claims::claim);

        JwsHeader header = JwsHeader.with(JwtConfig.ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}
