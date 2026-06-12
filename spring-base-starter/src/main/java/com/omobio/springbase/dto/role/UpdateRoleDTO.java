package com.omobio.springbase.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRoleDTO {
    @NotBlank
    private String name;
}
