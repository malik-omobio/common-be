package com.omobio.springbase.service.impl;

import com.omobio.springbase.common.enums.UserStatus;
import com.omobio.springbase.common.exception.CustomException;
import com.omobio.springbase.dto.permission.ResponsePermissionDTO;
import com.omobio.springbase.dto.permission.ResponsePermissionListDTO;
import com.omobio.springbase.dto.role.AssignRolePermissionDTO;
import com.omobio.springbase.dto.user.AssignUserPermissionDTO;
import com.omobio.springbase.model.PermissionEntity;
import com.omobio.springbase.model.Role;
import com.omobio.springbase.model.User;
import com.omobio.springbase.repository.PermissionRepository;
import com.omobio.springbase.repository.RoleRepository;
import com.omobio.springbase.repository.UserRepository;
import com.omobio.springbase.security.PermissionLoaderService;
import com.omobio.springbase.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionLoaderService permissionLoaderService;

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePermissionListDTO> findPermissionsByRoleId(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new CustomException("Role not found", HttpStatus.NOT_FOUND));

        Set<UUID> assignedIds = role.getPermissions().stream()
                .map(PermissionEntity::getId)
                .collect(Collectors.toSet());

        return buildPermissionList(assignedIds::contains);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePermissionListDTO> findPermissionsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        Set<UUID> rolePermissionIds = user.getRole().getPermissions().stream()
                .map(PermissionEntity::getId)
                .collect(Collectors.toSet());
        Set<UUID> additionalPermissionIds = user.getPermissions().stream()
                .map(PermissionEntity::getId)
                .collect(Collectors.toSet());

        return buildUserPermissionList(rolePermissionIds, additionalPermissionIds);
    }

    @Override
    @Transactional
    public void assignRolePermission(AssignRolePermissionDTO dto) {
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new CustomException("Role not found", HttpStatus.NOT_FOUND));

        Set<PermissionEntity> oldRolePermissions = new HashSet<>(role.getPermissions());
        Set<PermissionEntity> newPermissions = resolvePermissions(dto.getPermissionIds());

        role.setPermissions(new HashSet<>(newPermissions));
        roleRepository.saveAndFlush(role);

        Set<UUID> newRolePermissionIds = newPermissions.stream()
                .map(PermissionEntity::getId)
                .collect(Collectors.toSet());

        List<User> usersWithRole = userRepository.findAllByRoleId(dto.getRoleId());
        for (User user : usersWithRole) {
            user.getPermissions().removeAll(oldRolePermissions);
            user.getPermissions().removeIf(p -> newRolePermissionIds.contains(p.getId()));
        }
        userRepository.saveAll(usersWithRole);
        permissionLoaderService.reloadAllActiveUserPermissions();
    }

    @Override
    @Transactional
    public void assignUserPermission(AssignUserPermissionDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new CustomException("User is not active", HttpStatus.FORBIDDEN);
        }

        Set<UUID> rolePermissionIds = user.getRole().getPermissions().stream()
                .map(PermissionEntity::getId)
                .collect(Collectors.toSet());

        Set<PermissionEntity> requested = resolvePermissions(dto.getPermissionIds());

        List<UUID> roleOnlyIds = requested.stream()
                .map(PermissionEntity::getId)
                .filter(rolePermissionIds::contains)
                .toList();
        if (!roleOnlyIds.isEmpty()) {
            throw new CustomException(
                    "Cannot assign role permissions at user level. Use role permission assignment instead.",
                    HttpStatus.BAD_REQUEST);
        }

        user.setPermissions(new HashSet<>(requested));
        userRepository.saveAndFlush(user);
        permissionLoaderService.reloadAllActiveUserPermissions();
    }

    private Set<PermissionEntity> resolvePermissions(List<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<PermissionEntity> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        if (permissions.size() != permissionIds.size()) {
            Set<UUID> foundIds = permissions.stream().map(PermissionEntity::getId).collect(Collectors.toSet());
            List<UUID> missing = permissionIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new CustomException("Permissions not found: " + missing, HttpStatus.NOT_FOUND);
        }
        return permissions;
    }

    private List<ResponsePermissionListDTO> buildPermissionList(
            java.util.function.Function<UUID, Boolean> hasPermissionFn) {
        Map<String, List<PermissionEntity>> byCategory = permissionRepository.findAll().stream()
                .collect(Collectors.groupingBy(PermissionEntity::getCategory));

        List<ResponsePermissionListDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<PermissionEntity>> entry : byCategory.entrySet()) {
            List<ResponsePermissionDTO> children = entry.getValue().stream()
                    .map(permission -> ResponsePermissionDTO.builder()
                            .id(permission.getId())
                            .displayName(permission.getDisplayName())
                            .key(permission.getKey())
                            .hasPermission(hasPermissionFn.apply(permission.getId()))
                            .fromRole(false)
                            .editable(true)
                            .build())
                    .sorted(Comparator.comparing(ResponsePermissionDTO::getKey))
                    .toList();

            boolean categoryHasPermission = children.stream()
                    .anyMatch(child -> Boolean.TRUE.equals(child.getHasPermission()));

            result.add(ResponsePermissionListDTO.builder()
                    .categoryName(entry.getKey())
                    .hasPermission(categoryHasPermission)
                    .children(children)
                    .build());
        }
        return result;
    }

    private List<ResponsePermissionListDTO> buildUserPermissionList(
            Set<UUID> rolePermissionIds,
            Set<UUID> additionalPermissionIds) {
        Map<String, List<PermissionEntity>> byCategory = permissionRepository.findAll().stream()
                .collect(Collectors.groupingBy(PermissionEntity::getCategory));

        List<ResponsePermissionListDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<PermissionEntity>> entry : byCategory.entrySet()) {
            List<ResponsePermissionDTO> children = entry.getValue().stream()
                    .map(permission -> {
                        boolean fromRole = rolePermissionIds.contains(permission.getId());
                        boolean hasAdditional = additionalPermissionIds.contains(permission.getId());
                        return ResponsePermissionDTO.builder()
                                .id(permission.getId())
                                .displayName(permission.getDisplayName())
                                .key(permission.getKey())
                                .hasPermission(fromRole || hasAdditional)
                                .fromRole(fromRole)
                                .hasAdditional(hasAdditional)
                                .editable(!fromRole)
                                .build();
                    })
                    .sorted(Comparator.comparing(ResponsePermissionDTO::getKey))
                    .toList();

            boolean categoryHasPermission = children.stream()
                    .anyMatch(child -> Boolean.TRUE.equals(child.getHasPermission()));

            result.add(ResponsePermissionListDTO.builder()
                    .categoryName(entry.getKey())
                    .hasPermission(categoryHasPermission)
                    .children(children)
                    .build());
        }
        return result;
    }
}
