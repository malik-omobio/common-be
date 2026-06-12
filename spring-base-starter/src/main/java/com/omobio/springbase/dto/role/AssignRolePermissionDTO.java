package com.omobio.springbase.dto.role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class AssignRolePermissionDTO {
    @NotNull
    private UUID roleId;
    @NotNull
    private List<UUID> permissionIds = new ArrayList<>();
}
