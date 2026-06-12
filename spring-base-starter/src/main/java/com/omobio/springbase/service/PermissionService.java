package com.omobio.springbase.service;

import com.omobio.springbase.dto.permission.ResponsePermissionListDTO;
import com.omobio.springbase.dto.role.AssignRolePermissionDTO;
import com.omobio.springbase.dto.user.AssignUserPermissionDTO;

import java.util.List;
import java.util.UUID;

public interface PermissionService {
    List<ResponsePermissionListDTO> findPermissionsByRoleId(UUID roleId);
    List<ResponsePermissionListDTO> findPermissionsByUserId(UUID userId);
    void assignRolePermission(AssignRolePermissionDTO dto);
    void assignUserPermission(AssignUserPermissionDTO dto);
}
