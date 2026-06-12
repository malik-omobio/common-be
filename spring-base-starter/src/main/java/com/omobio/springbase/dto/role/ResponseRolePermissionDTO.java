package com.omobio.springbase.dto.role;

import com.omobio.springbase.dto.permission.ResponsePermissionListDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseRolePermissionDTO {
    private ResponseRoleDTO role;
    private List<ResponsePermissionListDTO> permissions;
}
