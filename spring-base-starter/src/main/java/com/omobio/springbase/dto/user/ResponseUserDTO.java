package com.omobio.springbase.dto.user;

import com.omobio.springbase.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseUserDTO {
    private UUID id;
    private String email;
    private UUID roleId;
    private String roleName;
    private UserStatus status;
}
