package com.omobio.springbase.service.impl;

import com.omobio.springbase.common.enums.UserStatus;
import com.omobio.springbase.common.exception.CustomException;
import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.omobio.springbase.dto.user.CreateUserDTO;
import com.omobio.springbase.dto.user.FilterUserDTO;
import com.omobio.springbase.dto.user.ResponseUserDTO;
import com.omobio.springbase.dto.user.ResponseUserPermissionDTO;
import com.omobio.springbase.dto.user.UpdateUserDTO;
import com.omobio.springbase.model.PermissionEntity;
import com.omobio.springbase.model.Role;
import com.omobio.springbase.model.User;
import com.omobio.springbase.repository.RoleRepository;
import com.omobio.springbase.repository.UserRepository;
import com.omobio.springbase.security.PermissionLoaderService;
import com.omobio.springbase.service.PermissionService;
import com.omobio.springbase.service.UserService;
import com.omobio.springbase.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;
    private final PermissionLoaderService permissionLoaderService;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<String> getPermissionNamesForUser(User user) {
        Set<String> effective = new LinkedHashSet<>();
        user.getRole().getPermissions().forEach(p -> effective.add(p.getKey()));
        user.getPermissions().forEach(p -> effective.add(p.getKey()));
        return new ArrayList<>(effective);
    }

    @Override
    public ResponseUserDTO create(CreateUserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException("Email already exists", HttpStatus.CONFLICT);
        }

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new CustomException("Role not found", HttpStatus.NOT_FOUND));

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .status(UserStatus.ACTIVE)
                .permissions(new HashSet<>())
                .build();

        User saved = userRepository.save(user);
        permissionLoaderService.reloadAllActiveUserPermissions();
        return toResponse(saved);
    }

    @Override
    public ApiPaginatedResponse<ResponseUserDTO> findAll(FilterUserDTO filters) {
        Specification<User> spec = UserSpecification.withFilters(filters);
        Page<User> page = userRepository.findAll(spec, PageRequest.of(filters.getPage(), filters.getPerPage()));
        List<ResponseUserDTO> data = page.getContent().stream().map(this::toResponse).toList();
        return new ApiPaginatedResponse<>(data, new ApiPaginatedResponse.Pagination(
                page.getTotalElements(), page.getNumber(), page.getTotalPages(), page.hasNext()));
    }

    @Override
    public Object findById(UUID id, boolean withPermissions) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        if (!withPermissions) {
            return toResponse(user);
        }
        return ResponseUserPermissionDTO.builder()
                .user(toResponse(user))
                .permissions(permissionService.findPermissionsByUserId(id))
                .build();
    }

    @Override
    public ResponseUserDTO update(UUID id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }

        if (dto.getRoleId() != null && !dto.getRoleId().equals(user.getRole().getId())) {
            Role newRole = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new CustomException("Role not found", HttpStatus.NOT_FOUND));
            user.setRole(newRole);
            Set<UUID> rolePermissionIds = newRole.getPermissions().stream()
                    .map(PermissionEntity::getId)
                    .collect(Collectors.toSet());
            user.getPermissions().removeIf(p -> rolePermissionIds.contains(p.getId()));
        }

        User saved = userRepository.save(user);
        permissionLoaderService.reloadAllActiveUserPermissions();
        return toResponse(saved);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<SimpleGrantedAuthority> authorities = getPermissionNamesForUser(user).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities);
    }

    private ResponseUserDTO toResponse(User user) {
        return ResponseUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getName())
                .status(user.getStatus())
                .build();
    }
}
