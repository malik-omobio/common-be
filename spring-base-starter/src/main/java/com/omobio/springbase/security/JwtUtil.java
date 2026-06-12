package com.omobio.springbase.security;

import com.omobio.springbase.config.redis.RedisService;
import com.omobio.springbase.dto.auth.GenerateTokenDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expires}")
    private long accessTokenExpires;

    @Value("${jwt.refresh-expires}")
    private long refreshTokenExpires;

    private final RedisService redisService;
    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Map<String, String> generateTokens(GenerateTokenDTO data) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateToken(data, "ACCESS", accessTokenExpires));
        tokens.put("refreshToken", generateToken(data, "REFRESH", refreshTokenExpires));
        return tokens;
    }

    private String generateToken(GenerateTokenDTO data, String tokenType, long expirationMillis) {
        long now = System.currentTimeMillis();
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .subject(data.getSub().toString())
                .claim("email", data.getEmail())
                .claim("role", data.getRole())
                .claim("type", tokenType)
                .claim("jti", jti)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMillis))
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String jti = claims.get("jti", String.class);
            return !redisService.isTokenBlacklisted(jti);
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims decode(String token) {
        return parseClaims(token);
    }

    public Claims getClaimsFromToken(String token) {
        return parseClaims(token);
    }

    public long getRefreshExpires() {
        return refreshTokenExpires;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
