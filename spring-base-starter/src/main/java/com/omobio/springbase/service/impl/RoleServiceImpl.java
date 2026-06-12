package com.omobio.springbase.service.impl;

import com.omobio.springbase.common.exception.CustomException;
import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.omobio.springbase.dto.role.CreateRoleDTO;
import com.omobio.springbase.dto.role.FilterRoleDTO;
import com.omobio.springbase.dto.role.ResponseRoleDTO;
import com.omobio.springbase.dto.role.ResponseRolePermissionDTO;
import com.omobio.springbase.model.Role;
import com.omobio.springbase.repository.RoleRepository;
import com.omobio.springbase.service.PermissionService;
import com.omobio.springbase.service.RoleService;
import com.omobio.springbase.specification.RoleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    @Override
    public ResponseRoleDTO create(CreateRoleDTO dto) {
        String roleName = dto.getName().trim().toUpperCase();
        roleRepository.findByName(roleName).ifPresent(role -> {
            throw new CustomException(roleName + " already exists", HttpStatus.CONFLICT);
        });

        Role saved = roleRepository.save(Role.builder().name(roleName).build());
        return toResponse(saved);
    }

    @Override
    public ApiPaginatedResponse<ResponseRoleDTO> findAll(FilterRoleDTO filters) {
        Specification<Role> spec = RoleSpecification.withFilters(filters);
        Page<Role> page = roleRepository.findAll(spec, PageRequest.of(filters.getPage(), filters.getPerPage()));
        List<ResponseRoleDTO> data = page.getContent().stream().map(this::toResponse).toList();
        return new ApiPaginatedResponse<>(data, new ApiPaginatedResponse.Pagination(
                page.getTotalElements(), page.getNumber(), page.getTotalPages(), page.hasNext()));
    }

    @Override
    public Object findById(UUID id, boolean withPermissions) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role not found", HttpStatus.NOT_FOUND));
        if (!withPermissions) {
            return toResponse(role);
        }
        return ResponseRolePermissionDTO.builder()
                .role(toResponse(role))
                .permissions(permissionService.findPermissionsByRoleId(id))
                .build();
    }

    @Override
    public List<ResponseRoleDTO> findAllForDropdown() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    private ResponseRoleDTO toResponse(Role role) {
        return ResponseRoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
