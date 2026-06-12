package com.omobio.springbase.controller.admin;

import com.omobio.springbase.common.constants.CorePermissions;
import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.omobio.springbase.common.response.ApiResponse;
import com.omobio.springbase.dto.role.CreateRoleDTO;
import com.omobio.springbase.dto.role.FilterRoleDTO;
import com.omobio.springbase.dto.role.ResponseRoleDTO;
import com.omobio.springbase.dto.role.UpdateRoleDTO;
import com.omobio.springbase.service.RoleService;
import com.omobio.springbase.util.abst.Guard;
import com.omobio.springbase.util.constants.Prefixes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("admin-role")
@RequestMapping(Prefixes.ADMIN_PATH + "/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Guard({CorePermissions.CREATE_ROLE})
    @PostMapping
    public ApiResponse<ResponseRoleDTO> create(@Valid @RequestBody CreateRoleDTO dto) {
        return new ApiResponse<>(roleService.create(dto), "Role created successfully");
    }

    @Guard({CorePermissions.VIEW_ROLE})
    @GetMapping
    public ApiPaginatedResponse<ResponseRoleDTO> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage) {
        return roleService.findAll(new FilterRoleDTO(name, page, perPage));
    }

    @Guard({CorePermissions.VIEW_ROLE_DROPDOWN})
    @GetMapping("/dropdown")
    public ApiResponse<List<ResponseRoleDTO>> findAllForDropdown() {
        return new ApiResponse<>(roleService.findAllForDropdown(), "Roles fetched successfully");
    }

    @Guard({CorePermissions.VIEW_ROLE})
    @GetMapping("/{id}")
    public ApiResponse<?> findById(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean withPermissions) {
        return new ApiResponse<>(roleService.findById(id, withPermissions), "Role fetched successfully");
    }

    @Guard({CorePermissions.UPDATE_ROLE})
    @PutMapping("/{id}")
    public ApiResponse<ResponseRoleDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleDTO dto) {
        return new ApiResponse<>(roleService.update(id, dto), "Role updated successfully");
    }

    @Guard({CorePermissions.DELETE_ROLE})
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return new ApiResponse<>("Role deleted successfully");
    }
}
