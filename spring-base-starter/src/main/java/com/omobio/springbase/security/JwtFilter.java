package com.omobio.springbase.security;

import com.omobio.springbase.common.enums.UserStatus;
import com.omobio.springbase.common.exception.CustomException;
import com.omobio.springbase.config.redis.RedisService;
import com.omobio.springbase.model.User;
import com.omobio.springbase.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final UserService userService;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    @Value("${app.security.excluded-paths:}")
    private String[] extraExcludedPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(AUTH_HEADER);
            if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
                chain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(TOKEN_PREFIX.length());
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            Claims claims = jwtUtil.getClaimsFromToken(token);
            if (!"ACCESS".equals(claims.get("type", String.class))) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }

            String email = claims.get("email", String.class);
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new CustomException("User not found", HttpStatus.UNAUTHORIZED));

            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new CustomException("Account is inactive", HttpStatus.FORBIDDEN);
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(email);
                List<String> permissions = redisService.getPermissionsByUserId(user.getId().toString());
                List<SimpleGrantedAuthority> authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);
        } catch (CustomException ex) {
            response.sendError(ex.getStatus().value(), ex.getMessage());
        } catch (Exception ex) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> excluded = new ArrayList<>(SecurityConfig.CORE_EXCLUDED_PATHS);
        for (String path : extraExcludedPaths) {
            if (!path.isBlank()) {
                excluded.add(path.trim());
            }
        }
        AntPathMatcher matcher = new AntPathMatcher();
        return excluded.stream().anyMatch(path -> matcher.match(path, request.getRequestURI()));
    }

    public String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
