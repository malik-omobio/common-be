package com.omobio.springbase.security;

import com.omobio.springbase.common.exception.CustomException;
import com.omobio.springbase.config.redis.RedisService;
import com.omobio.springbase.util.abst.Guard;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionGuardAspect {

    private final JwtFilter jwtFilter;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Around("@annotation(com.omobio.springbase.util.abst.Guard)")
    public Object checkPermissions(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Guard guard = signature.getMethod().getAnnotation(Guard.class);
        List<String> required = Arrays.asList(guard.value());

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attrs).getRequest();
        String token = jwtFilter.extractTokenFromHeader(request);
        Claims claims = jwtUtil.decode(token);

        if (claims == null || claims.getSubject() == null) {
            throw new CustomException("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        List<String> userPermissions = redisService.getPermissionsByUserId(claims.getSubject());
        boolean allowed = required.stream().anyMatch(userPermissions::contains);
        if (!allowed) {
            throw new CustomException("You do not have permission to perform this action", HttpStatus.FORBIDDEN);
        }

        return joinPoint.proceed();
    }
}
