package com.omobio.springbase.database.seeder;

import com.omobio.springbase.common.enums.UserStatus;
import com.omobio.springbase.model.Role;
import com.omobio.springbase.model.User;
import com.omobio.springbase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class UserSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.admin.email}")
    private String adminEmail;

    @Value("${seed.admin.password}")
    private String adminPassword;

    public void seedAdmin(Role adminRole) {
        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                user -> {
                    user.setStatus(UserStatus.ACTIVE);
                    user.setRole(adminRole);
                    user.getPermissions().removeAll(adminRole.getPermissions());
                    userRepository.save(user);
                },
                () -> userRepository.save(User.builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(adminRole)
                        .status(UserStatus.ACTIVE)
                        .permissions(new HashSet<>())
                        .build())
        );
    }
}
