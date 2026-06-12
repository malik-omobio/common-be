package com.omobio.springbase.service.impl;

import com.omobio.springbase.common.exception.CustomException;
import com.omobio.springbase.config.redis.RedisService;
import com.omobio.springbase.common.enums.UserStatus;
import com.omobio.springbase.dto.auth.GenerateTokenDTO;
import com.omobio.springbase.dto.auth.LoginRequestDTO;
import com.omobio.springbase.dto.auth.LoginResponseDTO;
import com.omobio.springbase.dto.auth.RefreshTokenRequestDTO;
import com.omobio.springbase.model.User;
import com.omobio.springbase.security.JwtUtil;
import com.omobio.springbase.service.AuthService;
import com.omobio.springbase.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new CustomException("Account is inactive", HttpStatus.FORBIDDEN);
        }

        List<String> permissions = userService.getPermissionNamesForUser(user);
        GenerateTokenDTO tokenData = new GenerateTokenDTO(
                user.getId(), user.getEmail(), user.getRole().getName());
        Map<String, String> tokens = jwtUtil.generateTokens(tokenData);
        redisService.cachePermissions(user.getId().toString(), permissions);

        return new LoginResponseDTO(
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
                "Bearer",
                permissions);
    }

    @Override
    public LoginResponseDTO refresh(RefreshTokenRequestDTO request) {
        Claims claims = jwtUtil.decode(request.getRefreshToken());

        if (!"REFRESH".equals(claims.get("type", String.class))) {
            throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new CustomException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findByEmail(claims.get("email", String.class))
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.UNAUTHORIZED));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new CustomException("Account is inactive", HttpStatus.FORBIDDEN);
        }

        blacklistTokenIfValid(request.getRefreshToken());

        GenerateTokenDTO tokenData = new GenerateTokenDTO(
                user.getId(), user.getEmail(), user.getRole().getName());
        List<String> permissions = userService.getPermissionNamesForUser(user);
        Map<String, String> tokens = jwtUtil.generateTokens(tokenData);
        redisService.cachePermissions(user.getId().toString(), permissions);

        return new LoginResponseDTO(
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
                "Bearer",
                permissions);
    }

    @Override
    public void logout(String token) {
        blacklistTokenIfValid(token);
    }

    private void blacklistTokenIfValid(String token) {
        Claims claims = jwtUtil.decode(token);
        String jti = claims.get("jti", String.class);
        long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (remaining > 0) {
            redisService.blacklistToken(jti, remaining);
        }
    }
}
