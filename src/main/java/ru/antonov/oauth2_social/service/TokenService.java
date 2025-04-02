package ru.antonov.oauth2_social.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.transaction.annotation.Transactional;
import ru.antonov.oauth2_social.dto.AuthResponseDto;
import ru.antonov.oauth2_social.dto.TokenRequestDto;
import ru.antonov.oauth2_social.dto.TokenValidationCheckResponse;
import ru.antonov.oauth2_social.entity.TokenEntity;
import ru.antonov.oauth2_social.entity.TokenMode;
import ru.antonov.oauth2_social.entity.TokenType;
import ru.antonov.oauth2_social.entity.UserEntity;
import ru.antonov.oauth2_social.exception.TokenConfigurationException;
import ru.antonov.oauth2_social.repository.TokenRepository;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Value("${application.jwt.secret-key}")
    private String secretKey;
    @Value("${application.jwt.user-access-token-expiration}")
    private Long userAccessTokenExpiration;
    @Value("${application.jwt.user-refresh-token-expiration}")
    private Long userRefreshTokenExpiration;
    @Value("${application.name}")
    private String issuer;

    private final TokenRepository tokenRepository;
    private final UserService userService;

    public String extractUsername(String token){return extractClaim(token, Claims::getSubject);}
    public String extractIssuedAt(String token){return extractClaim(token, Claims::getIssuedAt).toString(); }
    public String extractExpiredAt(String token) {return extractClaim(token, Claims::getExpiration).toString();}
    public String extractIssuer(String token) { return extractClaim(token, Claims::getIssuer);}
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public String generateUserToken(
            List<String> roles,
            String email,
            TokenMode tokenMode
    ){
        Map<String, Object> extraClaims = new HashMap<>(Map.of("roles", roles));

        Long expiration = 0L;
        if(tokenMode.equals(TokenMode.ACCESS)){
            expiration = userAccessTokenExpiration;
        }
        else if(tokenMode.equals(TokenMode.REFRESH)){
            expiration = userRefreshTokenExpiration;
        }

        return buildToken(
                extraClaims,
                email,
                expiration);
    }


    private String buildToken(
            Map<String, Object> extraClaims,
            String email,
            long expiration
    ){
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .issuer(issuer)
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenValid(String token){
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch ( JwtException | IllegalArgumentException ex){
            throw new TokenConfigurationException("Токен неправильно сконфигурирован или истек!");
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenValidationCheckResponse isTokenValid(TokenRequestDto tokenRequestDto){
        String token = tokenRequestDto.getToken();

        boolean isValid = isTokenValid(token);

        Boolean isNotRevokedAndNotExpired = tokenRepository
                .findByToken(token)
                .filter(t -> t.getTokenMode().equals(TokenMode.ACCESS))
                .map(t -> !t.isRevoked() && !t.isExpired())
                .orElse(false);

        return TokenValidationCheckResponse
                .builder()
                .isValid( isValid && Boolean.TRUE.equals(isNotRevokedAndNotExpired) )
                .build();
    }

    public AuthResponseDto refreshAccessToken(TokenRequestDto tokenRequestDto){
        String refresh = tokenRequestDto.getToken();
        boolean isRefreshTokenValid = isTokenValid(refresh) &&
                tokenRepository.findByToken(refresh)
                        .filter(t -> t.getTokenMode().equals(TokenMode.REFRESH))
                        .map(t -> !t.isRevoked() && !t.isExpired())
                        .orElse(false);

        if(!isRefreshTokenValid){
            throw new TokenConfigurationException("Токен истек или отозван");
        }

        String userEmail = extractUsername(refresh);
        List<String> roles = extractRoles(refresh);

        revokeAllUserTokens(userEmail);
        String accessToken = generateUserToken(roles, userEmail, TokenMode.ACCESS);
        String refreshToken = generateUserToken(roles, userEmail, TokenMode.REFRESH);

        UserEntity user = userService.findUserByEmail(userEmail);

        saveToken(accessToken, TokenType.BEARER, TokenMode.ACCESS,  user);
        saveToken(refreshToken, TokenType.BEARER, TokenMode.REFRESH, user);

        return AuthResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenEntity saveToken(String token, TokenType tokenType, TokenMode tokenMode, UserEntity userEntity) {
        return tokenRepository.saveAndFlush(TokenEntity.makeWithDefaults(token, tokenType, tokenMode, userEntity));
    }

    public int revokeAllUserTokens(String email){
        return tokenRepository.revokeAllTokensByUserEmail(email);
    }
}
