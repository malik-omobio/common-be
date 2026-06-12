package com.omobio.springbase.controller.admin;

import com.omobio.springbase.common.constants.CorePermissions;
import com.omobio.springbase.common.response.ApiResponse;
import com.omobio.springbase.dto.role.AssignRolePermissionDTO;
import com.omobio.springbase.dto.user.AssignUserPermissionDTO;
import com.omobio.springbase.service.PermissionService;
import com.omobio.springbase.util.abst.Guard;
import com.omobio.springbase.util.constants.Prefixes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("admin-permission")
@RequestMapping(Prefixes.ADMIN_PATH + "/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Guard({CorePermissions.ASSIGN_ROLE_PERMISSION})
    @PostMapping("/role-assign")
    public ApiResponse<Void> assignRolePermission(@Valid @RequestBody AssignRolePermissionDTO dto) {
        permissionService.assignRolePermission(dto);
        return new ApiResponse<>("Permission assigned successfully");
    }

    @Guard({CorePermissions.ASSIGN_USER_PERMISSION})
    @PostMapping("/user-assign")
    public ApiResponse<Void> assignUserPermission(@Valid @RequestBody AssignUserPermissionDTO dto) {
        permissionService.assignUserPermission(dto);
        return new ApiResponse<>("Permission assigned successfully");
    }
}
