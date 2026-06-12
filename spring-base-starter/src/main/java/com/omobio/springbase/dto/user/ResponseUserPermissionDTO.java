package com.omobio.springbase.dto.user;

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
public class ResponseUserPermissionDTO {
    private ResponseUserDTO user;
    private List<ResponsePermissionListDTO> permissions;
}
