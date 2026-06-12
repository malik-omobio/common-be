package com.omobio.springbase.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsePermissionListDTO {
    private String categoryName;
    private Boolean hasPermission;
    private List<ResponsePermissionDTO> children;
}
