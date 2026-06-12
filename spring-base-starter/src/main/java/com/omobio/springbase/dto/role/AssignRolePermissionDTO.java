package com.omobio.springbase.dto.role;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignRolePermissionDTO {
    @NotNull
    private UUID roleId;
    @NotEmpty
    private List<UUID> permissionIds;
}
