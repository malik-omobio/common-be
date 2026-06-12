package com.omobio.springbase.security;

import com.omobio.springbase.common.enums.UserStatus;
import com.omobio.springbase.config.redis.RedisService;
import com.omobio.springbase.model.PermissionEntity;
import com.omobio.springbase.model.User;
import com.omobio.springbase.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionLoaderService.class);

    private final UserRepository userRepository;
    private final RedisService redisService;

    @PostConstruct
    public void loadPermissionsOnStartup() {
        reloadAllActiveUserPermissions();
    }

    public void reloadAllActiveUserPermissions() {
        List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);

        if (activeUsers.isEmpty()) {
            logger.info("No active users found. Skipping permission caching.");
            return;
        }

        for (User user : activeUsers) {
            List<String> permissionKeys = user.getPermissions().stream()
                    .map(PermissionEntity::getKey)
                    .collect(Collectors.toList());
            redisService.cachePermissions(user.getId().toString(), permissionKeys);
            logger.info("Cached permissions for user {}: {}", user.getEmail(), permissionKeys);
        }
    }
}
