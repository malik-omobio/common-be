package com.omobio.springbase.service;

import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.omobio.springbase.dto.role.CreateRoleDTO;
import com.omobio.springbase.dto.role.FilterRoleDTO;
import com.omobio.springbase.dto.role.ResponseRoleDTO;
import com.omobio.springbase.dto.role.UpdateRoleDTO;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    ResponseRoleDTO create(CreateRoleDTO dto);
    ApiPaginatedResponse<ResponseRoleDTO> findAll(FilterRoleDTO filters);
    Object findById(UUID id, boolean withPermissions);
    List<ResponseRoleDTO> findAllForDropdown();
    ResponseRoleDTO update(UUID id, UpdateRoleDTO dto);
    void delete(UUID id);
}
