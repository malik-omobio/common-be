package com.omobio.springbase.database.seeder;

import com.omobio.springbase.model.PermissionEntity;
import com.omobio.springbase.model.Role;
import com.omobio.springbase.repository.PermissionRepository;
import com.omobio.springbase.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleSeeder {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role seedAdminRole() {
        Set<PermissionEntity> allPermissions = new HashSet<>(permissionRepository.findAll());
        return roleRepository.findByName("ADMIN").map(role -> {
            role.setPermissions(allPermissions);
            return roleRepository.save(role);
        }).orElseGet(() -> roleRepository.save(Role.builder()
                .name("ADMIN")
                .permissions(allPermissions)
                .build()));
    }
}
