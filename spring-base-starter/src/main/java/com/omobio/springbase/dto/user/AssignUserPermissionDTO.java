package com.omobio.springbase.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class AssignUserPermissionDTO {
    @NotNull
    private UUID userId;
    @NotNull
    private List<UUID> permissionIds = new ArrayList<>();
}
